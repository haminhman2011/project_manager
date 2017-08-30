package mh.manager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import mh.manager.asynctask.LoadingTask;
import mh.manager.lang.SharedPrefControl;

public class Loading extends AppCompatActivity implements LoadingTask.LoadingTaskFinishedListener {

    private LoginDatabase sql;
    public  Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_loading);
        SharedPrefControl.updateLangua(getApplicationContext());
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activity_splash_progress_bar);
        // Start your loading
        new LoadingTask(progressBar, this).execute("www.google.co.uk"); // Pass in whatever you need a url is just an example we don't use it in this tutorial
    }

    @Override
    public void onTaskFinished() {
        completeSplash();
    }

    private void completeSplash(){
        startApp();
        finish(); // Don't forget to finish this Splash Activity so the user can't return to it!
    }

    private void startApp() {
        sql = new LoginDatabase(this);
        sql.getWritableDatabase();
        Log.i("remember me,", String.valueOf(sql.getCountData()));
        if(sql.getCountData() > 0){
            intent = new Intent(Loading.this, MainActivity.class);
            startActivity(intent);
        }else {
            intent = new Intent(Loading.this, LoginActivity.class);
            startActivity(intent);
        }

    }
}
