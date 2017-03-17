package com.example.henu.step.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;

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
		View view=inflater.inflate(R.layout.listview_item,null);
		Run run=list.get(position);
		DecimalFormat df = new DecimalFormat("0.00");
		//在view视图中查找id为image_photo的控件
		TextView listview_time= (TextView) view.findViewById(R.id.listview_time);
		TextView listview_length= (TextView) view.findViewById(R.id.listview_length);
		TextView listview_duration= (TextView) view.findViewById(R.id.listview_duration);
		TextView listview_consume= (TextView) view.findViewById(R.id.listview_consume);
		StringBuffer sb = new StringBuffer();
		DataHelper dataHelper = new DataHelper();
		sb.append(dataHelper.changedata(run.getStart_time()));
		sb.append(" - ");
		sb.append(dataHelper.changedata(run.getEnd_time()));
		listview_time.setText(sb.toString());
		listview_length.setText(df.format(run.getLength()));
		listview_duration.setText(dataHelper.chagetime(run.getDuration()));
		listview_consume.setText(df.format(run.getConsume())+"千卡");
		return view;

	}
}
