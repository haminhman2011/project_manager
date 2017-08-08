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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mh.manager.HostApi;
import mh.manager.LoginDatabase;
import mh.manager.R;
import mh.manager.format.FormatFont;
import mh.manager.jsonfuntions.JsonLoadStatus;
import mh.manager.models.ModelAgent;
import mh.manager.models.ModelDepartment;

public class TransferActivity extends Activity {
    public HostApi hostApi;
    private LoginDatabase sql;
    public FormatFont formatFont;

    public ArrayList<ModelDepartment> modelDepartments;
    public ArrayList<String> arrDepartments;
    public Spinner spnTransfer;
    JSONObject jsonobject;
    JSONArray jsonarray;
    public TextView titleTeam, tvIdAgent;
    public String departmentId, ticketId, departmentName;
    public Button btnAssignAgent, btnCancel, btnClose;
    public EditText edtNoteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_transfer);

        btnClose = (Button) findViewById(R.id.close_button);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null){
            departmentName = bd.getString("departmentName");
        }

        spnTransfer = (Spinner) findViewById(R.id.spnTransfer);

        new DownloadJSON().execute();
    }

    public class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            hostApi = new HostApi();
            formatFont = new FormatFont();
            modelDepartments = new ArrayList<>();
            // Create an array to populate the spinner
            arrDepartments = new ArrayList<>();
            // JSON file URL address
            jsonobject = JsonLoadStatus.getJSONfromURL(hostApi.hostApi+"get-list-department");

            try {
                // Locate the NodeList name
                jsonarray = jsonobject.getJSONArray("datas");
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    ModelDepartment data = new ModelDepartment();
                    data.setId(!jsonobject.getString("id").equals("null") ? jsonobject.getString("id") : "");
                    data.setName(!jsonobject.getString("name").equals("null") ? jsonobject.getString("name") : "");
                    modelDepartments.add(data);
                    // Populate spinner with country names
                    arrDepartments.add(formatFont.formatFont(jsonobject.optString("name")));

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(TransferActivity.this,android.R.layout.simple_spinner_dropdown_item, arrDepartments);
            spnTransfer .setAdapter(adapter);
            for(int i=0; i < adapter.getCount(); i++) {
                if(departmentName.trim().equals(adapter.getItem(i).toString())){
                    spnTransfer.setSelection(i);
                    break;
                }
            }
            spnTransfer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    TextView tvIdTransfer = (TextView) findViewById(R.id.tvIdTransfer);
                    tvIdTransfer.setVisibility(View.GONE);
                    tvIdTransfer.setText(modelDepartments.get(position).getId());
//                    ((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }
}
