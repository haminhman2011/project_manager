package mh.manager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mh.manager.adapter.DynamicDetailClosedAdapter;
import mh.manager.adapter.TicketStatusAdapter;
import mh.manager.asynctask.CallUrlUpdateDetail;
import mh.manager.asynctask.LoadDataServerFromURITaskDetailClosed;
import mh.manager.asynctask.LoadDataServerTickedThreadClosed;
import mh.manager.dialog.AgentActivity;
import mh.manager.dialog.TeamActivity;
import mh.manager.dialog.TransferActivity;
import mh.manager.models.ModelClose;
import mh.manager.models.ModelDynamicDetailClosed;

public class DetailClosedActivity extends AppCompatActivity implements View.OnClickListener{

    public HostApi hostApi;
    private LoginDatabase sql;

    private final static String url_page = "get-ticket-detail?ticketNumber=";
    private final static String url_staffId = "&staffId=";
    private final static String url_token = "&token=";
    private final static String url_agentId = "&agentId=";
    private final static String url_status = "get-list-ticket-status?token=";

    private final static String TAG = DetailOpenActivity.class.getSimpleName();

    private TextView tvStatusUpdate, tvSticket, tvStatus, tvPriority, tvDepartment, tvCreatedDate, tvUser, tvEmail, tvPhone, tvSource, tvAssigned, tvSlaPlan, tvDueDate, tvHelpTopic, tvLastMassage, tvLastResponse;
    private Button btnBack, btnUpdateClosed, btnChangeTeam, btnChangeStatus, btnAssign, btnTransfer;
    public String ticketNumber, ticketId, staffId, agentId, token, email, status, userName, departmentId, departmentName, nameStatus;
    public EditText strNote;
    public Spinner spnChangeTeam, spnChangeStatus, spTicketStatus, spnAgent;

    public int pageCount = 0;
    public ListView lvDynamic;
    public DynamicDetailClosedAdapter adapter;
    public ProgressDialog dialog;
    public ArrayList<ModelDynamicDetailClosed> modelDynamicDetailClosed;
    public  LinearLayout.LayoutParams layoutParams;
    public LinearLayout llEntry, llLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail_closed);

        tvSticket       = (TextView) findViewById(R.id.tvSticketClosed);
        tvStatus        = (TextView) findViewById(R.id.tvStatusClosed);
        tvPriority      = (TextView) findViewById(R.id.tvPriorityClosed);
        tvDepartment    = (TextView) findViewById(R.id.tvDepartmentClosed);
        tvCreatedDate   = (TextView) findViewById(R.id.tvCreatedDateClosed);
        tvUser          = (TextView) findViewById(R.id.tvUserClosed);
        tvEmail         = (TextView) findViewById(R.id.tvEmailClosed);
        tvPhone         = (TextView) findViewById(R.id.tvPhoneClosed);
        tvSource        = (TextView) findViewById(R.id.tvSourceClosed);
        tvAssigned      = (TextView) findViewById(R.id.tvAssignedToClosed);
        tvSlaPlan       = (TextView) findViewById(R.id.tvSlaPlanClosed);
        tvDueDate       = (TextView) findViewById(R.id.tvDueDateClosed);
        tvHelpTopic     = (TextView) findViewById(R.id.tvHelpTopicClosed);
        tvLastMassage   = (TextView) findViewById(R.id.tvLastMessageClosed);
        tvLastResponse  = (TextView) findViewById(R.id.tvLastReponseClosed);

        tvStatusUpdate  = (TextView) findViewById(R.id.idStatusDetailClosed);

        strNote = (EditText) findViewById(R.id.strNote);

        llEntry = (LinearLayout) findViewById(R.id.llEntry);
        llLeft = (LinearLayout) findViewById(R.id.llLeft);

        initializeData();

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         * Xử lý data dynamic
         * get host api data
         * get token, id in database
         */
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
        lvDynamic = (ListView) findViewById(R.id.lvDynamicDataClosed);
        layoutParams = (LinearLayout.LayoutParams) lvDynamic.getLayoutParams();
        setListViewAdapter();

        // load data dynamic
        new LoadDataServerFromURITaskDetailClosed(DetailClosedActivity.this,hostApi.hostApi+url_page, ticketId, staffId, token, agentId).execute();

        // cap nhat data len server
        btnUpdateClosed = (Button) findViewById(R.id.btnUpdateClosed);
        btnUpdateClosed.setOnClickListener(this);

        Log.i("status==>", String.valueOf(sql.getListStatus()));
        spTicketStatus = (Spinner) findViewById(R.id.spinnerStatus);
        ArrayAdapter<String> ticketStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item ,sql.getListStatus());
        spTicketStatus.setAdapter(ticketStatusAdapter); // Pending
        for(int i=0; i < ticketStatusAdapter.getCount(); i++) {
            if(status.trim().equals(ticketStatusAdapter.getItem(i).toString())){
                spTicketStatus.setSelection(i);
                break;
            }
        }
        spTicketStatus.setOnItemSelectedListener(spClickItemTicketStatus);

        spnChangeTeam = (Spinner) findViewById(R.id.spnChangeTeam);
        String[] dataAssign={"Change..","Agent","Team"};
        ArrayAdapter<String> changeTeamAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,dataAssign);
        spnChangeTeam.setAdapter(changeTeamAdapter);

        btnAssign = (Button) findViewById(R.id.btnAssign);
        btnAssign.setOnClickListener(onClickAssign);

        // xử lý ticket thread chat, load data auto
        getDataTickedThreadUrl(hostApi.hostApi+"get-all-thread?ticketNumber="+ticketNumber);
//        Log.i("host api thres===>", hostApi.hostApi+"get-all-thread?ticketNumber="+ticketNumber);

        // transfer
        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(onClickTransfer);
    }

    /**
     * lấy dữ liệu từ closedactivity
     * dùng intent de bắn dữ liệu qua
     */
    private  void initializeData(){
        if(getIntent().getExtras() != null){
            ModelClose data = (ModelClose) getIntent().getSerializableExtra("Item");
            if(data != null){
                ticketId = data.getTicket_id();
                ticketNumber = data.getNumber();
                departmentId = data.getDepartmentId();
                departmentName = data.getDepartment();
                email = data.getEmail();
                status = data.getStatus();

                tvSticket.setText("#"+data.getNumber());
                tvStatus.setText(data.getStatus());
                tvPriority.setText(data.getPriority());
                tvDepartment.setText(data.getDepartment());
                tvCreatedDate.setText(data.getCreated());
                tvUser.setText(data.getUsername());
                tvEmail.setText(data.getEmail());
                tvPhone.setText(data.getPhone());
                tvSource.setText(data.getSource());
                tvAssigned.setText(data.getAssigned_to());
                tvSlaPlan.setText(data.getSlaname());
                tvDueDate.setText(data.getEst_duedate());
                tvHelpTopic.setText(data.getTopicname());
                tvLastMassage.setText(data.getLast_message());
                tvLastResponse.setText(data.getLast_reponse());
            }
        }
    }

    /**
     * Xử lý khi click chon item cua spinner ticket status se show ra du nao can thiet de gang
     */
    private AdapterView.OnItemSelectedListener spClickItemTicketStatus = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            TextView idStatus = (TextView) findViewById(R.id.idStatusDetailClosed);
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
                if(nameTeam.equals("Agent")){
                    Intent dataAgent = new Intent(DetailClosedActivity.this, AgentActivity.class);
                    dataAgent.putExtra("departmentId", departmentId);
                    dataAgent.putExtra("nameTeam", nameTeam);
                    dataAgent.putExtra("ticketId", ticketId);
                    startActivity(dataAgent);
                }else if(nameTeam.equals("Team")){
                    Intent dataTeam = new Intent(DetailClosedActivity.this, TeamActivity.class);
                    dataTeam.putExtra("nameTeam", nameTeam);
                    startActivity(dataTeam);
                }
            }else{
                Toast.makeText(DetailClosedActivity.this, "Vui lòng chọn!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * onClickTransfer
     */
    private View.OnClickListener onClickTransfer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent data = new Intent(DetailClosedActivity.this, TransferActivity.class);
            data.putExtra("departmentName", departmentName);
            startActivity(data);

        }
    };

    // calling asynctask to get json data from internet
    private void getDataTickedThreadUrl(String url) {
        new LoadDataServerTickedThreadClosed(this, url).execute();
    }
    // xử lý load thread động
    public void parseJsonResponseTickedThread(String result) {
        pageCount++;
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));
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
                        tvPoster.setText(objEntry.getString("poster"));
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
                        tvBody.setText(objEntry.getString("body"));
                        llEntry.addView(tvBody);

                        for(int e = 0; e < arrEvents.length(); e++){
                            JSONObject objEvents = arrEvents.getJSONObject(e);
                            tvMessageText = new TextView(this);
                            tvMessageText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvMessageText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                            tvMessageText.setTextSize(15);
                            tvMessageText.setPadding(25,0,0,0);
                            tvMessageText.setBackgroundResource(R.color.btnColor);
                            tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mess_16,0,0,0);
                            tvMessageText.setTextColor(Color.parseColor("#616161"));
                            if(!objEvents.getString("messageText").equals("empty")){
                                String strMss = String.valueOf(objEvents.getString("messageText"));
                                String replaceMss_1 = strMss.replace("<br /><br />", "");
                                String replaceMss_2 = replaceMss_1.replace("</b>", "");

                                Spanned test = Html.fromHtml(objEvents.getString("messageText"));
                                tvMessageText.setText("");
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
                                    tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mess_16,0,0,0);
                                    tvMessageText.setTextColor(Color.parseColor("#616161"));
                                    tvMessageText.setText(Html.fromHtml(objEvents.getString("messageText")));
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
                            tvPoster.setText(objEntry.getString("poster"));
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
                            tvMssLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mess_16,0,0,0);
                            tvMssLeft.setTextColor(Color.parseColor("#616161"));
                            tvMssLeft.setText(Html.fromHtml(objLeft.getString("messageText")));
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
                value = ((TextView)parentView.findViewById(R.id.edtNameDetailClosed)).getText().toString();
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
            wst.execute(new String[] { hostApi.hostApi+"update-ticket-status"});
            Intent intent = new Intent(DetailClosedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if(((strNotes.trim()).length() > 0 &&  !nameStatus.equals(status))){
            //3. nếu reply có data > 0 và status khac nhau
            Log.i("vao", "nếu reply có data > 0 và status khac nhau");
            getDataTickedThreadUrl(hostApi.hostApi+"get-all-thread?ticketNumber="+ticketNumber);
            wst.execute(new String[] { hostApi.hostApi+"update-ticket-status"});
            Intent intent = new Intent(DetailClosedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            //4. nếu reply có data ==0 và status giong nhau
            Log.i("vao", "nếu reply có data ==0 và status giong nhau");
            // load data dynamic data khi nhan nut update
            setListViewAdapter();
            new LoadDataServerFromURITaskDetailClosed(DetailClosedActivity.this,hostApi.hostApi+url_page, ticketId, staffId, token, agentId).execute();
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
        modelDynamicDetailClosed = new ArrayList<>();
        adapter = new DynamicDetailClosedAdapter(DetailClosedActivity.this, R.layout.item_dynamic_detail_closed, modelDynamicDetailClosed);
        lvDynamic.setAdapter(adapter);
    }

    //parsing json after getting from Internet
    public void parseJsonResponse(String result) {
        //Log.i(TAG, result);
        pageCount++;
        String strStatusTicket = null;
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                ModelDynamicDetailClosed data = new ModelDynamicDetailClosed();
                data.setLabel(!jObject.getString("label").equals("null") ? jObject.getString("label") : "");
                data.setValue(!jObject.getString("value").equals("null") ? jObject.getString("value") : "");
                data.setType(!jObject.getString("type").equals("null") ? jObject.getString("type") : "");
                data.setEntry_id(!jObject.getString("entry_id").equals("null") ? jObject.getString("entry_id") : "");
                data.setTicketStatus(!jObject.getString("ticketStatus").equals("null") ? jObject.getString("ticketStatus") : "");
                data.setTicket_id(jObject.getString("ticket_id"));
                data.setField_id(jObject.getString("field_id"));
                strStatusTicket = !jObject.getString("ticketStatus").equals("null") ? jObject.getString("ticketStatus") : "";
                modelDynamicDetailClosed.add(data);
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
}

