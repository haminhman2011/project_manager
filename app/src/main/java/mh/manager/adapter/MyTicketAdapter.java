package mh.manager.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mh.manager.R;
import mh.manager.format.FormatFont;
import mh.manager.models.ModelMyTicket;
import mh.manager.models.ModelOpen;

/**
 * Created by man.ha on 8/4/2017.
 */

public class MyTicketAdapter extends ArrayAdapter<ModelMyTicket>{
    public FormatFont formatFont;
    private Activity activity;
    public MyTicketAdapter(Activity activity, int resource,ArrayList<ModelMyTicket> listData) {
        super(activity, resource, listData);
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        formatFont = new FormatFont();
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if(convertView == null){
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_listview_myticket, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        }else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }
        if(position %2 == 1){
            // Set a background color for ListView regular row/item
            convertView.setBackgroundColor(Color.parseColor("#dddddd"));
        }else{
            // Set the background color for alternate row/item
            convertView.setBackgroundColor(Color.parseColor("#fafafa"));
        }
        ModelMyTicket data = getItem(position);
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
            tvTicketId = (TextView) v.findViewById(R.id.tvNumber);
            tvLastUpdated = (TextView) v.findViewById(R.id.tvLastUpdated);
            tvPriority = (TextView) v.findViewById(R.id.tvPriority);
            tvOpenStatus = (TextView) v.findViewById(R.id.tvStatus);
            tvCreatedDate = (TextView) v.findViewById(R.id.tvCreatedDate);
            tvTopic = (TextView) v.findViewById(R.id.tvTopic);
        }
    }
}
