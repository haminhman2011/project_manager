package mh.manager;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import mh.manager.adapter.OpenAdapter;
import mh.manager.lang.SharedPrefControl;
import mh.manager.models.ModelOpen;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpenFragment extends Fragment {

    public HostApi hostApi;
    private LoginDatabase sql;
    private final static String url_page = "get-list-ticket?state=open";
    private final static String url_staffId = "&staffId=";
    private final static String url_token = "&token=";
    private final static String url_agentId = "&agentId=";
    public String staffId, agentId, token;

    public int pageCount = 0;
    public OpenAdapter adapter;
    private ListView listView;
    public ProgressDialog dialog;
    private ArrayList<ModelOpen> dataOpens;
    private EditText edtSearch;
    private Button btnSearchOpen, refreshButton;


    public OpenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =   inflater.inflate(R.layout.fragment_open, container, false);
        SharedPrefControl.updateLangua(getContext());

        listView = (ListView) myView.findViewById(R.id.lvOpen);
        btnSearchOpen = (Button) myView.findViewById(R.id.btnSearchOpen);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(onItemClick);
        hostApi = new HostApi();

        sql = new LoginDatabase(getActivity());
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

        refreshButton= (Button)myView.findViewById(R.id.refreshButtonOpen);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    dataOpens = new ArrayList<>();
                    adapter = new OpenAdapter(getActivity(), R.layout.item_listview_open, dataOpens);
                    listView.setAdapter(adapter);
                    getDataFromUrl(hostApi.hostApi+url_page+url_token+token+url_agentId+agentId+url_staffId+staffId); //
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getContext(), getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtSearch = (EditText) myView.findViewById(R.id.edtSearchOpen);
        btnSearchOpen.setOnClickListener(searchOpen);
        return  myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       Log.i("test===>",hostApi.hostApi+url_page+url_token+token+url_agentId+agentId+url_staffId+staffId );
        if(isOnline()) {
            setListViewAdapter();
            getDataFromUrl(hostApi.hostApi + url_page + url_token + token + url_agentId + agentId + url_staffId + staffId); //
        }
    }

    // su kiem lick vao moi item hien detail cua item do sang activity khac
    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isOnline()){
            ModelOpen data = adapter.getItem(position);
            Intent detail = new Intent(getActivity(), DetailOpenActivity.class);
            detail.putExtra("Item", data);
            startActivityForResult(detail, 10);
            Toast.makeText(getActivity(), data.getNumber(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
        }
        }
    };

    // buTTon search data listview
    private View.OnClickListener searchOpen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isOnline()){
//                if(listView.getCount() > 0){
                    adapter.clear();
                    String strUrl = hostApi.hostApi+url_page+url_token+token+url_staffId+staffId+url_agentId+agentId+"&query="; //
                    String strTextSearch = edtSearch.getText().toString();
                    Log.i("api search===>", strUrl+strTextSearch);
                    getDataSearchFromUrl(strUrl+strTextSearch);
//                }

            }else{
                Toast.makeText(getContext(), getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
            }

        }
    };



    // load all data list view
    public void setListViewAdapter() {
        dataOpens = new ArrayList<>();
        Log.i("testman", String.valueOf(dataOpens));
        adapter = new OpenAdapter((Activity) getContext(), R.layout.item_listview_open, dataOpens);
        listView.setAdapter(adapter);
    }

    // calling asynctask to get json data from internet
    public void getDataFromUrl(String url) {
        new LoadDataServerOpen(getActivity(), url).execute();
    }

    public void getDataSearchFromUrl(String url) {
        new LoadDataServerOpen(getActivity(), url).execute();
    }
    //parsing json after getting from Internet
    public void parseJsonResponse(String result) {
//        Log.i("111","111");
//        Log.i("jsonjsonjson==>", result);
        pageCount++;
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));
            Log.i("datatrave==.", String.valueOf(jArray));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                ModelOpen data = new ModelOpen();
                data.setTicket_id(jObject.getString("ticket_id"));
                data.setNumber(!jObject.getString("number").equals("null") ? jObject.getString("number") : "");
                data.setLastupdate(!jObject.getString("lastupdate").equals("null") ? jObject.getString("lastupdate") : "");
                data.setStatus(!jObject.getString("status").equals("null") ? jObject.getString("status") : "");
                data.setTopicname(!jObject.getString("topicname").equals("null") ? jObject.getString("topicname") : "");
                data.setCreated(!jObject.getString("created").equals("null") ? jObject.getString("created") : "");
                data.setPriority(!jObject.getString("priority").equals("null") ? jObject.getString("priority") : "");
                data.setUsername(!jObject.getString("username").equals("null") ? jObject.getString("username") : "");
                data.setEmail(!jObject.getString("email").equals("null") ? jObject.getString("email") : "");
                data.setDepartment(!jObject.getString("department").equals("null") ? jObject.getString("department") : "");
                data.setDepartmentId(!jObject.getString("departmentId").equals("null") ? jObject.getString("departmentId") : "");
                data.setSource(!jObject.getString("source").equals("null") ? jObject.getString("source") : "");
                data.setSlaname(!jObject.getString("slaname").equals("null") ? jObject.getString("slaname"): "");
                data.setLast_message(!jObject.getString("lastmessage").equals("null") ? jObject.getString("lastmessage") : "");
                data.setLast_reponse(!jObject.getString("lastresponse").equals("null") ? jObject.getString("lastresponse") : "");
                data.setEst_duedate(!jObject.getString("est_duedate").equals("null") ? jObject.getString("est_duedate") : "");
                data.setSubject(!jObject.getString("subject").equals("null") ? jObject.getString("subject") : "");
                data.setTeamName(!jObject.getString("teamName").equals("null") ? jObject.getString("teamName") : "");
                data.setHotel(!jObject.getString("hotel").equals("null") ? jObject.getString("hotel") : "");
                data.setRoom(!jObject.getString("room").equals("null") ? jObject.getString("room") : "");
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

    public class LoadDataServerOpen extends AsyncTask<Void, Void, String> {
        private Context context;
        private String url;
        private ProgressDialog dialog;
        public JSONObject json = null;
        public LoadDataServerOpen(Context context, String url) {
            super();
            this.context = context;
            this.url = url;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setTitle(getString(R.string.processing));
            dialog.setMessage(getString(R.string.processing));
            dialog.setIndeterminate(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            // call load JSON from url method

//            Log.i("jsonjsonjso==>", loadJSON(this.url).toString());

            return loadJSON(this.url).toString();
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            Log.i("222","222");
                Log.i("ultresultresult==>", String.valueOf(result));
            dialog.dismiss();
            parseJsonResponse(result);
        }

        public JSONObject loadJSON(String url) {
            // Creating JSON Parser instance
            Log.i("url loi", url);
            Log.i("loi======>", String.valueOf(json));
            JSONParser jParser = new JSONParser();
            json = jParser.getJSONFromUrl(url);
            return json;

            // getting JSON string from URL

//            try{
//
//            }catch (Exception e){
//                Log.i("loi tra ve 2", e.getMessage());
//                e.printStackTrace();
//            }
//            return null;
        }

        private class JSONParser {
            private InputStream is = null;
            private JSONObject jObj = null;
            private String json = "";
            // constructor
            public JSONParser() {

            }
            public JSONObject getJSONFromUrl(String url) {
                // Making HTTP request
                try {
                    // defaultHttpClient
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),
                            8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
                // return JSON String
                return jObj;
            }
        }
    }

    /**
     * kiem tra co ket noi voi mạng không
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
