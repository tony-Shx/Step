package com.example.henu.step;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class RecordShowActivity extends AppCompatActivity {
	private MapView mMapView;
	private TextView textView_record_length, textView_record_time, textView_record_consume;
	private BaiduMap mBaiduMap;
	private ImageView imageView_record;
	// 构造折线点坐标
	List<LatLng> points = new ArrayList<LatLng>();
	private Tencent mTencent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		mTencent = Tencent.createInstance("101400364", this.getApplicationContext());
		//注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_record_show);
		textView_record_time = (TextView) findViewById(R.id.textView_record_time);
		textView_record_consume = (TextView) findViewById(R.id.textView_record_consume);
		textView_record_length = (TextView) findViewById(R.id.textView_record_length);
		imageView_record = (ImageView) findViewById(R.id.imageView_record);
		Run run = (Run) getIntent().getSerializableExtra("position");
		textView_record_length.setText(String.format("%.2f", run.getLength()));
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
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(points.get(points.size() - 1), 19));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final File newFile = new File(getExternalCacheDir(), "share.jpg");
		imageView_record.setVisibility(View.VISIBLE);
		mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
			@Override
			public void onSnapshotReady(Bitmap bitmap) {
				imageView_record.setImageBitmap(bitmap);
				mMapView.setVisibility(View.GONE);
				getScreenHot(getWindow().getDecorView().findViewById(android.R.id.content), newFile.getAbsolutePath());
				startShare(newFile);
				mMapView.setVisibility(View.VISIBLE);
				imageView_record.setVisibility(View.GONE);
			}
		});

		return true;
	}

	private void startShare(File file) {
		Bundle params = new Bundle();
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, file.getAbsolutePath());
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "Step");
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
		mTencent.shareToQQ(this, params, new BaseUiListener());
	}

	private class BaseUiListener implements IUiListener {
		protected void doComplete(JSONObject values) {
			Log.i("doComplete: ", values.toString());
		}

		@Override
		public void onError(UiError e) {
			Log.e("onError:", "code:" + e.errorCode + ", msg:"
					+ e.errorMessage + ", detail:" + e.errorDetail);
		}

		@Override
		public void onComplete(Object o) {

		}

		@Override
		public void onCancel() {

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mTencent.onActivityResult(requestCode, resultCode, data);
	}

	private void getScreenHot(View v, String filePath) {
		try {
			v.setDrawingCacheEnabled(true);
			v.buildDrawingCache();
			Bitmap bitmap = v.getDrawingCache();
			try {
				FileOutputStream fos = new FileOutputStream(filePath);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			} catch (FileNotFoundException e) {
				throw new InvalidParameterException();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void analyzePoints(String str_point) {
		System.out.println(str_point);
		String[] str = str_point.split("\n");
		int length = str.length;
		for (int i = 0; i < length; i++) {
			String[] point_s = str[i].split(" ");
			for (int j = 0; j < point_s.length; j++) {
				String[] latitude = point_s[j].split(",");
				LatLng point = new LatLng(Double.parseDouble(latitude[0]), Double.parseDouble(latitude[1]));
				points.add(point);
			}
		}
	}
}
