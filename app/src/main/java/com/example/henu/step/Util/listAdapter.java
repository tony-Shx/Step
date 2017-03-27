package com.example.henu.step.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/13.
 */

public class listAdapter extends BaseAdapter {

	private ArrayList<Run> list;
	private Context context;
	private LayoutInflater inflater;
	public listAdapter(Context context, ArrayList<Run> list) {
		this.list = list;
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//加载布局为一个视图
		View view;
		ViewHolder viewHolder;
		if(convertView==null){
		    view=inflater.inflate(R.layout.listview_item,null);
			viewHolder = new ViewHolder();
			//在view视图中查找id为image_photo的控件
			viewHolder.listview_time= (TextView) view.findViewById(R.id.listview_time);
			viewHolder.listview_length= (TextView) view.findViewById(R.id.listview_length);
			viewHolder.listview_duration= (TextView) view.findViewById(R.id.listview_duration);
			viewHolder.listview_consume= (TextView) view.findViewById(R.id.listview_consume);
			viewHolder.listview_image_update_status = (ImageView) view.findViewById(R.id.listview_update_status);
			view.setTag(viewHolder);
		}else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		Run run=list.get(position);
		DecimalFormat df = new DecimalFormat("0.00");
		StringBuffer sb = new StringBuffer();
		sb.append(DateHelper.getInstance().changeDateToString(run.getStart_time()));
		sb.append(" - ");
		sb.append(DateHelper.getInstance().changeDateToString(run.getEnd_time()));
		viewHolder.listview_time.setText(sb.toString());
		viewHolder.listview_length.setText(df.format(run.getLength()));
		viewHolder.listview_duration.setText(DateHelper.getInstance().chagetime(run.getDuration()));
		viewHolder.listview_consume.setText(df.format(run.getConsume())+"千卡");
		if(run.isUpdate()){
			viewHolder.listview_image_update_status.setImageResource(R.mipmap.update_ok);
		}
		return view;
	}

	private class ViewHolder{
		private TextView listview_time,listview_length,listview_duration,listview_consume;
		private ImageView listview_image_update_status;
	}

}
