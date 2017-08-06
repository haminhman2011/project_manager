package mh.manager.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import mh.manager.OpenActivity;

/**
 * Created by man.ha on 7/27/2017.
 */

public class LoadDataServerFromURlTaskOpen extends AsyncTask<Void, Void, String> {
    private final static String TAG_ = LoadDataServerFromURlTaskOpen.class.getSimpleName();
    private Activity activity;
    private String url;
    private ProgressDialog dialog;
    public JSONObject json = null;


    public LoadDataServerFromURlTaskOpen(Activity activity, String url) {
        super();
        this.activity = activity;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Create a progress dialog
        dialog = new ProgressDialog(activity);
        // Set progress dialog title
        dialog.setTitle("Dữ liệu đang được tải");
        // Set progress dialog message
        dialog.setMessage("Tải dữ liệu...");
        dialog.setIndeterminate(false);
        // Show progress dialog
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        // call load JSON from url method
        Log.i("jsonjsonjso==>", loadJSON(this.url).toString());
        return loadJSON(this.url).toString();
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
    //    Log.i("resultresultresultresult==>", String.valueOf(result));
        dialog.dismiss();


        ((OpenActivity) activity).parseJsonResponse(result);
        Log.i(TAG_, result);
    }

    public JSONObject loadJSON(String url) {

        // Creating JSON Parser instance
        JSONParser jParser = new JSONParser();
        // getting JSON string from URL
        json = jParser.getJSONFromUrl(url);
      //  Log.i("loi======>", String.valueOf(json));
//        if (json != null && json.length() > 0){
//
//            Log.i("jsonjsonjsonjsonjsonjson==>", String.valueOf(json));
//        }else{
//            Log.i("jsonjsonjsonjsonjsonjson==>", "aaaaaaaaaaaa");
//            Intent intent = new Intent(activity, ErrorsActivity.class);
//            activity.startActivity(intent);
//            Toast.makeText(activity, "Lổi dữ liệu", Toast.LENGTH_SHORT).show();
//        }


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
