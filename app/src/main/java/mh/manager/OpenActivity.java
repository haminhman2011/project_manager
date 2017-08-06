package mh.manager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mh.manager.adapter.OpenAdapter;
import mh.manager.asynctask.LoadDataServerFromURlTaskOpen;
import mh.manager.models.ModelOpen;

public class OpenActivity extends AppCompatActivity {

    public HostApi hostApi;
    private LoginDatabase sql;
    private final static String url_page = "get-list-ticket?state=open";
    private final static String url_staffId = "&staffId=";
    private final static String url_token = "&token=";
    private final static String url_agentId = "&agentId=";
    private final static String TAG = OpenActivity.class.getSimpleName();
    public String staffId, agentId, token;

    public int pageCount = 0;
    public OpenAdapter adapter;
    private ListView listView;
    public ProgressDialog dialog;
    private ArrayList<ModelOpen> dataOpens;
    private EditText edtSearch;
    private Button btnSearchOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        listView = (ListView) findViewById(R.id.lvOpen);
        btnSearchOpen = (Button) findViewById(R.id.btnSearchOpen);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(onItemClick);
        hostApi = new HostApi();

        sql = new LoginDatabase(this);
        sql.getWritableDatabase();
        try {
            for(int i=0; i<sql.getInforUser().length(); i++){
                JSONObject obj = sql.getInforUser().getJSONObject(i);
                staffId = obj.getString("id");
                agentId = obj.getString("id");
                token = obj.getString("token");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button refreshButton= (Button)findViewById(R.id.refreshButtonOpen);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOpens = new ArrayList<>();
                adapter = new OpenAdapter(OpenActivity.this, R.layout.item_listview_open, dataOpens);
                listView.setAdapter(adapter);
                getDataFromUrl(hostApi.hostApi+url_page+url_token+token+url_agentId+agentId); //+url_staffId+staffId
                adapter.notifyDataSetChanged();
            }
        });
        edtSearch = (EditText) findViewById(R.id.edtSearchOpen);
        btnSearchOpen.setOnClickListener(searchOpen);

        Log.i("tocken api=====>", hostApi.hostApi+url_page+url_token+token+url_agentId+agentId); // +url_staffId+staffId
        setListViewAdapter();
        getDataFromUrl(hostApi.hostApi+url_page+url_token+token+url_agentId+agentId); //+url_staffId+staffId

    }

    // su kiem lick vao moi item hien detail cua item do sang activity khac
    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ModelOpen data = adapter.getItem(position);
            Intent detail = new Intent(OpenActivity.this, DetailOpenActivity.class);
            detail.putExtra("Item", data);
            startActivityForResult(detail, 10);
            Toast.makeText(OpenActivity.this, data.getNumber(), Toast.LENGTH_SHORT).show();
        }
    };

    // buTTon search data listview
    private View.OnClickListener searchOpen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.clear();
            String strUrl = hostApi.hostApi+url_page+url_token+token+url_agentId+agentId+"&ticketNumber="; //+url_staffId+staffId
            String strTextSearch = edtSearch.getText().toString();
            Log.i("api search===>", strUrl+strTextSearch);
            getDataSearchFromUrl(strUrl+strTextSearch);
        }
    };

    // load all data list view
    private void setListViewAdapter() {
        dataOpens = new ArrayList<>();
        adapter = new OpenAdapter(this, R.layout.item_listview_open, dataOpens);

        listView.setAdapter(adapter);
    }

    // calling asynctask to get json data from internet
    private void getDataFromUrl(String url) {
        new LoadDataServerFromURlTaskOpen(this, url).execute();
    }

    private void getDataSearchFromUrl(String url) {
        new LoadDataServerFromURlTaskOpen(this, url).execute();
    }

    //parsing json after getting from Internet
    public void parseJsonResponse(String result) {
//        Log.i("jsonjsonjsonjsonjsonjson==>", result);
        Log.i(TAG, result);
        pageCount++;
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                ModelOpen data = new ModelOpen();
                data.setTicket_id(jObject.getString("ticket_id"));
                data.setNumber(!jObject.getString("number").equals("null") ? jObject.getString("number") : "");
                data.setPriority(!jObject.getString("priority").equals("null") ? jObject.getString("priority") : "");
                data.setUsername(!jObject.getString("username").equals("null") ? jObject.getString("username") : "");
                data.setEmail(!jObject.getString("email").equals("null") ? jObject.getString("email") : "");
                data.setDepartment(!jObject.getString("department").equals("null") ? jObject.getString("department") : "");
                data.setDepartmentId(!jObject.getString("departmentId").equals("null") ? jObject.getString("departmentId") : "");
                data.setSource(!jObject.getString("source").equals("null") ? jObject.getString("source") : "");
                data.setTopicname(!jObject.getString("topicname").equals("null") ? jObject.getString("topicname") : "");
                data.setSlaname(!jObject.getString("slaname").equals("null") ? jObject.getString("slaname"): "");
                data.setCreated(!jObject.getString("created").equals("null") ? jObject.getString("created") : "");
                data.setLast_message(!jObject.getString("lastmessage").equals("null") ? jObject.getString("lastmessage") : "");
                data.setLast_reponse(!jObject.getString("lastresponse").equals("null") ? jObject.getString("lastresponse") : "");
                data.setLastupdate(!jObject.getString("lastupdate").equals("null") ? jObject.getString("lastupdate") : "");
                data.setStatus(!jObject.getString("status").equals("null") ? jObject.getString("status") : "");
                data.setEst_duedate(!jObject.getString("est_duedate").equals("null") ? jObject.getString("est_duedate") : "");
                data.setSubject(!jObject.getString("subject").equals("null") ? jObject.getString("subject") : "");
                data.setTeamName(!jObject.getString("teamName").equals("null") ? jObject.getString("teamName") : "");
                dataOpens.add(data);
            }
            adapter.notifyDataSetChanged();
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Log.i("===BACK BUTTON PRESSED===", "BACK BUTTON");
            return true;
        } else {
//            Log.i("===ELSE BACK BUTTON PRESSED===", "ELSE BACK BUTTON");
        }
        return super.onKeyDown(keyCode, event);
    }



}
