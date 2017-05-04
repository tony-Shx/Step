package com.example.henu.step;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.henu.step.Bean.Run;
import com.example.henu.step.DataBase.DatebaseAdapter;
import com.example.henu.step.Util.DateHelper;

import java.util.ArrayList;
import java.util.List;

public class RecordShowActivity extends AppCompatActivity {
	private MapView mMapView;
	private TextView textView_record_length,textView_record_time,textView_record_consume;
	private BaiduMap mBaiduMap;
	// 构造折线点坐标
	List<LatLng> points = new ArrayList<LatLng>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		//注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_record_show);
		textView_record_time = (TextView) findViewById(R.id.textView_record_time);
		textView_record_consume = (TextView) findViewById(R.id.textView_record_consume);
		textView_record_length = (TextView) findViewById(R.id.textView_record_length);
		Run run= (Run) getIntent().getSerializableExtra("position");
		textView_record_length.setText(String.format("%.2f",run.getLength()));
		textView_record_time.setText(DateHelper.getInstance().chagetime(run.getDuration()));
		mMapView = (MapView) findViewById(R.id.mapView_show_record);
		mBaiduMap = mMapView.getMap();
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("记录页面");
		String str_point = run.getPoints();
		analyzePoints(str_point);
		OverlayOptions ooPolyline = new PolylineOptions().width(10).color(Integer.valueOf(Color.GREEN))
				.points(points);
		//添加在地图中
		Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(points.get(points.size()-1),19));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options,menu);
		return true;
	}

	private void analyzePoints(String str_point) {
		System.out.println(str_point);
		String[] str = str_point.split("\n");
		int length = str.length;
		for(int i = 0;i<length;i++){
			String[] point_s = str[i].split(" ");
			for(int j = 0;j<point_s.length;j++){
				String[] latitude = point_s[j].split(",");
				LatLng point = new LatLng(Double.parseDouble(latitude[0]),Double.parseDouble(latitude[1]));
				points.add(point);
			}
		}
	}
}
