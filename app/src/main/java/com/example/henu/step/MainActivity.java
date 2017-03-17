package com.example.henu.step;

import java.text.DecimalFormat;
import java.util.ArrayList;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.DataBase.DatebaseAdapter;

public class MainActivity extends Activity implements View.OnClickListener {


	private ArrayList<String> list = new ArrayList<String>();
	private Button startRun;
	private boolean runClick = false;
	private TextView txt_deal;
	private TextView txt_user;
	private FrameLayout ly_content;
	private FirstFragment f1;
	private SecondFragment f2;
	private FragmentManager fragmentManager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindView();
		startRun = (Button) findViewById(R.id.button_startRun);
		startRun.setOnClickListener(this);
		onClick(txt_deal);
//		ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>
//				(this,android.R.layout.simple_list_item_1,list);
//		mylistview.setAdapter(myArrayAdapter);
//
//		mylistview.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//									long arg3) {                               //侧边栏 跳转 根据每个项目的ID设置了Intent
//				// TODO Auto-generated method stub
//				if(list.get(arg2).equals("我的记录"))
//				{
//					Intent intent = new Intent("");         //这几个功能页面不知道怎么写
//					startActivity(intent);
//				}
//				if(list.get(arg2).equals("我的资料"))
//				{
//					Intent intent = new Intent("");
//					startActivity(intent);
//				}
//				if(list.get(arg2).equals("退出登陆"))
//				{
//					SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
//					SharedPreferences.Editor editor = sp.edit();
//					editor.putBoolean("isLogin",false);
//					editor.putString("telephone","");
//					editor.putString("password","");
//					editor.commit();
//					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//					startActivity(intent);
//					finish();
//				}
//
//			}
//
//		});
	}

	private void bindView() {
		txt_deal = (TextView)this.findViewById(R.id.txt_deal);
		txt_user = (TextView)this.findViewById(R.id.txt_user);
		ly_content = (FrameLayout) findViewById(R.id.fragment_container);
		txt_deal.setOnClickListener(this);
		txt_user.setOnClickListener(this);
	}

	//重置所有文本的选中状态
	public void selected(){
		txt_deal.setSelected(false);
		txt_user.setSelected(false);
	}
	//隐藏所有Fragment
	public void hideAllFragment(FragmentTransaction transaction){
		if(f1!=null){
			transaction.hide(f1);
		}
		if(f2!=null){
			transaction.hide(f2);
		}

	}


	@Override
	public void onClick(View v) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		hideAllFragment(transaction);
		switch(v.getId()){
			case R.id.txt_deal:
				selected();
				txt_deal.setSelected(true);
				if(f1==null){
					f1 = new FirstFragment();
					transaction.add(R.id.fragment_container,f1);
				}else{
					transaction.show(f1);
				}
				break;
			case R.id.txt_user:
				selected();
				txt_user.setSelected(true);
				if(f2==null){
					f2 = new SecondFragment();
					transaction.add(R.id.fragment_container,f2);
				}else{
					transaction.show(f2);
				}
				break;
			case R.id.button_startRun:
				initGPS();
				break;
			default:
				break;
		}
		transaction.commit();
	}
	private void initGPS() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS模块是否开启，如果没有则开启
		System.out.println("准备检查GPS："+!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER));
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("Step跑步轨迹记录需要GPS定位，请打开GPS");
			dialog.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							runClick = true;
							// 转到手机设置界面，用户设置GPS
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
						}
					});
			dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			} );
			dialog.show();
		}else{
			startActivity(new Intent(this,RunningActivity.class));
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("执行了onResume");
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if(runClick&&locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
			startActivity(new Intent(this,RunningActivity.class));
			finish();
		}
	}
}
