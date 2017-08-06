package mh.manager.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by man.ha on 7/27/2017.
 */

public class CallUrlUpdateDetail extends AsyncTask<String, Integer, String> {

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
    public CallUrlUpdateDetail(int taskType, Context mContext, String processMessage) {

        this.taskType = taskType;
        this.mContext = mContext;
        this.processMessage = processMessage;

    }

    @Override
    protected void onPostExecute(String response) {
        progressDialog.dismiss();
        Log.i("trang thai cap nhat", response);

//            if(!response.equals("fail")){
//                Toast.makeText(mContext, "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(mContext, "Đăng nhập thất bại, vui lòng kiểm tra lại",Toast.LENGTH_SHORT).show();
//            }


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
