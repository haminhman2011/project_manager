package mh.manager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mh.manager.HostApi;
import mh.manager.LoginDatabase;
import mh.manager.MainActivity;
import mh.manager.R;

/**
 * Created by man.ha on 7/27/2017.
 */

public class NotificationServices extends Service {
        private NotificationManager mNotificationManager;
        private int notificationID = 100;
        private int numMessages = 0;
        public Handler handler;
        private Looper mServiceLooper;
        public int intTask;

        public JSONArray  haminhman;

        public Context context;

        private LoginDatabase sql;
        private final static String url_page = "get-list-ticket?statusId=1";
        private final static String url_staffId = "&staffId=";
        private final static String url_token = "&token=";
        private final static String url_agentId = "&agentId=";

        public String staffId, agentId, token;
        public HostApi hostApi;





        private class GetContacts extends AsyncTask<Void, Void, Void> {



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                hostApi  = new HostApi();
                sql = new LoginDatabase(getBaseContext());
                sql.getWritableDatabase();
//                try {
//                    for(int i=0; i<sql.getInforUser().length(); i++){
//                        JSONObject obj = sql.getInforUser().getJSONObject(i);
//                        staffId = obj.getString("id");
//                        agentId = obj.getString("id");
//                        token = obj.getString("token");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                Log.i("staffId", sql.getId());

                HttpHandler sh = new HttpHandler();
                Log.i("noti", hostApi.hostApi+"get-noti?staffId="+sql.getId());

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(hostApi.hostApi+"get-noti?staffId="+sql.getId());
//            Log.e(TAG, "Response from url: " + jsonStr);hostApi+url_page+url_staffId+staffId+url_token+token+url_agentId+agentId

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        // Getting JSON Array node
                        JSONArray contacts = jsonObj.getJSONArray("datas");
                        intTask = contacts.length();

                        if ( contacts.length() > 0){
                            int i;
//            Toast.makeText(getApplicationContext(), String.valueOf(intTask), Toast.LENGTH_SHORT).show();
                            for ( i= 0; i < contacts.length(); i++) {
                                JSONObject jObject = contacts.getJSONObject(i);
                                if(!jObject.getString("code_name").equals("null")){
                                    Log.i("dem thu tu", String.valueOf(i));
                                    Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
                                    //Define sound URI
                                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    mBuilder.setContentTitle("Tin nhắn mới");
                                    mBuilder.setContentText(jObject.getString("message"));
                                    mBuilder.setTicker("Thông báo!");
//                                    mBuilder.setSound(soundUri);
                                    if(jObject.getString("code_name").equals("assigned.alert")){
                                        mBuilder.setSmallIcon(R.drawable.icon_assign_16);
                                    }else if(jObject.getString("code_name").equals("ticket.alert")){
                                        mBuilder.setSmallIcon(R.drawable.icon_new_16);
                                    }else if(jObject.getString("code_name").equals("ticket.overdue")){
                                        mBuilder.setSmallIcon(R.drawable.icon_timeout_16);
                                    }
            /* tăng số thông báo */
    //            mBuilder.setNumber(++numMessages);
                /* Tạo đối tượng chỉ đến activity sẽ mở khi chọn thông báo */
                                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                                    stackBuilder.addParentStack(MainActivity.class);
                /* Đăng ký activity được gọi khi chọn thông báo */
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(resultPendingIntent);
                                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    /* cập nhật thông báo */
                                    mNotificationManager.notify(++notificationID, mBuilder.build());
                                }else{
                                    Log.i("dữ liệu null", "dữ liệu lỗi hoặc là dữ liệu test");
                                }
                            }


                        }
                        else{
                            Log.i("Cập nhcông việc==>", String.valueOf(intTask));
                        }

                        Log.i("so luong data===>", String.valueOf(contacts.length()));
                    } catch (final JSONException e) {
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




    public void displayNotification(JSONArray haminhman) {


    }
}
