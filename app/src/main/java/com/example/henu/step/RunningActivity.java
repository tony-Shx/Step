package com.example.henu.step;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.henu.step.Bean.Run;
import com.example.henu.step.Bean.RunningRecord;
import com.example.henu.step.DataBase.DatebaseAdapter;
import com.example.henu.step.Util.DataHelper;
import com.example.henu.step.Util.MathHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RunningActivity extends AppCompatActivity implements View.OnClickListener {

	private MapView mMapView;
	private DatebaseAdapter db;
	private Run run;
	private BaiduMap mBaiduMap;
	private TextView editText_length, editText_time, editText_speed;
	private BitmapDescriptor mCurrentMarker;
	private MyHandler handler = new MyHandler();
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener(handler);
	// 构造折线点坐标
	List<LatLng> points = new ArrayList<LatLng>();
	private boolean isStop = true;
	private long start_time;
	private final int UPDATESUCCESS = 0X100;
	private final int UPDATEFAILED = 0X101;
	private final int RECEIVELOCATION = 0X102;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		//注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_running);
		//第一：默认初始化
		Bmob.initialize(this, "bd7c2a4e820ce954f26ac4b4b2aaa85d");
		//获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		mLocationClient.registerLocationListener(myListener);    //注册监听函数
		Button button_startRunning = (Button) findViewById(R.id.button_startRunning);
		Button button_pauseRunning = (Button) findViewById(R.id.button_pauseRunning);
		Button button_stopRunning = (Button) findViewById(R.id.button_stopRunning);
		editText_length = (TextView) findViewById(R.id.editText_length);
		editText_speed = (TextView) findViewById(R.id.editText_speed);
		editText_time = (TextView) findViewById(R.id.editText_time);
		button_pauseRunning.setOnClickListener(this);
		button_startRunning.setOnClickListener(this);
		button_stopRunning.setOnClickListener(this);
		initLocation();
	}

	private class MyHandler extends Handler {
		MyHandler() {

		}

		DecimalFormat df = new DecimalFormat("0.00");
		OverlayOptions option;
		int temp = 0;
		BitmapDescriptor bitmap = null;
		MathHelper mathHelper = new MathHelper();
		double length;
		double speed;

		public void handleMessage(Message msg) {
			if(msg.arg1==RECEIVELOCATION){
			BDLocation location = (BDLocation) msg.obj;
			// 开启定位图层
			mBaiduMap.setMyLocationEnabled(true);
			if (location != null) {
				LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
				//log输出经纬度，便于调试观察
				Log.v("points", "temp=" + temp + point.latitudeE6 + "@@" + point.longitudeE6);
				// 构建Marker图标即定位图标
				bitmap = BitmapDescriptorFactory.fromResource(R.drawable.huaji); // 非推算结果
				OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
//				//获取卫星数量，数量<4,定位不成功，不执行任何操作，直接return
//				MyLocationData myLocationData = new MyLocationData.Builder().build();
//				temp++;
//				if (myLocationData.satellitesNum < 4) {
//					if (temp==1||temp % 20 == 0) {
//						Toast.makeText(getApplicationContext(), "正在搜索GPS......"+myLocationData.satellitesNum, Toast.LENGTH_SHORT).show();
//					}
//					return;
//				}
				//前5秒一般定位不准（GPS定位需要一点时间），所以前5秒定位数据舍弃不用，直接return
				if (temp++ < 5) {
					mBaiduMap.clear();
					mBaiduMap.addOverlay(option);
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, 20));
					editText_time.setText("累计用时： 0分 " + temp + "秒");
					return;
				}
				//Toast.makeText(getApplicationContext(), "定位成功后开始收集定位数据"+myLocationData.satellitesNum, Toast.LENGTH_SHORT).show();
				System.out.println("定位成功后开始收集定位数据");
				//定位成功后开始收集定位数据
				points.add(point);
				// 在地图上添加Marker，并显示
				mBaiduMap.clear();
				mBaiduMap.addOverlay(option);
				//计算运动的距离并且显示
				length = mathHelper.getLength(points) / 1000;
				editText_length.setText("累计距离：" + df.format(length) + " 千米");
				//处理运动时间
				if (temp > 59) {
					int fen = temp / 60;
					int miao = temp % 60;
					editText_time.setText("累计用时： " + fen + "分 " + miao + "秒");
				} else {
					editText_time.setText("累计用时： 0分 " + temp + "秒");
				}
				speed = length * 1000 / temp;
				editText_speed.setText("平均速度： " + df.format(speed) + " 米/秒");
				//当points中有两个以上坐标点时开始画轨迹（轨迹最少是2个点）
				if (points.size() > 1) {
					OverlayOptions ooPolyline = new PolylineOptions().width(10).color(Integer.valueOf(Color.GREEN))
							.points(points);
					//添加在地图中
					Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
					if (points.size() % 5 == 0) {
						mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
					}
				} else {
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, 20));
				}
			}
			}else if(msg.arg1==UPDATESUCCESS){
				String objectId  = (String) msg.obj;
				run.setId(objectId);
				run.setUpdate(1);
				db.add(run);
				isStop = true;
				startActivity(new Intent(getApplicationContext(),MainActivity.class));
				finish();
			}else if(msg.arg1==UPDATEFAILED){
				//更新失败时用系统当前时间作为ID主键
				run.setId(System.currentTimeMillis()+"");
				run.setUpdate(0);
				db.add(run);
				isStop = true;
				Toast.makeText(getApplicationContext(),"数据同步出错，请检查网络连接",Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_startRunning:
				isStop = false;
				getStart_time();
				mLocationClient.start();
				break;
			case R.id.button_pauseRunning:
				mLocationClient.stop();
				break;
			case R.id.button_stopRunning:
				mLocationClient.stop();
				mLocationClient.unRegisterLocationListener(myListener);
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage("跑步结束，是否保存此次记录？");
				dialog.setPositiveButton("保存",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								saveRun();
							}
						});
				dialog.setNeutralButton("不保存", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						startActivity(new Intent(getApplicationContext(),MainActivity.class));
						finish();
					}
				} );
				dialog.show();
				break;
			default:
				break;
		}
	}

	private void getStart_time() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL("http://www.baidu.com");
					URLConnection uc = url.openConnection();// 生成连接对象
					uc.connect();// 发出连接
					long ld = uc.getDate();// 读取网站日期时间
					start_time = ld/1000-1483200000L;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void saveRun() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				db = new DatebaseAdapter(getApplicationContext());
				run = new Run();
				SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
				run.setTelephone(sp.getString("telephone", null));
				run.setStart_time(start_time);
				try {
					URL url = new URL("http://www.baidu.com");
					URLConnection uc = url.openConnection();// 生成连接对象
					uc.connect();// 发出连接
					long ld = uc.getDate();// 读取网站日期时间
					System.out.println("获取到的网络时间："+ld);
					long endtime = ld/1000-1483200000L;
					run.setEnd_time(endtime);
				} catch (IOException e) {
					e.printStackTrace();
				}
				run.setDuration(handler.temp);
				run.setLength(handler.length);
				run.setConsume(60*handler.length*1.036);
				StringBuffer sb = new StringBuffer();
				int i = 0;
				for (LatLng point : points) {
					sb.append(point.latitude).append(",").append(point.longitude);
					//每10个坐标敲一个回车
					if (++i == 10) {
						sb.append("\n");
						i = 0;
					} else {
						sb.append(" ");
					}
				}
				run.setPoints(sb.toString());
				//创建服务器端存储对象runningRecord（一个对象对应服务器一条记录）
				RunningRecord runningRecord = new RunningRecord();
				runningRecord.setPoints(run.getPoints());
				runningRecord.setTelephone(run.getTelephone());
				runningRecord.setStart_time(DataHelper.changedata(run.getStart_time()));
				runningRecord.setEnd_time(DataHelper.changedata(run.getEnd_time()));
				runningRecord.setLength(run.getLength());
				runningRecord.setDuration(run.getDuration());
				runningRecord.setConsume(run.getConsume());
				runningRecord.save(new SaveListener<String>() {
					@Override
					public void done(String s, BmobException e) {
						if(e==null){
							Message msg = new Message();
							msg.arg1 = UPDATESUCCESS;
							msg.obj = s;
							handler.sendMessage(msg);
						}else{
							Message msg = new Message();
							msg.arg1 = UPDATEFAILED;
							handler.sendMessage(msg);
							Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
						}
					}
				});


//				new SaveListener<String>() {
//					@Override
//					public void done(String s, BmobException e) {
//						if(e==null){
//							Message msg = new Message();
//							msg.arg1 = UPDATESUCCESS;
//							handler.sendMessage(msg);
//						}else{
//							Message msg = new Message();
//							msg.arg1 = UPDATEFAILED;
//							handler.sendMessage(msg);
//							Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//
//						}
//					}
//				}



				//Toast.makeText(getApplicationContext(), "数据保存成功！", Toast.LENGTH_SHORT).show();
			}
		}).start();

	}

	private void initGPS() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS模块是否开启，如果没有则开启
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("Step跑步轨迹记录需要GPS定位，请打开GPS");
			dialog.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
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
			});
			dialog.show();
		}
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
		);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	@Override
	public void onBackPressed() {
		if (isStop) {
			startActivity(new Intent(this,MainActivity.class));
			finish();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("Step正在运行，请先结束本次运动再退出");
			dialog.setPositiveButton("我知道了", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			});
			dialog.show();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		//取消注册事件，以防出现空指针闪退
		mLocationClient.unRegisterLocationListener(myListener);

	}

	@Override
	protected void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}
