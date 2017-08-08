package mh.manager.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mh.manager.HostApi;
import mh.manager.LoginDatabase;
import mh.manager.R;
import mh.manager.asynctask.CallUrlUpdateDetail;
import mh.manager.format.FormatFont;
import mh.manager.jsonfuntions.JsonLoadStatus;
import mh.manager.models.ModelAgent;

/**
 * Created by man.ha on 7/28/2017.
 */

public class AgentActivity extends Activity{
    public HostApi hostApi;
    private LoginDatabase sql;
    private final static String url_page = "get-list-agent";
    private final static String url_deptId= "?deptId=";

    public ArrayList<ModelAgent> modelAgents;
    public ArrayList<String> arrAgent;
    public Spinner spAgent;
    JSONObject jsonobject;
    JSONArray jsonarray;
    public TextView titleTeam, tvIdAgent;
    public String departmentId, ticketId, staffAssignedId;
    public Button btnAssignAgent, btnCancel, btnClose;
    public EditText edtNoteDialog;
    public FormatFont formatFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_change_agent);

        sql = new LoginDatabase(this);
        sql.getWritableDatabase();
        try {
            for(int i=0; i<sql.getInforUser().length(); i++){
                JSONObject obj = sql.getInforUser().getJSONObject(i);
                staffAssignedId = obj.getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();

        tvIdAgent = (TextView) findViewById(R.id.tvIdAgent);
        edtNoteDialog= (EditText) findViewById(R.id.edtNoteDialog);
        titleTeam = (TextView) findViewById(R.id.titleTeam);
        if(bd != null){
            titleTeam.setText(String.valueOf(bd.get("nameTeam")));
            departmentId = bd.getString("departmentId");
            ticketId = bd.getString("ticketId");
        }

        spAgent = (Spinner) findViewById(R.id.spnAgent);

        new DownloadJSON().execute();

        btnAssignAgent = (Button) findViewById(R.id.btnAssignAgent);
        btnAssignAgent.setOnClickListener(onClickAssign);

        btnClose = (Button) findViewById(R.id.close_button);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public View.OnClickListener onClickAssign = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String strStaffId;
            strStaffId = String.valueOf(tvIdAgent.getText());
            // Tạo mới một lớp CallUrl
            CallUrlUpdateDetail wst = new CallUrlUpdateDetail(CallUrlUpdateDetail.POST_TASK, AgentActivity.this, "Checking...");
            wst.addNameValuePair("staffId",strStaffId);
            wst.addNameValuePair("ticketId",ticketId);
            wst.addNameValuePair("teamId","");
            wst.addNameValuePair("staffAssignedId",staffAssignedId);
            Log.i("assign============>", "staffId "+strStaffId+"------------"+"ticketId "+ticketId+"-----------------"+"teamId"+"----------"+"staffAssignedId "+staffAssignedId);

            wst.execute(new String[] { hostApi.hostApi+"assign-ticket"});
            Toast.makeText(AgentActivity.this, "Assign sucess", Toast.LENGTH_SHORT).show();
        }
    };

    public class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            hostApi = new HostApi();
            formatFont = new FormatFont();
            modelAgents = new ArrayList<>();
            // Create an array to populate the spinner
            arrAgent = new ArrayList<>();
            // JSON file URL address
            jsonobject = JsonLoadStatus.getJSONfromURL(hostApi.hostApi+url_page+url_deptId+departmentId);

            try {
                // Locate the NodeList name
                jsonarray = jsonobject.getJSONArray("datas");
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    ModelAgent data = new ModelAgent();
                    data.setStaff_id(!jsonobject.getString("staff_id").equals("null") ? jsonobject.getString("staff_id") : "");
                    data.setUsername(!jsonobject.getString("username").equals("null") ? jsonobject.getString("username") : "");
                    data.setFirstname(!jsonobject.getString("firstname").equals("null") ? jsonobject.getString("firstname") : "");
                    data.setLastname(!jsonobject.getString("lastname").equals("null") ? jsonobject.getString("lastname") : "");
                    modelAgents.add(data);
                    // Populate spinner with country names
                    arrAgent.add(formatFont.formatFont(jsonobject.optString("firstname")) + " " + formatFont.formatFont(jsonobject.optString("lastname")));

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            spAgent .setAdapter(new ArrayAdapter<>(AgentActivity.this,android.R.layout.simple_spinner_dropdown_item, arrAgent));
            spAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvIdAgent = (TextView) findViewById(R.id.tvIdAgent);
                    tvIdAgent.setVisibility(View.GONE);
                    tvIdAgent.setText(modelAgents.get(position).getStaff_id());
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }
}
