package mh.manager.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mh.manager.R;
import mh.manager.format.FormatFont;
import mh.manager.models.ModelDynamicDetailClosed;

/**
 * Created by man.ha on 7/27/2017.
 */

public class DynamicDetailClosedAdapter extends ArrayAdapter<ModelDynamicDetailClosed> {

    public FormatFont formatFont;
    private Activity activity;

    public DynamicDetailClosedAdapter(Activity activity, int resource,ArrayList<ModelDynamicDetailClosed> listData) {
        super(activity, resource, listData);
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        formatFont = new FormatFont();
        View vi = convertView;
        LinearLayout llLeft, llRight;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(vi == null){
            vi = inflater.inflate(R.layout.item_dynamic_detail_closed, parent, false);
        }
        ModelDynamicDetailClosed data = getItem(position);

        llLeft = (LinearLayout)vi.findViewById(R.id.llDynamicClosedLeft);
        llRight = (LinearLayout)vi.findViewById(R.id.llDynamicClosedRight);

        // add text view
        TextView tv = new TextView(activity);
        tv.setId(R.id.tvLabelDetailClosed);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        tv.setTextSize(15);
        tv.setTextColor(Color.parseColor("#616161"));
        tv.setText(formatFont.formatFont(data.getLabel()));

        TextView tvEntryId = new TextView(activity);
        tvEntryId.setId(R.id.tvEntryId);
        tvEntryId.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvEntryId.setText(formatFont.formatFont(data.getEntry_id()));
        tvEntryId.setVisibility(View.GONE);

        TextView tvFieldId = new TextView(activity);
        tvFieldId.setId(R.id.tvFieldId);
        tvFieldId.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvFieldId.setText(formatFont.formatFont(data.getField_id()));
        tvFieldId.setVisibility(View.GONE);

        EditText ed = new EditText(activity);
        ed.setId(R.id.edtNameDetailClosed);
        ed.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ed.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        ed.setTextSize(15);
        ed.setPadding(15, 0, 15, 0);
        ed.setHeight(60);
        ed.setBackgroundResource(R.drawable.rounded_border_edittext);
        ed.setTextColor(Color.parseColor("#616161"));
        ed.setText(formatFont.formatFont(data.getValue()));


        llLeft.addView(tv);
        llLeft.addView(tvEntryId);
        llLeft.addView(tvFieldId);
        llRight.addView(ed);

        return vi;
    }
}

