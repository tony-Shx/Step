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
import android.widget.TextView;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.DataBase.DatebaseAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/12 0012.
 */
public class FirstFragment extends Fragment {

	private TextView txt_run_corder;
	private TextView txt_run_time;
	private TextView t1, textView_mylist;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.first_fragment, container, false);
		txt_run_corder = (TextView) view.findViewById(R.id.run_corder);
		txt_run_time = (TextView) view.findViewById(R.id.run_time);
		t1 = (TextView) view.findViewById(R.id.t1);
		textView_mylist = (TextView) view.findViewById(R.id.textView_mylist);
		textView_mylist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), MylistActivity.class));
			}
		});
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
}

