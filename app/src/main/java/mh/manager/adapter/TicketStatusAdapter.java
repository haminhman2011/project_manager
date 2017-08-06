package mh.manager.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mh.manager.R;
import mh.manager.models.ModelStatus;

/**
 * Created by man.ha on 7/27/2017.
 */

public class TicketStatusAdapter extends ArrayAdapter<ModelStatus> {
    private Activity activity;
    private List<ModelStatus> modelStatusList;



    public TicketStatusAdapter(Activity activity, int txtViewResourceId, List<ModelStatus> modelStatusList){
        super(activity, txtViewResourceId, modelStatusList);
        this.activity = activity;
        this.modelStatusList = modelStatusList;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_ticket_status, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.tvSticketStatus);

        ModelStatus model = getItem(position);
        textView.setText(model.getName());

        return convertView;
    }


}

