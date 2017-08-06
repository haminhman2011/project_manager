package mh.manager.dialog;

import android.app.Activity;
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
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mh.manager.HostApi;
import mh.manager.R;
import mh.manager.jsonfuntions.JsonLoadStatus;
import mh.manager.models.ModelAgent;
import mh.manager.models.ModelTeam;

/**
 * Created by man.ha on 7/28/2017.
 */

public class TeamActivity extends Activity{
    public HostApi hostApi;
    private final static String url_page = "get-list-team";
    private final static String url_teamId= "?teamId";
    public ArrayList<ModelTeam> modelTeams;
    public ArrayList<String> arrTeams;
    public Spinner spAgent;
    JSONObject jsonobject;
    JSONArray jsonarray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_change_team);
        spAgent = (Spinner) findViewById(R.id.snTeam);

        new DownloadJSON().execute();



    }

    public class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            hostApi = new HostApi();
            modelTeams = new ArrayList<>();
            // Create an array to populate the spinner
            arrTeams = new ArrayList<>();
            // JSON file URL address
            jsonobject = JsonLoadStatus.getJSONfromURL(hostApi.hostApi+url_page+url_teamId);

            try {
                // Locate the NodeList name
                jsonarray = jsonobject.getJSONArray("datas");
                for (int i = 0; i < jsonarray.length(); i++) {
//                    Log.i("id status==================>", jsonobject.optString("id") + "===="+ jsonobject.optString("name"));
                    jsonobject = jsonarray.getJSONObject(i);
                    ModelTeam data = new ModelTeam();
                    data.setTeam_id(!jsonobject.getString("team_id").equals("null") ? jsonobject.getString("team_id") : "");
                    data.setLead_id(!jsonobject.getString("lead_id").equals("null") ? jsonobject.getString("lead_id") : "");
                    data.setFlags(!jsonobject.getString("flags").equals("null") ? jsonobject.getString("flags") : "");
                    data.setName(!jsonobject.getString("name").equals("null") ? jsonobject.getString("name") : "");
                    data.setNotes(!jsonobject.getString("notes").equals("null") ? jsonobject.getString("notes") : "");
                    data.setCreated(!jsonobject.getString("created").equals("null") ? jsonobject.getString("created") : "");
                    data.setUpdated(!jsonobject.getString("updated").equals("null") ? jsonobject.getString("updated") : "");
                    modelTeams.add(data);
                    // Populate spinner with country names
                    arrTeams.add(jsonobject.optString("name"));

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Locate the spinner in activity_main.xml

            // Spinner adapter
            spAgent .setAdapter(new ArrayAdapter<>(TeamActivity.this,android.R.layout.simple_spinner_dropdown_item, arrTeams));
            // Spinner on item click listener
            spAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    // Locate the textviews in activity_main.xml
//                    TextView idStatus = (TextView) findViewById(R.id.idStatusDetailOPen);
//                    // Set the text followed by the position
//                    idStatus.setText(status.get(position).getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }
}
