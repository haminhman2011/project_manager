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
import mh.manager.models.ModelClose;

/**
 * Created by man.ha on 7/27/2017.
 */

public class CloseAdapter extends ArrayAdapter<ModelClose> {
    public FormatFont formatFont;
    private Activity activity;

    public CloseAdapter(Activity activity, int resource,List<ModelClose> listData) {
        super(activity, resource, listData);
        this.activity = activity;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        formatFont = new FormatFont();
        CloseAdapter.ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_listview_close, parent, false);
            holder = new CloseAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (CloseAdapter.ViewHolder) convertView.getTag();
        }
        if(position %2 == 1){
            // Set a background color for ListView regular row/item
            convertView.setBackgroundColor(Color.parseColor("#dddddd"));
        }else{
            // Set the background color for alternate row/item
            convertView.setBackgroundColor(Color.parseColor("#fafafa"));
        }
        ModelClose data = getItem(position);
        holder.tvTicketId.setText(data.getNumber());
        holder.tvClosedStatus.setText(formatFont.formatFont(data.getStatus()));
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
        private TextView tvTicketId, tvLastUpdated, tvClosedStatus, tvPriority, tvCreatedDate, tvTopic;

        public ViewHolder(View v) {
            tvTicketId = (TextView) v.findViewById(R.id.tvCloseNumber);
            tvLastUpdated = (TextView) v.findViewById(R.id.tvCloseLastUpdated);
            tvPriority = (TextView) v.findViewById(R.id.tvClosePriority);
            tvClosedStatus = (TextView) v.findViewById(R.id.tvClosedStatus);
            tvCreatedDate = (TextView) v.findViewById(R.id.tvCloseCreatedDate);
            tvTopic = (TextView) v.findViewById(R.id.tvCloseTopic);
        }
    }


}
