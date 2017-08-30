package mh.manager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import mh.manager.DetailNotiActivity;
import mh.manager.HostApi;
import mh.manager.LoginDatabase;
import mh.manager.MainActivity;
import mh.manager.OpenFragment;
import mh.manager.R;
import mh.manager.adapter.OpenAdapter;
import mh.manager.models.ModelOpen;

/**
 * Created by man.ha on 7/27/2017.
 */

public class NotificationServices extends Service {
    public Notification.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private int notificationID = 001;
    private int numMessages = 0;
    public Handler handler;
    private Looper mServiceLooper;
    public int intTask;

    public JSONArray haminhman;

    public Context context;
    private ArrayList<ModelOpen> dataOpens;

    private LoginDatabase sql;
    private final static String url_page = "get-list-ticket?statusId=1";
    private final static String url_staffId = "&staffId=";
    private final static String url_token = "&token=";
    private final static String url_agentId = "&agentId=";

    public String staffId, agentId, token;
    public HostApi hostApi;

    public OpenAdapter adapter;


    private class GetContacts extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            hostApi = new HostApi();
            sql = new LoginDatabase(getBaseContext());
            sql.getWritableDatabase();
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(hostApi.hostApi + "get-noti?staffId=" + sql.getId());
                Log.i("notification===.link:", hostApi.hostApi+"get-noti?staffId="+sql.getId());
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("datas");
                    intTask = contacts.length();

                    if (contacts.length() > 0) {
                        Log.i("so luong data===>", String.valueOf(contacts.length()));
                        int i;
                        for (i = 0; i < contacts.length(); i++) {
//                            Log.i("dem thu tu", String.valueOf(i));
                            JSONObject jObject = contacts.getJSONObject(i);

                            if (!jObject.getString("code_name").equals("null")) {
                                Log.i("dem thu tu", String.valueOf(i + "numeber"));

                                mBuilder = new Notification.Builder(getApplicationContext());
                                //Define sound URI
                                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                                mBuilder.setContentTitle("Tin nhắn mới");
                                mBuilder.setContentText(jObject.getString("message"));
                                mBuilder.setTicker("Thông báo!");
                                mBuilder.setAutoCancel(true);

//                                    mBuilder.setSound(soundUri);
                                if (jObject.getString("code_name").equals("assigned.alert")) {
                                    mBuilder.setSmallIcon(R.drawable.icon_assign_16);
                                } else if (jObject.getString("code_name").equals("ticket.alert")) {
                                    mBuilder.setSmallIcon(R.drawable.icon_new_16);
                                } else if (jObject.getString("code_name").equals("ticket.overdue")) {
                                    mBuilder.setSmallIcon(R.drawable.icon_timeout_16);
                                }else if(jObject.getString("code_name").equals("note.alert")){
                                    mBuilder.setSmallIcon(R.drawable.icon_note_16);
                                }else if(jObject.getString("code_name").equals("transfer.alert")){
                                    mBuilder.setSmallIcon(R.drawable.icon_transfer_16);
                                }else{
                                    mBuilder.setSmallIcon(R.drawable.icon_assign_16);
                                }
            /* tăng số thông báo */
                                //            mBuilder.setNumber(++numMessages);

                                Intent resultIntent = new Intent(getApplicationContext(), DetailNotiActivity.class);
                                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                dataOpens = new ArrayList<>();


                                   Log.i("aaaaaaaaaaaaaa", hostApi.hostApi+sql.getLinkDetail()+"&ticketNumber="+jObject.getString("ticketNumber"));
//                                getDataFromUrl(hostApi.hostApi + sql.getLinkDetail() + "&ticketNumber=" + jObject.getString("ticketNumber"));
                                resultIntent.putExtra("Item", hostApi.hostApi + sql.getLinkDetail() + "&ticketNumber=" + jObject.getString("ticketNumber"));
                                resultIntent.putExtra("ticketNumber", jObject.getString("ticketNumber"));
                                resultIntent.putExtra("deptId", jObject.getString("deptId"));
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                                stackBuilder.addParentStack(DetailNotiActivity.class);
                                stackBuilder.addNextIntent(resultIntent);
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), i,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                                mBuilder.setContentIntent(resultPendingIntent);

                                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                mNotificationManager.notify(i, mBuilder.build());

                            } else {
                                Log.i("dữ liệu null", "dữ liệu lỗi hoặc là dữ liệu test");

                            }
                        }
                    } else {

                        Log.i("Cập nhcông việc==>", String.valueOf(intTask));
//                        Log.i("dem bien noti==>", String.valueOf(++notificationID));
                    }


                } catch (final JSONException e) {
                    Log.i("noti", "loi");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
        }
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            public void run() {
                new GetContacts().execute();

                handler.postDelayed(this, 60000);
            }
        };
        handler.postDelayed(r, 60000);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
