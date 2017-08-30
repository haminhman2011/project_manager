package mh.manager.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import mh.manager.DetailNotiActivity;
import mh.manager.DetailOpenActivity;
import mh.manager.HttpHandler;

/**
 * Created by DEV on 8/18/2017.
 */

public class LoadDataServerTicketTheadNoti extends AsyncTask<Void, Void, String> {
    private Activity activity;
    private String url;
    private ProgressDialog dialog;

    public LoadDataServerTicketTheadNoti(Activity activity, String url) {
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
//        Log.i("jsonStr==>", jsonStr);
        return jsonStr;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (dialog.isShowing())
            dialog.dismiss();
//        Log.i("jsonStrsss==>", result);
        ((DetailNotiActivity) activity).parseJsonResponseTickedThread(result);
    }

}

