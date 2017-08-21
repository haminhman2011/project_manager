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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mh.manager.HostApi;
import mh.manager.LocaLIpAddress;
import mh.manager.LoginDatabase;
import mh.manager.MainActivity;
import mh.manager.R;
import mh.manager.asynctask.CallUrlUpdateDetail;
import mh.manager.format.FormatFont;
import mh.manager.jsonfuntions.JsonLoadStatus;
import mh.manager.lang.SharedPrefControl;
import mh.manager.models.ModelDepartment;

public class TransferActivity extends Activity {
    public HostApi hostApi;
    private LoginDatabase sql;
    public FormatFont formatFont;

    public ArrayList<ModelDepartment> modelDepartments;
    public ArrayList<String> arrDepartments;
    public Spinner spnTransfer;
    JSONObject jsonobject;
    JSONArray jsonarray;
    public TextView titleTeam, tvIdAgent, tvIdTransfer , tvNameSpnDepartment;
    public String departmentId, staffId, departmentName, note, ipAddress, ticketId;
    public Button btnTransfer, btnCancel, btnClose;
    public EditText edtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_transfer);
        SharedPrefControl.updateLangua(getApplicationContext());

        edtNote = (EditText) findViewById(R.id.edtNote);
        tvIdTransfer = (TextView) findViewById(R.id.tvIdTransfer);
        tvNameSpnDepartment = (TextView) findViewById(R.id.tvNameSpnDepartment);
        tvNameSpnDepartment.setVisibility(View.GONE);

        btnClose = (Button) findViewById(R.id.close_button);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null){
            departmentName = bd.getString("departmentName");
            ticketId = bd.getString("ticketId");
            staffId = bd.getString("staffId");
        }
        spnTransfer = (Spinner) findViewById(R.id.spnTransfer);
        new DownloadJSON().execute();

        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(onClickTransfer);
    }

    // btnTransfer
    private View.OnClickListener onClickTransfer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isOnline()){
                hostApi = new HostApi();
                String strNote, strIdDeparment, spnNameDepartnemt;
                spnNameDepartnemt = String.valueOf(tvNameSpnDepartment.getText());
                if(!spnNameDepartnemt.equals(departmentName)){
                    strNote = String.valueOf(edtNote.getText());
                    strIdDeparment = String.valueOf(tvIdTransfer.getText());
                    LocaLIpAddress locaLIpAddress = new LocaLIpAddress();
                    // Tạo mới một lớp CallUrl
                    CallUrlTransfer  wst = new CallUrlTransfer(CallUrlTransfer.POST_TASK, v.getContext(), "Checking...");
                    wst.addNameValuePair("ticketId",ticketId);
                    wst.addNameValuePair("deptId",strIdDeparment);
                    wst.addNameValuePair("staffId",staffId);
                    wst.addNameValuePair("note",strNote);
                    wst.addNameValuePair("ipAddress",locaLIpAddress.getLocalIpAddress());
//                Log.i("tranfer==>", "ticketId = "+ticketId+"----"+"deptId = "+strIdDeparment+"----"+"staffId = "+staffId+"----"+"note = "+strNote+"----"+"ipAddress = "+locaLIpAddress.getLocalIpAddress());
                    // Đường dẫn đến server
                    wst.execute(new String[] { hostApi.hostApi+"transfer-ticket"});

                }else{
                    Toast.makeText(TransferActivity.this, "Department duplicates please select again", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getBaseContext(), getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
            }


        }
    };

    public class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            hostApi = new HostApi();
            formatFont = new FormatFont();
            modelDepartments = new ArrayList<>();
            // Create an array to populate the spinner
            arrDepartments = new ArrayList<>();
            // JSON file URL address
            jsonobject = JsonLoadStatus.getJSONfromURL(hostApi.hostApi+"get-list-department");

            try {
                // Locate the NodeList name
                jsonarray = jsonobject.getJSONArray("datas");
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    ModelDepartment data = new ModelDepartment();
                    data.setId(!jsonobject.getString("id").equals("null") ? jsonobject.getString("id") : "");
                    data.setName(!jsonobject.getString("name").equals("null") ? jsonobject.getString("name") : "");
                    modelDepartments.add(data);
                    // Populate spinner with country names
                    arrDepartments.add(formatFont.formatFont(jsonobject.optString("name")));

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(TransferActivity.this,android.R.layout.simple_spinner_dropdown_item, arrDepartments);
            spnTransfer .setAdapter(adapter);
            for(int i=0; i < adapter.getCount(); i++) {
                if(departmentName.trim().equals(adapter.getItem(i).toString())){
                    spnTransfer.setSelection(i);
                    break;
                }
            }
            spnTransfer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvIdTransfer = (TextView) findViewById(R.id.tvIdTransfer);
                    tvIdTransfer.setVisibility(View.GONE);
                    tvNameSpnDepartment.setText(modelDepartments.get(position).getName());
                    tvIdTransfer.setText(modelDepartments.get(position).getId());
//                    ((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }

    public class CallUrlTransfer extends AsyncTask<String, Integer, String> {

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
        public CallUrlTransfer(int taskType, Context mContext, String processMessage) {

            this.taskType = taskType;
            this.mContext = mContext;
            this.processMessage = processMessage;

        }

        @Override
        protected void onPostExecute(String response) {
            progressDialog.dismiss();
            Log.i("trang thai cap nhat", response);
            if(response.equals("success")){
                Toast.makeText(mContext, "Transfer success",Toast.LENGTH_SHORT).show();
                Intent intent  = new Intent(TransferActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(mContext, "Transfer errors",Toast.LENGTH_SHORT).show();
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
