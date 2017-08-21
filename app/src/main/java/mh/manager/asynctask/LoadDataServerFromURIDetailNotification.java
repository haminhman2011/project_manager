package mh.manager.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import mh.manager.DetailNotiActivity;
import mh.manager.DetailOpenActivity;

/**
 * Created by DEV on 8/18/2017.
 */

public class LoadDataServerFromURIDetailNotification extends AsyncTask<String, Void, String> {
    public String Strurl, ticketId, staffId, token, agentId;
    private Activity activity;
    private ProgressDialog dialog;

    public LoadDataServerFromURIDetailNotification(Activity activity,String Strurl, String ticketId, String staffId, String token, String agentId) {
        super();
        this.activity = activity;
        this.Strurl = Strurl;
        this.ticketId = ticketId;
        this.staffId = staffId;
        this.token = token;
        this.agentId = agentId;
    }
    protected void onPreExecute(){
        super.onPreExecute();
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Dữ liệu đang được tải");
        dialog.setMessage("Tải dữ liệu...");
        dialog.setIndeterminate(false);
        dialog.show();
    }

    protected String doInBackground(String... arg0) {
        try {
            URL url = new URL("http://demo.cloudteam.vn:8080/cocobay_manager/api/get-ticket-detail"); // here is your URL path
            Log.i("Strurl", String.valueOf(url));
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("ticketNumber", "156293");
            postDataParams.put("staffId", "3");
            postDataParams.put("token", "ieEpY4LZ2nh_poAutA7WdWwq1u79bNm2");
            postDataParams.put("agentId", "3");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                Log.i("váo 111", "1111");
                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";
                while((line = in.readLine()) != null) {
                    Log.i("váo 222", "222");
                    Log.i("váo 222", line);
                    sb.append(line);
                    break;
                }
                in.close();

                Log.i("sb.toString()", sb.toString());

                return sb.toString();
            }
            else {
                return new String("false : "+responseCode);
            }
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        dialog.dismiss();
        Log.i("resul===>", result);
        ((DetailNotiActivity) activity).parseJsonResponse(result);
    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
}


