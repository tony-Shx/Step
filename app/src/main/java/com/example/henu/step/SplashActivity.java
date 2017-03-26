package com.example.henu.step;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.Bean.RunningRecord;
import com.example.henu.step.DataBase.DatebaseAdapter;
import com.example.henu.step.Util.DateHelper;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SplashActivity extends Activity {

	private boolean isLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//第一：默认初始化
		Bmob.initialize(this, "bd7c2a4e820ce954f26ac4b4b2aaa85d");
		SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
		isLogin = sp.getBoolean("isLogin", false);
		downloadRunningDate(sp.getString("telephone", "11111111"));
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			//延迟3秒加载登录页面
			public void run() {
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				if (isLogin) {
					intent = new Intent(SplashActivity.this, MainActivity.class);
				}
				startActivity(intent);
				finish();
			}
		}, 1000 * 3);

	}

	private void downloadRunningDate(final String telephone) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				BmobQuery<RunningRecord> query = new BmobQuery<RunningRecord>();
				query.addWhereEqualTo("telephone", telephone);
				query.setLimit(50);
				query.findObjects(new FindListener<RunningRecord>() {
					@Override
					public void done(List<RunningRecord> list, BmobException e) {
						if (e == null) {
							for (RunningRecord runningRecord : list) {
								String id = runningRecord.getObjectId();
								DatebaseAdapter db = new DatebaseAdapter(getApplicationContext());
								Log.i("BMOB_SplashActivity:", "e==null"+id);
								Run run = db.findById(id);
								if (run == null) {
									run = new Run();
									run.setId(id);
									run.setTelephone(runningRecord.getTelephone());
									long startTime = DateHelper.changeStringToDate(runningRecord.getStart_time());
									long endTime = DateHelper.changeStringToDate(runningRecord.getEnd_time());
									if (startTime > 0L && endTime > 0L) {
										run.setStart_time(startTime / 1000 - 1483200000L);
										run.setEnd_time(endTime / 1000 - 1483200000L);
									} else {
										run.setStart_time(0L);
										run.setEnd_time(0L);
									}
									run.setLength(runningRecord.getLength());
									run.setDuration(runningRecord.getDuration());
									run.setConsume(runningRecord.getConsume());
									run.setPoints(runningRecord.getPoints());
									run.setUpdate(1);
									db.add(run);
								} else {
									run.setUpdate(1);
									db.update(run, run.getId());
								}
							}
						} else {
							Log.e("BMOB_SplashActivity:", "初始化数据同步出错（网络原因）" + e.getMessage());
						}
					}
				});


			}
		}).start();
	}

	@Override
	public void finish() {
		super.finish();
	}
}
