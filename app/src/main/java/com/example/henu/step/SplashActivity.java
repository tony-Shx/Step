package com.example.henu.step;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			//延迟3秒加载登录页面
			public void run() {
				Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
				SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
				if(sp.getBoolean("isLogin",false)){
					intent = new Intent(SplashActivity.this,MainActivity.class);
				}
				startActivity(intent);
				finish();
			}
		},1000*3);
	}

	@Override
	public void finish() {
		super.finish();
	}
}
