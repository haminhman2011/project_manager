package mh.manager;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import mh.manager.asynctask.CallUrlUpdateDetail;
import mh.manager.lang.SharedPrefControl;

public class MainTestData extends AppCompatActivity implements View.OnClickListener{
    public View rdoVN, rdoEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test_data);

        rdoVN = findViewById(R.id.rdoVietNam);
        rdoEN = findViewById(R.id.rdoEnglish);
//        rdoEN.setChecked(true);
        rdoVN.setOnClickListener(this);
        rdoEN.setOnClickListener(this);

    }


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
}
