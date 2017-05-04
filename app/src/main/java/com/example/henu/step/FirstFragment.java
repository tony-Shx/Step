package com.example.henu.step;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.Bean.User;
import com.example.henu.step.Bean.UserInfo;
import com.example.henu.step.DataBase.DatebaseAdapter;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/3/12 0012.
 */
public class FirstFragment extends Fragment implements View.OnClickListener {

	private TextView txt_run_corder,txt_run_time,t1, textView_mylist;
	private Button setArea,button_w;
	private ImageView imageView_touXiang;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.first_fragment, container, false);
		txt_run_corder = (TextView) view.findViewById(R.id.run_corder);
		txt_run_time = (TextView) view.findViewById(R.id.run_time);
		t1 = (TextView) view.findViewById(R.id.t1);
		textView_mylist = (TextView) view.findViewById(R.id.textView_mylist);
		imageView_touXiang = (ImageView) view.findViewById(R.id.touxiang);
		textView_mylist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), MylistActivity.class));
			}
		});

		button_w = (Button) view.findViewById(R.id.button_w);
		button_w.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), WeatherActivity.class));
			}
		});
		imageView_touXiang.setOnClickListener(this);
		dateInit();
		return view;
	}

	private void dateInit() {
		DatebaseAdapter db = new DatebaseAdapter(getActivity());
		ArrayList<Run> list = db.findAll();
		float total_length = 0;
		int total_time = 0;
		for (Run run : list) {
			total_length += run.getLength();
			total_time += run.getDuration();
		}
		DecimalFormat df = new DecimalFormat("0.00");
		System.out.println(df.format(total_length).toString());
		txt_run_corder.setText(df.format(total_length).toString());
		txt_run_time.setText(df.format(total_time / 60.00));
		SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
		t1.setText(sp.getString("telephone", "null"));

	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences preferences = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
		String txurl = preferences.getString("txurl","null");
		String telephone = preferences.getString("telephone",null);
		if(txurl.equals("null")){
			BmobQuery<UserInfo> query = new BmobQuery<>();
			query.addWhereEqualTo("telephone",telephone);
			query.findObjects(new FindListener<UserInfo>() {
				@Override
				public void done(List<UserInfo> list, BmobException e) {
					if(e==null){
						if(list.isEmpty()){
							Toast.makeText(getActivity(),"查询头像出错",Toast.LENGTH_LONG).show();
						}else{
							UserInfo userInfo = list.get(0);
							Picasso.with(getActivity()).load(userInfo.getTouXiang()).into(imageView_touXiang);
						}
					}else{
						Toast.makeText(getActivity(),"头像加载失败.....",Toast.LENGTH_LONG).show();
					}

				}
			});
		}else{
			Picasso.with(getActivity()).load(txurl).into(imageView_touXiang);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.touxiang:
				startActivity(new Intent(getActivity(),ShowDetailActivity.class));
				break;
			default:
				break;
		}
	}
}

