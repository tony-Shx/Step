package com.example.henu.step.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.henu.step.Bean.Setting;
import com.example.henu.step.R;

import java.util.List;

/**
 * Created by Administrator on 2017/4/4.
 */

public class settingListAdapter extends ArrayAdapter<Setting> {
    private int resourceId;
    private List<Setting> settingData;
    private Context context;
    private View view;
    private ViewHolder viewHolder;

    public settingListAdapter(Context context, int listViewResourceId, List<Setting> objects){
        super(context,listViewResourceId,objects);
        this.context = context;
        this.resourceId = listViewResourceId;
        settingData = objects;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Setting setting = getItem(position);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            view = inflater.inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.settingName=(TextView)view.findViewById(R.id.setting_name);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.settingName.setText(setting.getName());
        return view;
    }
    class  ViewHolder{
        TextView settingName;
    }
}
