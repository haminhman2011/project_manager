package mh.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mh.manager.adapter.DynamicDetailOpenAdapter;
import mh.manager.asynctask.CallUrlUpdateDetail;
import mh.manager.asynctask.LoadDataServerFromURITaskDetailOpen;
import mh.manager.asynctask.LoadDataServerTickedThreadOpen;
import mh.manager.dialog.AgentActivity;
import mh.manager.dialog.TeamActivity;
import mh.manager.dialog.TransferActivity;
import mh.manager.format.FormatFont;
import mh.manager.jsonfuntions.JsonLoadStatus;
import mh.manager.lang.SharedPrefControl;
import mh.manager.models.ModelDepartment;
import mh.manager.models.ModelDynamicDetailOpen;
import mh.manager.models.ModelOpen;
import mh.manager.models.ModelStatus;
import mh.manager.models.ModelUrlRec;

public class DetailOpenActivity extends AppCompatActivity implements View.OnClickListener{
    public HostApi hostApi;
    private LoginDatabase sql;
    public FormatFont formatFont;
    public SeekBar sbAudio;
    public MediaPlayer mediaplayer;
    public Handler handler;
    public boolean audio_Available = false;
    public int totalTime = 0;
    private int counter  =0;//bien dem giay

    private final static String url_page = "get-ticket-detail";
    private final static String url_staffId = "&staffId=";
    private final static String url_token = "&token=";
    private final static String url_agentId = "&agentId=";
    private final static String url_status = "get-list-ticket-status?token=";

    private final static String TAG = DetailOpenActivity.class.getSimpleName();

    public TextView tvStatusUpdate, tvSticket, tvStatus, tvPriority, tvDepartment, tvCreatedDate, tvEmail, tvAssigned, tvDueDate, tvHelpTopic, tvHotel, tvRoom, txtTotalTime;
    public Button btnBack, btnUpdateOpen, btnChangeTeam, btnChangeStatus, btnAssign, btnTransfer, btnEditTicket, btnCancel;
    public ImageButton btnPlay, btnPause;
    public String ticketNumber, ticketId, staffId, agentId, token, email, status, userName, departmentId, departmentName, nameStatus, assign, transfer;
    public EditText strNote;
    public Spinner spnChangeTeam, spnChangeStatus, spTicketStatus, spnAgent;

    public int pageCount = 0;
    public ListView lvDynamic, lvTicketThread;
    public DynamicDetailOpenAdapter adapter;
    public ProgressDialog dialog;
    public ArrayList<ModelDynamicDetailOpen> modelDynamicDetailOpens;
    public  LinearLayout.LayoutParams layoutParams;
    public LinearLayout llEntry, llLeft;

    public ArrayList<ModelStatus> modelStatus;
    public ArrayList<String> arrStatus;
    JSONObject jsonobject;
    JSONArray jsonarray, arrPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail_open);
        SharedPrefControl.updateLangua(getApplicationContext());

        handler = new Handler();
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPause = (ImageButton) findViewById(R.id.btnPause);
        btnPause.setVisibility(View.GONE);
//        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);
//        sbAudio = (SeekBar) findViewById(R.id.sbAudio);
//        sbAudio.setMax(100);
//        sbAudio.setOnSeekBarChangeListener(onCLickSeekBarChangeListener);

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
        tvHotel         = (TextView) findViewById(R.id.tvHotel);
        tvRoom          = (TextView) findViewById(R.id.tvRoom);

        strNote = (EditText) findViewById(R.id.edtNoteOPen);
        llEntry = (LinearLayout) findViewById(R.id.llEntry);
        llLeft = (LinearLayout) findViewById(R.id.llLeft);

        initializeData();
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audio_Available){
                    mediaplayer.pause();
                    btnPlay.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.GONE);
                }
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
//        setListViewAdapter();
        // Load tiket statuc + load them data cua permission;
        spTicketStatus = (Spinner) findViewById(R.id.spinnerStatus);
        new downloadJSONStatus().execute();

//        Log.i("link host==>", hostApi.hostApi+url_page +"-----"+ ticketId+"-----"+ staffId+"-----"+ token+"-----"+ agentId);
        // load data dynamic
        Log.i("dynamic", hostApi.hostApi+url_page+ticketId+staffId+token+agentId);
        new LoadDataServerFromURITaskDetailOpen(DetailOpenActivity.this,hostApi.hostApi+url_page, ticketId, staffId, token, agentId).execute();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audio_Available){
                    if(!mediaplayer.isPlaying()){
                        mediaplayer.start();
                        btnPause.setVisibility(View.VISIBLE);
                        btnPlay.setVisibility(View.GONE);
                        // phai chay cai time
//                        countTimer();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Not play", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaplayer.pause();
                btnPlay.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
            }
        });

        // cap nhat data len server
        btnUpdateOpen = (Button) findViewById(R.id.btnUpdateOpen);
        btnUpdateOpen.setOnClickListener(this);

//        Log.i("assign", assign);


//        ArrayAdapter<String> ticketStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item ,sql.getListStatus());
//        spTicketStatus.setAdapter(ticketStatusAdapter); // Pending
//        for(int i=0; i < ticketStatusAdapter.getCount(); i++) {
//            if(status.trim().equals(ticketStatusAdapter.getItem(i).toString())){
//                spTicketStatus.setSelection(i);
//                break;
//            }
//        }
//        spTicketStatus.setOnItemSelectedListener(spClickItemTicketStatus);

        spnChangeTeam = (Spinner) findViewById(R.id.spnChangeTeam);
        String[] dataAssign={"Change..","Agent"};//,"Team"
        ArrayAdapter<String> changeTeamAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,dataAssign);
        changeTeamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnChangeTeam.setAdapter(changeTeamAdapter);

        btnAssign = (Button) findViewById(R.id.btnAssign);


        btnAssign.setOnClickListener(onClickAssign);

        // xử lý ticket thread chat, load data auto
        getDataTickedThreadUrl(hostApi.hostApi+"get-all-thread?ticketNumber="+ticketNumber);
        Log.i("host api thres===>", hostApi.hostApi+"get-all-thread?ticketNumber="+ticketNumber);

        // transfer
        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(onClickTransfer);
        //edit detail ticket
        btnEditTicket = (Button) findViewById(R.id.btnEditTicket);
        btnEditTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEdit = new Intent(DetailOpenActivity.this, EditDetailOpenActivity.class);
                startActivity(intentEdit);
                finish();
            }
        });


    }

    private SeekBar.OnSeekBarChangeListener onCLickSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int percent = seekBar.getProgress();
            // so miligiay hien tai ma audio nen dich den
            int currentTime_SeekTo = percent * totalTime / 100;
            mediaplayer.seekTo(currentTime_SeekTo);
        }
    };

    private  void countTimer(){
        handler.postDelayed(Timer_Counter, 1000);
    }
    private Runnable Timer_Counter = new Runnable() {
        @Override
        public void run() {
//            if(conter < )
            counter++;
//            txtCurrentTime.setText(""+ conter);
            // tinh phan tram soudio da play duoc thi keo speebar
            int currentPercent = 100 * mediaplayer.getCurrentPosition()/totalTime;
            sbAudio.setProgress(currentPercent);
            countTimer();
        }
    };
    /**
     * lấy dữ liệu từ openactivity
     * dùng intent de bắn dữ liệu qua
     */
    private  void initializeData(){
        if(getIntent().getExtras() != null){
            ModelOpen data = (ModelOpen) getIntent().getSerializableExtra("Item");
            if(data != null){
                ticketId = data.getTicket_id();
                ticketNumber = data.getNumber();
                departmentId = data.getDepartmentId();
                departmentName = data.getDepartment();
                email = data.getEmail();
                status = data.getStatus();
                Log.i("departmentId", departmentId);

                tvSticket.setText("#"+data.getNumber());
                tvPriority.setText(data.getPriority());
                tvDepartment.setText(data.getDepartment());
                tvCreatedDate.setText(data.getCreated());
                tvEmail.setText(data.getEmail());
                if(!data.getUsername().equals("") && data.getTeamName().equals("")){
                    tvAssigned.setText(data.getUsername());

                }else if(data.getUsername().equals("") && !data.getTeamName().equals("")){
                    tvAssigned.setText(data.getTeamName());

                }else{
                    tvAssigned.setText(data.getUsername() +" / "+ data.getTeamName());


                }
                tvDueDate.setText(data.getEst_duedate());
                tvHelpTopic.setText(data.getTopicname());
                tvHotel.setText(data.getHotel());
                tvRoom.setText(data.getRoom());
            }
        }
    }



    /**
     * Show dialog team
     */
    private View.OnClickListener onClickAssign = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            String nameTeam = spnChangeTeam.getSelectedItem().toString();
            String nameTeam = "Agent";
//            if(!nameTeam.equals("Change..")){
                if(isOnline()){
//                    if(nameTeam.equals("Agent")){
                        Intent dataAgent = new Intent(DetailOpenActivity.this, AgentActivity.class);
                        dataAgent.putExtra("departmentId", departmentId);
                        dataAgent.putExtra("nameTeam", nameTeam);
                        dataAgent.putExtra("ticketId", ticketId);
                        startActivity(dataAgent);
//                    }else if(nameTeam.equals("Team")){
//                        Toast.makeText(getBaseContext(), "Feature is being perfected", Toast.LENGTH_SHORT).show();
//                        Intent dataTeam = new Intent(DetailOpenActivity.this, TeamActivity.class);
//                        dataTeam.putExtra("nameTeam", nameTeam);
//                        startActivity(dataTeam);
//                    }
                }else{
                    Toast.makeText(getBaseContext(), getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
                }
//            }else{
//                Toast.makeText(DetailOpenActivity.this, getString(R.string.please_choose), Toast.LENGTH_SHORT).show();
//            }
        }
    };

    /**
     * onClickTransfer
     */
    private View.OnClickListener onClickTransfer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        if(isOnline()){
            Intent data = new Intent(DetailOpenActivity.this, TransferActivity.class);
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
        new LoadDataServerTickedThreadOpen(this, url).execute();
    }
    // xử lý load thread động
    public void parseJsonResponseTickedThread(String result) {
        pageCount++;
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));
            Log.i("h 1111===>", String.valueOf(json));
            TextView tvPoster, tvMessageText, tvCreated, tvBody;
            if(jArray.length() == 1){
//                Log.i("11111", "11111");
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
                            tvMessageText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mess_16,0,0,0);
                            tvMessageText.setTextColor(Color.parseColor("#616161"));
                            if(!objEvents.getString("messageText").equals("empty")){
                                tvMessageText.setText(" " + Html.fromHtml(objEvents.getString("messageText"))); //
                                llEntry.addView(tvMessageText);
                            }
                        }
                    }
                }
            }else if(jArray.length() > 1){
                Log.i("222", "2222");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject objDatas = jArray.getJSONObject(i);
                    if(objDatas.isNull("left")) {
                        Log.i("222", "111");
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
//                                Log.i("messageText", String.valueOf(Html.fromHtml(objEvents.getString("messageText"))));
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
                        Log.i("222", "33333");
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
            }else{
                Log.i("3333", "33333");
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
//            JSONArray jsonArray = new JSONArray();
//            try {
//                for (int i = 0; i < lvDynamic.getCount(); i++) {
//                    JSONObject jsonObject = new JSONObject();
//                    parentView = getViewByPosition(i, lvDynamic);
//                    value = ((TextView)parentView.findViewById(R.id.edtNameDetailOpen)).getText().toString();
//                    tvEntryId = ((TextView)parentView.findViewById(R.id.tvEntryId)).getText().toString();
//                    tvFieldId = ((TextView)parentView.findViewById(R.id.tvFieldId)).getText().toString();
//                    jsonObject.put("value", value);
//                    jsonObject.put("entry_id", tvEntryId);
//                    jsonObject.put("field_id", tvFieldId);
//                    jsonArray.put(jsonObject);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            // Tạo mới một lớp CallUrl
            CallUrlUpdateDetail wst = new CallUrlUpdateDetail(CallUrlUpdateDetail.POST_TASK, this, "Checking...");
//            CallUrlUpdateDetail wstDetail = new CallUrlUpdateDetail(CallUrlUpdateDetail.POST_TASK, this, "Checking...");
            CallUrlUpdateDetail wstThreadEntry = new CallUrlUpdateDetail(CallUrlUpdateDetail.POST_TASK, this, "Checking...");

            wst.addNameValuePair("status",strStatus);
            wst.addNameValuePair("ticketId",ticketId);
            wst.addNameValuePair("token",token);
            wst.addNameValuePair("agentId",agentId);
            wst.addNameValuePair("staffId",staffId);

//            wstDetail.addNameValuePair("details", String.valueOf("{\"datas\":"+jsonArray+"}"));

            wstThreadEntry.addNameValuePair("ticketId", ticketId);
            wstThreadEntry.addNameValuePair("email", email);
            wstThreadEntry.addNameValuePair("body", strNotes);
            wstThreadEntry.addNameValuePair("staffId",staffId);
            wstThreadEntry.addNameValuePair("ipAddress", locaLIpAddress.getLocalIpAddress());

            // Đường dẫn đến server
//            wstDetail.execute(new String[] { hostApi.hostApi+"update-ticket-detail"});

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
                Intent intent = new Intent(DetailOpenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else if(((strNotes.trim()).length() > 0 &&  !nameStatus.equals(status))){
                //3. nếu reply có data > 0 và status khac nhau
                Log.i("vao", "nếu reply có data > 0 và status khac nhau");
                wst.execute(new String[] { hostApi.hostApi+"update-ticket-status"});
                wstThreadEntry.execute(new String[] { hostApi.hostApi+"create-thread-entry"});
                getDataTickedThreadUrl(hostApi.hostApi+"get-all-thread?ticketNumber="+ticketNumber);

                Intent intent = new Intent(DetailOpenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                //4. nếu reply có data ==0 và status giong nhau
                Log.i("vao", "nếu reply có data ==0 và status giong nhau");
                // load data dynamic data khi nhan nut update
//                setListViewAdapter();
//                new LoadDataServerFromURITaskDetailOpen(DetailOpenActivity.this,hostApi.hostApi+url_page, ticketId, staffId, token, agentId).execute();
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
        adapter = new DynamicDetailOpenAdapter(DetailOpenActivity.this, R.layout.item_dynamic_detail_open, modelDynamicDetailOpens);
        lvDynamic.setAdapter(adapter);
    }

    //parsing json after getting from Internet
    public void parseJsonResponse(String result) {
        pageCount++;
        String strStatusTicket = null;
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = new JSONArray(json.getString("datas"));

            JSONArray jUrlRec = new JSONArray(json.getString("urlRec"));
            Log.i("urlRec====>", String.valueOf(jUrlRec.getJSONObject(0).getString("urlRec")));
            String strUrlVoid = jUrlRec.getJSONObject(0).getString("urlRec");
            if(!strUrlVoid.equals("Not found")){
                btnPlay.setVisibility(View.VISIBLE);
                mediaplayer = new MediaPlayer();
                mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaplayer.setLooping(true);
                mediaplayer.setDataSource(strUrlVoid);
                mediaplayer.setOnPreparedListener(onPrepared_Audio);
                mediaplayer.setOnBufferingUpdateListener(onBuffering_loading);
                mediaplayer.prepareAsync();// play dang stream, bat dong bo
            }else{
                btnPlay.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
            }



            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
//                ModelDynamicDetailOpen data = new ModelDynamicDetailOpen();

//                    data.setLabel(!jObject.getString("label").equals("null") ? jObject.getString("label") : "");
//                    data.setValue(!jObject.getString("value").equals("null") ? jObject.getString("value") : "");
//                    data.setType(!jObject.getString("type").equals("null") ? jObject.getString("type") : "");
//                    data.setEntry_id(!jObject.getString("entry_id").equals("null") ? jObject.getString("entry_id") : "");
//                    data.setTicketStatus(!jObject.getString("ticketStatus").equals("null") ? jObject.getString("ticketStatus") : "");
//                    data.setTicket_id(jObject.getString("ticket_id"));
//                    data.setField_id(jObject.getString("field_id"));
                    strStatusTicket = !jObject.getString("ticketStatus").equals("null") ? jObject.getString("ticketStatus") : "";
//                    modelDynamicDetailOpens.add(data);


//                Log.i("dem ky tu chuoi", jObject.getString("value"));
            }



            tvStatus.setText(strStatusTicket);
            // tính toán set layout tự động trong listview detail
            layoutParams.height = jArray.length() * 70;
            lvDynamic.setLayoutParams(layoutParams);
            adapter.notifyDataSetChanged();
            if (dialog != null) {
                dialog.dismiss();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    private MediaPlayer.OnBufferingUpdateListener  onBuffering_loading = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            // precent la % la audio data la load duoc de play nhac
//            sbAudio.setSecondaryProgress(percent);
        }
    };

    private MediaPlayer.OnPreparedListener onPrepared_Audio = new MediaPlayer.OnPreparedListener(){

        @Override
        public void onPrepared(MediaPlayer mp) {
            // audio dan sang sang play
            // lay duoc total time
            totalTime = mediaplayer.getDuration();// time tinh theo milisecion
//            int minute = totalTime / 1000/60;
//            txtTotalTime.setText(minute + ":0");
            // dung flag de danh dau bat dau
            audio_Available = true;

        }
    };

    public void parseJsonResponseStatusAndPermission() {
        jsonobject = JsonLoadStatus.getJSONfromURL(hostApi.hostApi+"get-list-ticket-status?staffId="+staffId+"&deptId="+departmentId); //
        Log.i("link per===>", hostApi.hostApi+"get-list-ticket-status?staffId="+staffId+"&deptId="+departmentId);
        try {
            // Locate the NodeList name
//                jsonarray = new JSONArray(jsonobject.getString("status"));

            jsonarray = jsonobject.getJSONArray("status");
            arrPermission = jsonobject.getJSONArray("permission");
            for (int i = 0; i < arrPermission.length(); i++) {
                jsonobject = arrPermission.getJSONObject(i);
                assign = String.valueOf(jsonobject.getString("assign"));
                transfer = String.valueOf(jsonobject.getString("transfer"));

            }




//            btnAssign.setEnabled(false);
            for (int i = 0; i < jsonarray.length(); i++) {
                jsonobject = jsonarray.getJSONObject(i);
                ModelStatus data = new ModelStatus();
                data.setId(!jsonobject.getString("id").equals("null") ? jsonobject.getString("id") : "");
                data.setName(!jsonobject.getString("name").equals("null") ? jsonobject.getString("name") : "");
                data.setState(!jsonobject.getString("state").equals("null") ? jsonobject.getString("state") : "");
                modelStatus.add(data);
                // Populate spinner with country names
                arrStatus.add(formatFont.formatFont(jsonobject.optString("name")));
            }


        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }



    public class downloadJSONStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            hostApi = new HostApi();
            formatFont = new FormatFont();
            modelStatus = new ArrayList<>();
            // Create an array to populate the spinner
            arrStatus = new ArrayList<>();
            // JSON file URL address
            parseJsonResponseStatusAndPermission();


            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(DetailOpenActivity.this,android.R.layout.simple_spinner_dropdown_item, arrStatus);
            spTicketStatus .setAdapter(adapter);
            for(int i=0; i < adapter.getCount(); i++) {
                if(status.trim().equals(adapter.getItem(i).toString())){
                    spTicketStatus.setSelection(i);
                    break;
                }
            }

            // phan quyen cac nut
            // nam trong day voi lý do la phan phân quyền nằm chung với status nen khi load xong neu la 0 thi an cac button và 1 thì nguoc lai
            if(transfer.equals("0")){
                btnTransfer.setVisibility(View.GONE);
            }else{
                btnTransfer.setVisibility(View.VISIBLE);
            }
            LinearLayout llAssign = (LinearLayout) findViewById(R.id.llAssign);
            if(assign.equals("0")){
                llAssign.setVisibility(View.GONE);
            }else{
                llAssign.setVisibility(View.VISIBLE);
            }

            spTicketStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    TextView idStatus = (TextView) findViewById(R.id.idStatusDetailOPen);
                    idStatus.setVisibility(View.INVISIBLE);
                    nameStatus = spTicketStatus.getSelectedItem().toString();
                    idStatus.setText(sql.getIdStatus(spTicketStatus.getSelectedItem().toString()));
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
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

