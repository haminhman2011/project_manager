package mh.manager;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import mh.manager.lang.SharedPrefControl;
import mh.manager.service.NotificationServices;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
    private LoginDatabase sql;


    TabHost tabHost;
    private TextView tvHello, tvEmail, tvDepartments;
    private Button btnLogOut;
    public Intent intService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
//        SharedPrefControl.updateLangua(getApplicationContext());

                 /* Time Lockout after 10 mins */
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                Log.i("dang xuat=========>","dang xuat");
//                Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//                finish();
//                return;
//            }
//        }, 10000);
        /* Time Lockout END */

        tvHello = (TextView) findViewById(R.id.tvHello);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvDepartments = (TextView) findViewById(R.id.tvDepartment);

        sql = new LoginDatabase(this);
        sql.getWritableDatabase();
        try {
//            Log.i("data login===>", String.valueOf(sql.getInforUser()));
            for(int i=0; i<sql.getInforUser().length(); i++){
                JSONObject obj = sql.getInforUser().getJSONObject(i);

                tvHello.setText(obj.getString("username"));
                tvEmail.setText(obj.getString("email"));
                tvDepartments.setText(obj.getString("departments"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //logout
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sql.clearTable("login");
                sql.clearTable("status");
                SharedPreferences SM = getSharedPreferences("userrecord", 0);
                SharedPreferences.Editor edit = SM.edit();
                edit.putBoolean("userlogin", false);
                edit.commit();


                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // end

        // set tab
        tabHost = getTabHost();
        setTabs();

        // load notification services
        intService = new Intent(this, NotificationServices.class);
        startService(intService);
    }


    private  void setTabs(){
        addTab("Open", R.drawable.tab_open, OpenActivity.class);
        addTab("My Tickets", R.drawable.tab_my_tickets, MyTicketsActivity.class);
        addTab("Overdue", R.drawable.tab_new, OverdueActivity.class);
        addTab("Closed", R.drawable.tab_close, ClosedActivity.class);

    }

    private void addTab(String labelId, int drawableId, Class<?> c){
        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_main, getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.titleDialogTeam);
        title.setText(labelId);
//        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
//        icon.setImageResource(drawableId);
        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
