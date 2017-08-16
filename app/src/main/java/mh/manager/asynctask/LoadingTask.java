package mh.manager.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * Created by DEV on 8/11/2017.
 */

public class LoadingTask extends AsyncTask<String, Integer, Integer> {

public interface LoadingTaskFinishedListener {
    void onTaskFinished();
}

    private final ProgressBar progressBar;

    private final LoadingTaskFinishedListener finishedListener;

    public LoadingTask(ProgressBar progressBar, LoadingTaskFinishedListener finishedListener) {
        this.progressBar = progressBar;
        this.finishedListener = finishedListener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        Log.i("Tutorial", "Starting task with url: "+params[0]);
        if(resourcesDontAlreadyExist()){
            downloadResources();
        }
        return 1234;
    }

    private boolean resourcesDontAlreadyExist() {
        return true; // returning true so we show the splash every time
    }


    private void downloadResources() {
        int count = 3;
        for (int i = 0; i < count; i++) {
            // Update the progress bar after every step
            int progress = (int) ((i / (float) count) * 100);
            publishProgress(progress);
            // Do some long loading things
            try { Thread.sleep(500); } catch (InterruptedException ignore) {}
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(values[0]); // This is ran on the UI thread so it is ok to update our progress bar ( a UI view ) here
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        finishedListener.onTaskFinished(); // Tell whoever was listening we have finished
    }
}
