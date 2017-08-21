package mh.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

import mh.manager.adapter.DynamicDetailOpenAdapter;
import mh.manager.asynctask.CallUrlUpdateDetail;
import mh.manager.asynctask.LoadDataServerFromURIDetailNotification;
import mh.manager.asynctask.LoadDataServerFromURITaskDetailOpen;
import mh.manager.asynctask.LoadDataServerTicketTheadNoti;
import mh.manager.dialog.AgentActivity;
import mh.manager.dialog.TransferActivity;
import mh.manager.lang.SharedPrefControl;
import mh.manager.models.ModelDynamicDetailOpen;

public class DetailNotiActivity extends AppCompatActivity implements View.OnClickListener{

    public HostApi hostApi;
    private LoginDatabase sql;

    private final static String url_page = "get-ticket-detail?ticketNumber=";
    private final static String url_staffId = "&staffId=";
    private final static String url_token = "&token=";
    private final static String url_agentId = "&agentId=";
    private final static String url_status = "get-list-ticket-status?token=";

    private final static String TAG = DetailOpenActivity.class.getSimpleName();

    public TextView tvStatusUpdate, tvSticket, tvStatus, tvPriority, tvDepartment, tvCreatedDate, tvEmail, tvAssigned, tvDueDate, tvHelpTopic;
    private Button btnBack, btnUpdateOpen, btnChangeTeam, btnChangeStatus, btnAssign, btnTransfer, btnEditTicket, btnCancel;
    public String ticketNumber, ticketId, staffId, agentId, token, email, status, userName, departmentId, departmentName, nameStatus;
    public EditText strNote;
    public Spinner spnChangeTeam, spnChangeStatus, spTicketStatus, spnAgent;

    public int pageCount = 0;
    public ListView lvDynamic, lvTicketThread;
    public DynamicDetailOpenAdapter adapter;
    public ProgressDialog dialog;
    public ArrayList<ModelDynamicDetailOpen> modelDynamicDetailOpens;
    public  LinearLayout.LayoutParams layoutParams;
    public LinearLayout llEntry, llLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_noti);
        SharedPrefControl.updateLangua(getApplicationContext());

        tvSticket       = (TextView) findViewById(R.id.tvSticketOpen);
        tvStatus        = (TextView) findViewById(R.id.tvStatusOPen);
        tvPriority      = (TextView) findViewById(R.id.tvPriorityOpen);
        tvDepartment    = (TextView) findViewById(R.id.tvDepartmentOpen);
        tvCreatedDate   = (TextView) findViewById(R.id.tvCreatedDateOpen);
        tvEmail         = (TextView) findViewById(R.id.tvEmailOpen);
        tvAssigned      = (TextView) findViewById(R.id.tvAssignedToOpen);
        tvDueDate       = (TextView) findViewById(R.id.tvDueDateOpen);
        tvHelpTopic     = (TextView) findViewById(R.id.tvHelpTopicOpen);
        tvStatusUpdate  = (TextView) findViewById(R.id.idStatusDetailOPen);
        strNote = (EditText) findViewById(R.id.edtNoteOPen);
        llEntry = (LinearLayout) findViewById(R.id.llEntry);
        llLeft = (LinearLayout) findViewById(R.id.llLeft);

        getDataFromUrl("http://demo.cloudteam.vn:8080/cocobay_manager/api/get-list-ticket?3&token=ieEpY4LZ2nh_poAutA7WdWwq1u79bNm2&staffId=3&agentId=3&ticketNumber=156293"); //

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         * Xử lý data dynamic
         * get host api data
         * get token database
         */
        //
        sql = new LoginDatabase(this);
        sql.getWritableDatabase();
        try {
            for(int i=0; i<sql.getInforUser().length(); i++){
                JSONObject obj = sql.getInforUser().getJSONObject(i);
                staffId = obj.getString("id");
                agentId = obj.getString("id");
                token = obj.getString("token");
                email = obj.getString("email");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        hostApi = new HostApi();
        lvDynamic = (ListView) findViewById(R.id.lvDynamicDataOpen);
        layoutParams = (LinearLayout.LayoutParams) lvDynamic.getLayoutParams();
        setListViewAdapter();

        Log.i("link host==>", hostApi.hostApi+url_page +"-----"+ "156293"+"-----"+ staffId+"-----"+ token+"-----"+ agentId);
        // load data dynamic

        new LoadDataServerFromURIDetailNotification(DetailNotiActivity.this,hostApi.hostApi+url_page, "156293", staffId, token, agentId).execute();
//        Log.i("222222==>", status);

        // cap nhat data len server
        btnUpdateOpen = (Button) findViewById(R.id.btnUpdateOpen);
        btnUpdateOpen.setOnClickListener(this);

        spTicketStatus = (Spinner) findViewById(R.id.spinnerStatus);
        ArrayAdapter<String> ticketStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item ,sql.getListStatus());
        spTicketStatus.setAdapter(ticketStatusAdapter); // Pending
//        for(int i=0; i < ticketStatusAdapter.getCount(); i++) {
//            if(status.trim().equals(ticketStatusAdapter.getItem(i).toString())){
//                spTicketStatus.setSelection(i);
//                break;
//            }
//        }
        spTicketStatus.setOnItemSelectedListener(spClickItemTicketStatus);

        spnChangeTeam = (Spinner) findViewById(R.id.spnChangeTeam);
        String[] dataAssign={"Change..","Agent","Team"};
        ArrayAdapter<String> changeTeamAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,dataAssign);
        changeTeamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnChangeTeam.setAdapter(changeTeamAdapter);

        btnAssign = (Button) findViewById(R.id.btnAssign);
        btnAssign.setOnClickListener(onClickAssign);

        // xử lý ticket thread chat, load data auto
        getDataTickedThreadUrl(hostApi.hostApi+"get-all-thread?ticketNumber="+"156293");//ticketNumber
        Log.i("host api thres===>", hostApi.hostApi+"get-all-thread?ticketNumber="+"156293");

        // transfer
        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(onClickTransfer);

        //edit detail ticket
        btnEditTicket = (Button) findViewById(R.id.btnEditTicket);
        btnEditTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEdit = new Intent(getBaseContext(), EditDetailOpenActivity.class);

                startActivity(intentEdit);
                finish();
            }
        });


    }

    /**
     * Xử lý khi click chon item cua spinner ticket status se show ra du nao can thiet de gang
     */
    private AdapterView.OnItemSelectedListener spClickItemTicketStatus = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            TextView idStatus = (TextView) findViewById(R.id.idStatusDetailOPen);
            idStatus.setVisibility(View.INVISIBLE);
            nameStatus = spTicketStatus.getSelectedItem().toString();
            idStatus.setText(sql.getIdStatus(spTicketStatus.getSelectedItem().toString()));
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * Show dialog team
     */
    private View.OnClickListener onClickAssign = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String nameTeam = spnChangeTeam.getSelectedItem().toString();
            if(!nameTeam.equals("Change..")){
                if(isOnline()){
                    if(nameTeam.equals("Agent")){
                        Intent dataAgent = new Intent(getBaseContext(), AgentActivity.class);
                        dataAgent.putExtra("departmentId", departmentId);
                        dataAgent.putExtra("nameTeam", nameTeam);
                        dataAgent.putExtra("ticketId", ticketId);
                        startActivity(dataAgent);
                    }else if(nameTeam.equals("Team")){
                        Toast.makeText(getBaseContext(), "Feature is being perfected", Toast.LENGTH_SHORT).show();
//                    Intent dataTeam = new Intent(DetailOpenActivity.this, TeamActivity.class);
//                    dataTeam.putExtra("nameTeam", nameTeam);
//                    startActivity(dataTeam);
                    }
                }else{
                    Toast.makeText(getBaseContext(), getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getBaseContext(), "Vui lòng chọn!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * onClickTransfer
     */
    private View.OnClickListener onClickTransfer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isOnline()){
                Intent data = new Intent(getBaseContext(), TransferActivity.class);
                data.putExtra("departmentName", departmentName);
                data.putExtra("ticketId", ticketId);
                data.putExtra("staffId", staffId);
                startActivity(data);
            }else{
                Toast.makeText(getBaseContext(), getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
            }
        }
    };

    // calling asynctask to get json data from internet
    private void getDataTickedThreadUrl(String url) {
        new LoadDataServerTicketTheadNoti(this, url).execute();
    }
    // xử lý load thread động
    public void parseJsonResponseTickedThread(String result) {
        pageCount++;
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));
//            Log.i("h 1111===>", String.valueOf(json));
            TextView tvPoster, tvMessageText, tvCreated, tvBody;
            if(jArray.length() == 1){
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject objDatas = jArray.getJSONObject(i);
                    JSONArray arrEntry = objDatas.getJSONArray("entry");
                    JSONArray arrEvents = objDatas.getJSONArray("events");
                    for(int j = 0; j < arrEntry.length(); j++) {
                        JSONObject objEntry = arrEntry.getJSONObject(j);

                        llEntry.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        llEntry.setBackgroundResource(R.drawable.rounded_corner);
                        lp.setMargins(15,5,15,0);
                        llEntry.setLayoutParams(lp);

                        tvPoster = new TextView(this);
                        tvPoster.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvPoster.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                        tvPoster.setTextSize(15);
                        tvPoster.setTypeface(null, Typeface.BOLD);
                        tvPoster.setPadding(15,0,0,0);
                        tvPoster.setTextColor(Color.parseColor("#616161"));
                        tvPoster.setText(Html.fromHtml(objEntry.getString("poster")));
                        llEntry.addView(tvPoster);

                        tvCreated = new TextView(this);
                        tvCreated.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvCreated.setTextSize(13);
                        tvCreated.setPadding(15,0,15,0);
                        tvCreated.setText(" posted "+ objEntry.getString("created")+ " "+ objEntry.getString("title"));
                        llEntry.addView(tvCreated);

                        tvBody = new TextView(this);
                        LinearLayout.LayoutParams llpBody = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        llpBody.setMargins(10,8,10,8);
                        tvBody.setLayoutParams(llpBody);
                        tvBody.setTextSize(15);
                        tvBody.setBackgroundResource(R.color.btnColor);
                        tvBody.setPadding(15,15,15,15);
                        tvBody.setText(Html.fromHtml(objEntry.getString("body")));
                        llEntry.addView(tvBody);

                        for(int e = 0; e < arrEvents.length(); e++){
                            JSONObject objEvents = arrEvents.getJSONObject(e);
                            tvMessageText = new TextView(this);
                            tvMessageText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvMessageText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                            tvMessageText.setTextSize(15);
                            tvMessageText.setPadding(25,0,0,0);
                            tvMessageText.setBackgroundResource(R.color.btnColor);
//                            tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mess_16,0,0,0);
                            tvMessageText.setTextColor(Color.parseColor("#616161"));
                            if(!objEvents.getString("messageText").equals("empty")){
                                tvMessageText.setText(" " + Html.fromHtml(objEvents.getString("messageText"))); //
                                llEntry.addView(tvMessageText);
                            }
                        }
                    }
                }
            }else if(jArray.length() > 1){
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject objDatas = jArray.getJSONObject(i);
                    if(objDatas.isNull("left")) {
                        JSONArray arrEntry = objDatas.getJSONArray("entry");
                        for(int j = 0; j < arrEntry.length(); j++) {
                            JSONObject objEntry = arrEntry.getJSONObject(j);
                            llEntry.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llEntry.setBackgroundResource(R.drawable.rounded_corner);
                            lp.setMargins(15,5,15,0);
                            llEntry.setLayoutParams(lp);

                            JSONArray arrEvents = objDatas.getJSONArray("events");
                            for(int e = 0; e < arrEvents.length(); e++){
                                JSONObject objEvents = arrEvents.getJSONObject(e);
                                if(!objEvents.getString("messageText").equals("empty")){
                                    tvMessageText = new TextView(this);
                                    tvMessageText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvMessageText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                                    tvMessageText.setTextSize(15);
                                    tvMessageText.setPadding(25,0,0,0);
                                    tvMessageText.setBackgroundResource(R.color.btnColor);
//                                    tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mess_16,0,0,0);
                                    tvMessageText.setTextColor(Color.parseColor("#616161"));
                                    tvMessageText.setText(" " + Html.fromHtml(objEvents.getString("messageText")));
                                    llEntry.addView(tvMessageText);
                                }
                            }

                            tvPoster = new TextView(this);
                            tvPoster.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvPoster.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                            tvPoster.setTextSize(15);
                            tvPoster.setTypeface(null, Typeface.BOLD);
                            tvPoster.setPadding(15,0,0,0);
                            tvPoster.setTextColor(Color.parseColor("#616161"));
                            tvPoster.setText(Html.fromHtml(objEntry.getString("poster")));
                            llEntry.addView(tvPoster);

                            tvCreated = new TextView(this);
                            tvCreated.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvCreated.setTextSize(13);
                            tvCreated.setPadding(15,0,15,0);
                            tvCreated.setText(" posted "+ objEntry.getString("created")+ " "+ Html.fromHtml(objEntry.getString("title")));
                            llEntry.addView(tvCreated);

                            tvBody = new TextView(this);
                            LinearLayout.LayoutParams llpBody = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llpBody.setMargins(10,8,10,8);
                            tvBody.setLayoutParams(llpBody);
                            tvBody.setTextSize(15);
                            tvBody.setBackgroundResource(R.color.btnColor);
                            tvBody.setPadding(15,15,15,15);
                            tvBody.setText(Html.fromHtml(objEntry.getString("body")));
                            llEntry.addView(tvBody);
                        }
                    }else{
                        JSONArray arrLeft = objDatas.getJSONArray("left");

                        for(int l = 0; l < arrLeft.length(); l++){
//                            llLeft = (LinearLayout) findViewById(R.id.llLeft);
                            llLeft.setOrientation(LinearLayout.VERTICAL);
                            JSONObject objLeft= arrLeft.getJSONObject(l);
                            TextView tvMssLeft  = new TextView(this);
                            tvMssLeft.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvMssLeft.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                            tvMssLeft.setTextSize(15);
                            tvMssLeft.setPadding(20,0,0,0);
                            tvMssLeft.setBackgroundResource(R.color.btnColor);
//                            tvMssLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mess_16,0,0,0);
                            tvMssLeft.setTextColor(Color.parseColor("#616161"));
                            tvMssLeft.setText(" " + Html.fromHtml(objLeft.getString("messageText")));
                            llLeft.addView(tvMssLeft);
                        }
                    }
                }
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xu ly update data server
     */
    @Override
    public void onClick(View v) {
        if(isOnline()){
            String value, strStatus, tvEntryId, tvFieldId, strNotes;
            strStatus = (String) tvStatusUpdate.getText();
            strNotes = String.valueOf(strNote.getText());
            LocaLIpAddress locaLIpAddress = new LocaLIpAddress();
            View parentView = null;
            JSONArray jsonArray = new JSONArray();
            try {
                for (int i = 0; i < lvDynamic.getCount(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    parentView = getViewByPosition(i, lvDynamic);
                    value = ((TextView)parentView.findViewById(R.id.edtNameDetailOpen)).getText().toString();
                    tvEntryId = ((TextView)parentView.findViewById(R.id.tvEntryId)).getText().toString();
                    tvFieldId = ((TextView)parentView.findViewById(R.id.tvFieldId)).getText().toString();
                    jsonObject.put("value", value);
                    jsonObject.put("entry_id", tvEntryId);
                    jsonObject.put("field_id", tvFieldId);
                    jsonArray.put(jsonObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Tạo mới một lớp CallUrl
            CallUrlUpdateDetail wst = new CallUrlUpdateDetail(CallUrlUpdateDetail.POST_TASK, this, "Checking...");
            CallUrlUpdateDetail wstDetail = new CallUrlUpdateDetail(CallUrlUpdateDetail.POST_TASK, this, "Checking...");
            CallUrlUpdateDetail wstThreadEntry = new CallUrlUpdateDetail(CallUrlUpdateDetail.POST_TASK, this, "Checking...");

            wst.addNameValuePair("status",strStatus);
            wst.addNameValuePair("ticketId",ticketId);
            wst.addNameValuePair("token",token);
            wst.addNameValuePair("agentId",agentId);
            wst.addNameValuePair("staffId",staffId);

            wstDetail.addNameValuePair("details", String.valueOf("{\"datas\":"+jsonArray+"}"));

            wstThreadEntry.addNameValuePair("ticketId", ticketId);
            wstThreadEntry.addNameValuePair("email", email);
            wstThreadEntry.addNameValuePair("body", strNotes);
            wstThreadEntry.addNameValuePair("ipAddress", locaLIpAddress.getLocalIpAddress());

            // Đường dẫn đến server
            wstDetail.execute(new String[] { hostApi.hostApi+"update-ticket-detail"});

            //1 . nếu reply co data > 0 và status giong nhau
            if(((strNotes.trim()).length() > 0 &&  nameStatus.equals(status))){
                Log.i("vao", "nếu reply co data > 0 và status giong nhau");
                wstThreadEntry.execute(new String[] { hostApi.hostApi+"create-thread-entry"});
                llEntry.removeAllViews();
                llLeft.removeAllViews();
                strNote.setText("");
                getDataTickedThreadUrl(hostApi.hostApi+"get-all-thread?ticketNumber="+ticketNumber);
            }else if(((strNotes.trim()).length() == 0 &&  !nameStatus.equals(status))){
                //2. nếu reply co data == 0 và status khac nhau
                Log.i("vao", "nếu reply co data == 0 và status khac nhau");
                Log.i("444","status "+strStatus+ "----"+"ticketId "+ticketId+"----"+"token "+token+"----"+"agentId "+agentId+"----"+"staffId "+staffId);
                Log.i("3333",hostApi.hostApi+"update-ticket-status");
                wst.execute(new String[] { hostApi.hostApi+"update-ticket-status"});
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else if(((strNotes.trim()).length() > 0 &&  !nameStatus.equals(status))){
                //3. nếu reply có data > 0 và status khac nhau
                Log.i("vao", "nếu reply có data > 0 và status khac nhau");
                wst.execute(new String[] { hostApi.hostApi+"update-ticket-status"});
                wstThreadEntry.execute(new String[] { hostApi.hostApi+"create-thread-entry"});
                getDataTickedThreadUrl(hostApi.hostApi+"get-all-thread?ticketNumber="+ticketNumber);

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                //4. nếu reply có data ==0 và status giong nhau
                Log.i("vao", "nếu reply có data ==0 và status giong nhau");
                // load data dynamic data khi nhan nut update
                setListViewAdapter();
                new LoadDataServerFromURITaskDetailOpen(DetailNotiActivity.this,hostApi.hostApi+url_page, ticketId, staffId, token, agentId).execute();
            }
        }else{
            Toast.makeText(getBaseContext(), getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public View getViewByPosition(int pos, ListView listView){
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() -1;
        if(pos <firstListItemPosition || pos > lastListItemPosition){
            return listView.getAdapter().getView(pos, null, listView);
        }else{
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    // end
    /*
    * Xử lý load data listview dynamic
    */
    private void setListViewAdapter(){
        modelDynamicDetailOpens = new ArrayList<>();
        adapter = new DynamicDetailOpenAdapter(DetailNotiActivity.this, R.layout.item_dynamic_detail_open, modelDynamicDetailOpens);
        lvDynamic.setAdapter(adapter);
    }

    //parsing json after getting from Internet
    public void parseJsonResponse(String result) {
        pageCount++;
        String strStatusTicket = null;
        try {
            Log.i("result", result);
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                ModelDynamicDetailOpen data = new ModelDynamicDetailOpen();
                data.setLabel(!jObject.getString("label").equals("null") ? jObject.getString("label") : "");
                data.setValue(!jObject.getString("value").equals("null") ? jObject.getString("value") : "");
                data.setType(!jObject.getString("type").equals("null") ? jObject.getString("type") : "");
                data.setEntry_id(!jObject.getString("entry_id").equals("null") ? jObject.getString("entry_id") : "");
                data.setTicketStatus(!jObject.getString("ticketStatus").equals("null") ? jObject.getString("ticketStatus") : "");
                data.setTicket_id(jObject.getString("ticket_id"));
                data.setField_id(jObject.getString("field_id"));
                strStatusTicket = !jObject.getString("ticketStatus").equals("null") ? jObject.getString("ticketStatus") : "";
                modelDynamicDetailOpens.add(data);
            }
            tvStatus.setText(strStatusTicket);
            // tính toán set layout tự động trong listview detail
            layoutParams.height = jArray.length() * 85;
            lvDynamic.setLayoutParams(layoutParams);
            adapter.notifyDataSetChanged();
            if (dialog != null) {
                dialog.dismiss();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // calling asynctask to get json data from internet
    public void getDataFromUrl(String url) {
        new LoadDataServerOpen(this, url).execute();
    }

    //parsing json after getting from Internet
    public void parseJsonResponseDetail(String result) {
//        Log.i("jsonjsonjson==>", result);
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                ticketId = jObject.getString("ticket_id");
                ticketNumber = !jObject.getString("number").equals("null") ? jObject.getString("number") : "";
                departmentId = !jObject.getString("departmentId").equals("null") ? jObject.getString("departmentId") : "";
                departmentName = !jObject.getString("department").equals("null") ? jObject.getString("department") : "";
                email = !jObject.getString("email").equals("null") ? jObject.getString("email") : "";
                status = !jObject.getString("status").equals("null") ? jObject.getString("status") : "";
//                Log.i("11111==>", status);

                tvSticket.setText("#"+(!jObject.getString("number").equals("null") ? jObject.getString("number") : ""));
                tvPriority.setText(!jObject.getString("priority").equals("null") ? jObject.getString("priority") : "");
                tvDepartment.setText(!jObject.getString("department").equals("null") ? jObject.getString("department") : "");
                tvCreatedDate.setText(!jObject.getString("created").equals("null") ? jObject.getString("created") : "");
                tvEmail.setText(!jObject.getString("email").equals("null") ? jObject.getString("email") : "");
                String userName = !jObject.getString("username").equals("null") ? jObject.getString("username") : "";
                String teamName = !jObject.getString("teamName").equals("null") ? jObject.getString("teamName") : "";
                if(!userName.equals("") && teamName.equals("")){
                    tvAssigned.setText(userName);
                }else if(userName.equals("") && !teamName.equals("")){
                    tvAssigned.setText(teamName);
                }else{
                    tvAssigned.setText(userName +" / "+ teamName);
                }
                tvDueDate.setText(!jObject.getString("est_duedate").equals("null") ? jObject.getString("est_duedate") : "");
                tvHelpTopic.setText(!jObject.getString("topicname").equals("null") ? jObject.getString("topicname") : "");
            }
//            if (dialog != null) {
//                dialog.dismiss();
//            }
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
            dialog.setTitle("Dữ liệu đang được tải");
            dialog.setMessage("Tải dữ liệu...");
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
//            Log.i("222","222");
//            Log.i("ultresultresult==>", String.valueOf(result));
            dialog.dismiss();
            parseJsonResponseDetail(result);
        }

        public JSONObject loadJSON(String url) {
            // Creating JSON Parser instance
            JSONParser jParser = new JSONParser();
            // getting JSON string from URL
            json = jParser.getJSONFromUrl(url);
//            Log.i("loi======>", String.valueOf(json));
            return json;
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
     * end
     */
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

    /**
     * kiem tra co ket noi voi mạng không
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
