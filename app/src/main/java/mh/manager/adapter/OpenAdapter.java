package mh.manager.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mh.manager.R;
import mh.manager.format.FormatFont;
import mh.manager.models.ModelOpen;

/**
 * Created by man.ha on 7/27/2017.
 */

public class OpenAdapter extends ArrayAdapter<ModelOpen> {
    public FormatFont formatFont;
    private Activity activity;
    public OpenAdapter(Activity activity, int resource,ArrayList<ModelOpen> listData) {
        super(activity, resource, listData);
        this.activity = activity;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        formatFont = new FormatFont();
        OpenAdapter.ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if(convertView == null){
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_listview_open, parent, false);
            // get all UI view
            holder = new OpenAdapter.ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        }else {
            // if holder created, get tag from view
            holder = (OpenAdapter.ViewHolder) convertView.getTag();
        }
        if(position %2 == 1){
            // Set a background color for ListView regular row/item
            convertView.setBackgroundColor(Color.parseColor("#dddddd"));
        }else{
            // Set the background color for alternate row/item
            convertView.setBackgroundColor(Color.parseColor("#fafafa"));
        }
        ModelOpen data = getItem(position);
//        Log.i("adapterwww===>", String.valueOf(data.getNumber()));
        holder.tvTicketId.setText(data.getNumber()); // ticket id duoc lay từ api "number"
        holder.tvOpenStatus.setText(formatFont.formatFont(data.getStatus()));
        holder.tvTopic.setText(formatFont.formatFont(data.getTopicname()));
        holder.tvCreatedDate.setText(data.getCreated());
        holder.tvLastUpdated.setText(data.getLastupdate());
        if(data.getPriority().equals("emergency")){
            holder.tvPriority.setTextColor(Color.RED);
        }else if(data.getPriority().equals("low")){
            holder.tvPriority.setTextColor(Color.parseColor("#ffd76e"));
        }else if(data.getPriority().equals("high")){
            holder.tvPriority.setTextColor(Color.GREEN);
        }else{
            holder.tvPriority.setTextColor(Color.parseColor("#4c8cbe"));
        }
        holder.tvPriority.setText(data.getPriority());


        return convertView;
    }

    private static class ViewHolder {
        private TextView tvTicketId, tvLastUpdated, tvPriority, tvCreatedDate, tvTopic, tvOpenStatus;

        public ViewHolder(View v) {
            tvTicketId = (TextView) v.findViewById(R.id.tvOpenNumber);
            tvLastUpdated = (TextView) v.findViewById(R.id.tvOpenLastUpdated);
            tvPriority = (TextView) v.findViewById(R.id.tvOpenPriority);
            tvOpenStatus = (TextView) v.findViewById(R.id.tvOpenStatus);
            tvCreatedDate = (TextView) v.findViewById(R.id.tvOpenCreatedDate);
            tvTopic = (TextView) v.findViewById(R.id.tvOpenTopic);
        }
    }

}
