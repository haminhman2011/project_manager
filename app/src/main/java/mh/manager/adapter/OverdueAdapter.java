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

import java.util.List;

import mh.manager.R;
import mh.manager.format.FormatFont;
import mh.manager.models.ModelOverdue;

/**
 * Created by man.ha on 7/27/2017.
 */

public class OverdueAdapter extends ArrayAdapter<ModelOverdue> {
    public FormatFont formatFont;
    private Activity activity;

    public OverdueAdapter(Activity activity, int resource,List<ModelOverdue> listData) {
        super(activity, resource, listData);
        this.activity = activity;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        formatFont = new FormatFont();
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_listview_overdue, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position %2 == 1)
        {
            // Set a background color for ListView regular row/item
            convertView.setBackgroundColor(Color.parseColor("#dddddd"));
        }
        else
        {
            // Set the background color for alternate row/item
            convertView.setBackgroundColor(Color.parseColor("#fafafa"));
        }
        ModelOverdue data = getItem(position);
        holder.tvTicketId.setText(data.getNumber()); // ticket id duoc lay từ api "number"
        holder.tvOverdueStatus.setText(formatFont.formatFont(data.getStatus()));
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
        private TextView tvTicketId, tvLastUpdated, tvOverdueStatus, tvPriority, tvCreatedDate, tvTopic;

        public ViewHolder(View v) {
            tvTicketId = (TextView) v.findViewById(R.id.tvOverdueNumber);
            tvLastUpdated = (TextView) v.findViewById(R.id.tvOverdueLastUpdated);
            tvPriority = (TextView) v.findViewById(R.id.tvOverduePriority);
            tvOverdueStatus = (TextView) v.findViewById(R.id.tvOverdueStatus);
            tvCreatedDate = (TextView) v.findViewById(R.id.tvOverdueCreatedDate);
            tvTopic = (TextView) v.findViewById(R.id.tvOverdueTopic);
        }
    }

}

