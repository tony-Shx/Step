package com.example.henu.step;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.Bean.RunningRecord;
import com.example.henu.step.DataBase.DatebaseAdapter;
import com.example.henu.step.Util.DataHelper;
import com.example.henu.step.Util.listAdapter;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MylistActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

	private ListView listView;
	private ArrayList<Run> list;
	private final int UPDATESUCCESS = 0X100;
	private final int UPDATEFAILED = 0X101;
	private final int DELETEFAILED = 0X200;
	private final int DELETESUCCESS = 0X201;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mylist);
		//第一：默认初始化
		Bmob.initialize(this, "bd7c2a4e820ce954f26ac4b4b2aaa85d");
		listView = (ListView) this.findViewById(R.id.listview_mylist);
		DatebaseAdapter db = new DatebaseAdapter(this);
		list = db.findAll();
		listView.setAdapter(new listAdapter(this, list));
		listView.setOnItemClickListener(this);
		listView.setOnCreateContextMenuListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, RecordShowActivity.class);
		intent.putExtra("position", position);
		startActivity(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		int position = info.position;
		menu.add(0, 0, position, "删除");
		Run run = list.get(position);
		if (!run.isUpdate()) {
			menu.add(0, 1, position, "上传");
		}
		Log.i("onCreateContextMenu: ", position + " " + String.valueOf(v.getId()));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				int position = item.getOrder();
				deleteRunRecord(position);
				break;
			case 1:
				updateRunRecord(item.getOrder());
				Toast.makeText(this, "正在上传中...", Toast.LENGTH_SHORT).show();
				break;
		}
		return true;
	}

	private void deleteRunRecord(final int position) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Run run = list.get(position);
				RunningRecord runningRecord = new RunningRecord();
				runningRecord.setObjectId(run.getId());
				runningRecord.delete(new UpdateListener() {
					@Override
					public void done(BmobException e) {
						if (e == null) {
							Message msg = new Message();
							msg.arg1 = DELETESUCCESS;
							msg.obj = run;
							handler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.arg1 = DELETEFAILED;
							handler.sendMessage(msg);
						}
					}
				});
			}
		}).start();

	}

	private void freshListViewDate() {
		DatebaseAdapter db = new DatebaseAdapter(this);
		list = db.findAll();
		listView.setAdapter(new listAdapter(this, list));
	}

	private void updateRunRecord(final int position) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Run run = list.get(position);
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
						if (e == null) {
							Message msg = new Message();
							msg.arg1 = UPDATESUCCESS;
							msg.arg2 = position;
							msg.obj = s;
							handler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.arg1 = UPDATEFAILED;
							handler.sendMessage(msg);
							Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
						}
					}
				});
			}
		}).start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DatebaseAdapter db = new DatebaseAdapter(getApplicationContext());
			switch (msg.arg1) {
				case UPDATESUCCESS:
					Run run = list.get(msg.arg2);
					String oldID = run.getId();
					run.setId((String) msg.obj);
					run.setUpdate(1);
					db.update(run, oldID);
					freshListViewDate();
					Toast.makeText(getApplicationContext(), "上传成功！", Toast.LENGTH_LONG).show();
					break;
				case UPDATEFAILED:
					Toast.makeText(getApplicationContext(), "数据同步出错，请检查网络连接", Toast.LENGTH_LONG).show();
					break;
				case DELETESUCCESS:
					Run run1 = (Run) msg.obj;
					db.delete(run1.getId());
					freshListViewDate();
					Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
					break;
				case DELETEFAILED:
					Toast.makeText(getApplicationContext(), "删除出错，请检查网络连接", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		}
	};
}
