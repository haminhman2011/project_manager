package mh.manager.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import mh.manager.DetailOverdueActivity;
import mh.manager.service.HttpHandler;

/**
 * Created by man.ha on 8/3/2017.
 */

public class LoadDataServerTickedThreadOverdue extends AsyncTask<Void, Void, String> {
    private Activity activity;
    private String url;
    private ProgressDialog dialog;

    public LoadDataServerTickedThreadOverdue(Activity activity, String url) {
        super();
        this.activity = activity;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Dữ liệu đang được tải");
        dialog.setMessage("Tải dữ liệu...");
        dialog.setIndeterminate(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpHandler sh = new HttpHandler();
        return sh.makeServiceCall(url);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (dialog.isShowing())
            dialog.dismiss();
        ((DetailOverdueActivity) activity).parseJsonResponseTickedThread(result);
    }
}
