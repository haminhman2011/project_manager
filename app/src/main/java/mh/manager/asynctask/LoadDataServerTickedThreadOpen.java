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

import mh.manager.DetailOpenActivity;
import mh.manager.HttpHandler;

/**
 * Created by man.ha on 7/27/2017.
 */

public class LoadDataServerTickedThreadOpen extends AsyncTask<Void, Void, String> {
    private Activity activity;
    private String url;
    private ProgressDialog dialog;

    public LoadDataServerTickedThreadOpen(Activity activity, String url) {
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
        dialog.setTitle("Processing...");
        // Set progress dialog message
        dialog.setMessage("Processing...");
        dialog.setIndeterminate(false);
        // Show progress dialog
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url);
        Log.i("jsonStr==>", jsonStr);
        Log.i("mess", url);
        return jsonStr;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (dialog.isShowing())
            dialog.dismiss();
        Log.i("jsonStrsss==>", result);
        ((DetailOpenActivity) activity).parseJsonResponseTickedThread(result);
    }

}
