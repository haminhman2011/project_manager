package mh.manager.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import mh.manager.HostApi;
import mh.manager.LoginDatabase;
import mh.manager.MainActivity;
import mh.manager.R;
import mh.manager.asynctask.CallUrlUpdateDetail;
import mh.manager.format.FormatFont;
import mh.manager.jsonfuntions.JsonLoadStatus;
import mh.manager.lang.SharedPrefControl;
import mh.manager.models.ModelAgent;

/**
 * Created by man.ha on 7/28/2017.
 */

public class AgentActivity extends Activity{
    public HostApi hostApi;
    private LoginDatabase sql;
    private final static String url_page = "get-list-agent";
    private final static String url_deptId= "?deptId=";

    public ArrayList<ModelAgent> modelAgents;
    public ArrayList<String> arrAgent;
    public Spinner spAgent;
    JSONObject jsonobject;
    JSONArray jsonarray;
    public TextView titleTeam, tvIdAgent;
    public String departmentId, ticketId, staffAssignedId;
    public Button btnAssignAgent, btnDialogCancel, btnClose;
    public EditText edtNoteDialog;
    public FormatFont formatFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_change_agent);
        SharedPrefControl.updateLangua(getApplicationContext());

        sql = new LoginDatabase(this);
        sql.getWritableDatabase();
        try {
            for(int i=0; i<sql.getInforUser().length(); i++){
                JSONObject obj = sql.getInforUser().getJSONObject(i);
                staffAssignedId = obj.getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();

        tvIdAgent = (TextView) findViewById(R.id.tvIdAgent);
        edtNoteDialog= (EditText) findViewById(R.id.edtNoteDialog);
        titleTeam = (TextView) findViewById(R.id.titleTeam);
        if(bd != null){
            titleTeam.setText(String.valueOf(bd.get("nameTeam")));
            departmentId = bd.getString("departmentId");
            ticketId = bd.getString("ticketId");
        }

        spAgent = (Spinner) findViewById(R.id.spnAgent);

        new DownloadJSON().execute();

        btnAssignAgent = (Button) findViewById(R.id.btnAssignAgent);
        btnAssignAgent.setOnClickListener(onClickAssign);

        btnClose = (Button) findViewById(R.id.close_button);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDialogCancel = (Button) findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public View.OnClickListener onClickAssign = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isOnline()){
                String strStaffId;
                strStaffId = String.valueOf(tvIdAgent.getText());
                // Tạo mới một lớp CallUrl
                AgentAssign wst = new AgentAssign(AgentAssign.POST_TASK, AgentActivity.this, "Checking...");
                wst.addNameValuePair("staffId",strStaffId);
                wst.addNameValuePair("ticketId",ticketId);
                wst.addNameValuePair("teamId","");
                wst.addNameValuePair("staffAssignedId",staffAssignedId);
//            Log.i("assign============>", "staffId "+strStaffId+"------------"+"ticketId "+ticketId+"-----------------"+"teamId"+"----------"+"staffAssignedId "+staffAssignedId);
                wst.execute(new String[] { hostApi.hostApi+"assign-ticket"});
            }else{
                Toast.makeText(getBaseContext(), getString(R.string.not_connection),Toast.LENGTH_SHORT).show();
            }
        }
    };

    public class AgentAssign extends AsyncTask<String, Integer, String> {

        public static final int POST_TASK = 1;
        public static final int GET_TASK = 2;
        private static final String TAG = "WebServiceTask";
        // thời gian chờ của một kết nối, tính theo milliseconds (waiting to
        // connect)
        // private static final int CONN_TIMEOUT = 30000;
        // thời gian chờ của một socket, tính bằng milliseconds (waiting for data)
        // private static final int SOCKET_TIMEOUT = 50000;
        private int taskType = GET_TASK;
        private Context mContext = null;
        private String processMessage = "Processing...";
        private ArrayList<NameValuePair> params = new ArrayList<>();
        private ProgressDialog progressDialog;

        // Khởi tạo
        public AgentAssign(int taskType, Context mContext, String processMessage) {

            this.taskType = taskType;
            this.mContext = mContext;
            this.processMessage = processMessage;

        }

        @Override
        protected void onPostExecute(String response) {
            progressDialog.dismiss();
            Log.i("trang thai cap nhat", response);

            if(response.equals("success")){
                Toast.makeText(AgentActivity.this, "Assign sucess", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(mContext, "Đăng nhập thất bại, vui lòng kiểm tra lại",Toast.LENGTH_SHORT).show();
            }
        }
        // thêm thông tin cần thiết để gửi lên server
        public void addNameValuePair(String name, String value) {

            params.add(new BasicNameValuePair(name, value));
        }

        // hiển thị dialog trên UI cho người dùng biết app đang trong quá trình làm
        // việc
        @Override
        protected void onPreExecute() {

            // showProgressDialog();
            this.progressDialog = ProgressDialog.show(mContext, "",
                    processMessage);
        }

        // kết nối đến server thông url
        protected String doInBackground(String... urls) {
            String url = urls[0];
            String result = "";
            HttpResponse response = doResponse(url);
            if (response == null) {
                return result;
            } else {
                try {

                    // kết quả trả về được chuyển về dạng chuỗi
                    result = inputStreamToString(response.getEntity().getContent());


                } catch (IllegalStateException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);

                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
            }
            Log.i("111111=======>", result);
            return result;
        }

        // khởi tạo socket và kết nối
        private HttpParams getHttpParams() {

            HttpParams htpp = new BasicHttpParams();
            // HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
            // HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);
            return htpp;
        }

        // thao tác xử lý khi kết nối đến server
        private HttpResponse doResponse(String url) {

            HttpClient httpclient = new DefaultHttpClient(getHttpParams());
            httpclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
            HttpResponse response = null;

            try {
                switch (taskType) {

                    // kiểm tra tác vụ cần thực hiển
                    // post gửi yêu cầu kèm thông tin
                    // Get gửi yêu cầu
                    case POST_TASK:
                        HttpPost httppost = new HttpPost(url);

                        // Add parameters
                        httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                        response = httpclient.execute(httppost);
                        break;
                    case GET_TASK:
                        HttpGet httpget = new HttpGet(url);
                        response = httpclient.execute(httpget);
                        break;
                }
            } catch (Exception e) {

                Log.e(TAG, e.getLocalizedMessage(), e);

            }

            return response;
        }

        // Chuyển thông tin nhận về thành dạng chuỗi
        private String inputStreamToString(InputStream is) {

            String line = "";
            StringBuilder total = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                // đọc thông tin nhận được cho đến khi kết thúc
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

            // Trả về giá trị chuỗi đầy đủ
            return total.toString();
        }
    }

    public class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            hostApi = new HostApi();
            formatFont = new FormatFont();
            modelAgents = new ArrayList<>();
            // Create an array to populate the spinner
            arrAgent = new ArrayList<>();
            // JSON file URL address
            jsonobject = JsonLoadStatus.getJSONfromURL(hostApi.hostApi+url_page+url_deptId+departmentId);

            try {
                // Locate the NodeList name
                jsonarray = jsonobject.getJSONArray("datas");
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    ModelAgent data = new ModelAgent();
                    data.setStaff_id(!jsonobject.getString("staff_id").equals("null") ? jsonobject.getString("staff_id") : "");
                    data.setUsername(!jsonobject.getString("username").equals("null") ? jsonobject.getString("username") : "");
                    data.setFirstname(!jsonobject.getString("firstname").equals("null") ? jsonobject.getString("firstname") : "");
                    data.setLastname(!jsonobject.getString("lastname").equals("null") ? jsonobject.getString("lastname") : "");
                    modelAgents.add(data);
                    // Populate spinner with country names
                    arrAgent.add(formatFont.formatFont(jsonobject.optString("firstname")) + " " + formatFont.formatFont(jsonobject.optString("lastname")));

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            spAgent .setAdapter(new ArrayAdapter<>(AgentActivity.this,android.R.layout.simple_spinner_dropdown_item, arrAgent));
            spAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvIdAgent = (TextView) findViewById(R.id.tvIdAgent);
                    tvIdAgent.setVisibility(View.GONE);
                    tvIdAgent.setText(modelAgents.get(position).getStaff_id());
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
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
