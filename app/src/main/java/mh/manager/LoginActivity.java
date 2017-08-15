package mh.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Handler;

import mh.manager.lang.SharedPrefControl;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public HostApi hostApi;
    private final String ServerURL_Login = "login";

    private LoginDatabase sql;

    private EditText userName, passWord;
    private Button btnLogin;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    public String idUserName, userNameApi, email, dept_name, token, idStatus, nameStatus, stateStatus;
    private SharedPreferences pref;

    public String checkErrorApi;

    public RadioButton rdoVN, rdoEN;

    public  Handler handler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //remove title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        rdoVN = (RadioButton) findViewById(R.id.rdoVietNam);
        rdoEN = (RadioButton) findViewById(R.id.rdoEnglish);
        if(SharedPrefControl.readLang(getApplicationContext()).equals("vi")){
            rdoVN.setChecked(true);
            rdoEN.setChecked(false);
        }else{
            rdoVN.setChecked(false);
            rdoEN.setChecked(true);
        }
        SharedPrefControl.updateLangua(getApplicationContext());

        // nạp lại data layout
//        recreate();
        rdoVN.setOnClickListener(changeLang);
        rdoEN.setOnClickListener(changeLang);

        // khai bao host server data
        hostApi = new HostApi();

        sql = new LoginDatabase(this);
        sql.getWritableDatabase();

        userName = (EditText) findViewById(R.id.edtUserName);
        passWord = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        pref = getSharedPreferences("userrecord", 0);
        Boolean islogin = pref.getBoolean("userlogin", false);
        if(islogin){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // Lấy thông tin đăng nhập từ preferent
//        pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        String username = pref.getString(PREF_USERNAME, "username");
//        String passwordshare = pref.getString(PREF_PASSWORD, "password");
        userName.getText();
        passWord.getText();



    }

    /*
    change lngon ngu
    */
    private View.OnClickListener changeLang = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String lang = "";
            String country = "";
            switch (v.getId()) {

                case R.id.rdoVietNam:
                    Log.i("111","111");
                    lang = "vi";
                    break;
                case R.id.rdoEnglish:
                    Log.i("22","22");
                    lang = "en";
                    break;
            }
            SharedPrefControl.savingPreferences(getApplicationContext(), "lang", lang);
            SharedPrefControl.savingPreferences(getApplicationContext(), "country", country);
            SharedPrefControl.updateLangua(getApplicationContext());
            // nạp lại data layout
            recreate();

        }
    };
    // resume ứng dụng

    @Override
    protected void onResume() {
        // Lấy thông tin đăng nhập từ preferent
        pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        String username = pref.getString(PREF_USERNAME, "eng_staff1");
//        String passwordshare = pref.getString(PREF_PASSWORD, "123456");
//        userName.setText(username);
//        passWord.setText(passwordshare);
//        userName.getText();
//        passWord.getText();
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        try {
            // Kiểm tra thông tin nhập vào, nếu trống yêu cầu nhập lại
            if (userName.getText().toString().equalsIgnoreCase("") || passWord.getText().toString().equalsIgnoreCase("")){
                String mess = "Tên người dùng hoặc mật khẩu còn trống.Làm ơn nhập lại!";
            }else{
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .putString(PREF_USERNAME, userName.getText().toString())
                        .putString(PREF_PASSWORD, passWord.getText().toString())
                        .commit();
                // check();
                // Tạo mới một lớp CallUrl
                CallUrl wst = new CallUrl(CallUrl.POST_TASK, this, "Checking...");
                // Thêm data
                wst.addNameValuePair("username", userName.getText().toString().trim());
                wst.addNameValuePair("password",passWord.getText().toString().trim());
                // Gửi lên server
                wst.execute(new String[] {
                        // Đường dẫn đến server
                        hostApi.hostApi+ServerURL_Login
                });
                Log.i("logindata ====>", String.valueOf(wst));
                // Trường hợp nhận được khi thiết bị không có kết nối mạng,
                // hoặc server có sự cố
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CallUrl extends AsyncTask<String, Integer, String> {
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
        public CallUrl(int taskType, Context mContext, String processMessage) {
            this.taskType = taskType;
            this.mContext = mContext;
            this.processMessage = processMessage;
        }
        @Override
        protected void onPostExecute(String response) {
            progressDialog.dismiss();
            sql.clearTable("login");
            if(!response.equals("fail")){
                try {
                    // Dùng Json đọc thông tin nhận được từ server
                    JSONObject json_data = new JSONObject(response);
                    JSONArray jArray = new JSONArray(json_data.getString("datas"));
                    Log.i("tocken===>", String.valueOf(jArray));
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        idUserName = jObject.getString("staff_id");
                        userNameApi = jObject.getString("username");
                        dept_name = jObject.getString("dept_name");
                        email = jObject.getString("email");
                        token = jObject.getString("token");
                    }
//                    Log.i("aBoolean===>", "trả về loagin thanh cong hay khongdsd       "+ idUserName + "----" + userNameApi + "----" +email + "----" +dept_name);
                    sql.insertLoain(idUserName, userNameApi, email, dept_name, token);

                    JSONArray jStatus = new JSONArray(json_data.getString("status"));
//                    Log.i("jStatusjStatus===>", String.valueOf(jStatus));
                    for (int z = 0; z < jStatus.length(); z++) {
                        JSONObject jb = jStatus.getJSONObject(z);
//                        Log.i("jStatusjStatus===>", String.valueOf(jStatus.getJSONObject(z)));
                        idStatus = jb.getString("id");
                        nameStatus = jb.getString("name");
                        stateStatus = jb.getString("state");
                        sql.insertStatus(idStatus, nameStatus, stateStatus);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(mContext, "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
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
            this.progressDialog = ProgressDialog.show(mContext, "",processMessage);
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
            HttpResponse response = null;
            try {
                switch (taskType) {
                    // kiểm tra tác vụ cần thực hiển
                    // post gửi yêu cầu kèm thông tin
                    // Get gửi yêu cầu
                    case POST_TASK:
                        HttpPost httppost = new HttpPost(url);
                        // Add parameters
                        httppost.setEntity(new UrlEncodedFormEntity(params));
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
}