package com.example.henu.step;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.Bean.RunningRecord;
import com.example.henu.step.Bean.User;
import com.example.henu.step.DataBase.DatebaseAdapter;
import com.example.henu.step.Util.DateHelper;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends Activity implements View.OnClickListener {

	private Button button_register, button_login;
	private EditText editText_telephone_login, editText_password_login;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//第一：默认初始化
		Bmob.initialize(this, "bd7c2a4e820ce954f26ac4b4b2aaa85d");
		findView();
	}

	private void findView() {
		button_login = (Button) findViewById(R.id.button2_login);
		button_register = (Button) findViewById(R.id.button_register);
		editText_password_login = (EditText) findViewById(R.id.editText_password_login);
		editText_telephone_login = (EditText) findViewById(R.id.editText_telephone_login);
		button_login.setOnClickListener(this);
		button_register.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button2_login:
				button_login.setClickable(false);
				button_register.setClickable(false);
				final String telephone = editText_telephone_login.getText().toString();
				final String password = editText_password_login.getText().toString();
				if (telephone.length() != 11) {
					//显示提示对话框
					AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("温馨提示")
							.setMessage("您输入的用户名不存在，请重新输入")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									// 获取编辑框焦点
									editText_telephone_login.setFocusable(true);
									editText_telephone_login.requestFocus();
									//打开软键盘
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									if (imm != null) {
										imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
									}
									editText_telephone_login.selectAll();
								}
							})
							.create();
					dialog.show();
				} else {
					final AlertDialog.Builder normalDia = new AlertDialog.Builder(LoginActivity.this);
					normalDia.setIcon(R.drawable.smssdk_dialog_btn_nor);
					normalDia.setTitle("温馨提示：");
					normalDia.setMessage("正在加载，请稍后");
					normalDia.setView(new ProgressBar(this));
					normalDia.setCancelable(false);
					dialog = normalDia.show();
					BmobQuery<User> query = new BmobQuery<User>();
					//查询username叫“XX”的数据
					query.addWhereEqualTo("telephone", telephone);
					//返回10条数据，如果不加上这条语句，默认返回10条数据
					query.setLimit(10);
					//执行查询方法
					query.findObjects(new FindListener<User>() {
						@Override
						public void done(List<User> object, BmobException e) {
							if (e == null) {
								System.out.println("查询成功：共" + object.size() + "条数据。");
								if (object.size() == 0) {
									//AlertDialog.Builder normalDialog=new AlertDialog.Builder(getApplicationContext());
									final AlertDialog.Builder normalDia = new AlertDialog.Builder(LoginActivity.this);
									normalDia.setIcon(R.drawable.smssdk_dialog_btn_nor);
									normalDia.setTitle("温馨提示：");
									normalDia.setMessage("该用户不存在，请先注册再登录");
									normalDia.setPositiveButton("注册", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
											startActivity(intent);
										}
									})
											.setNegativeButton("取消", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.cancel();
												}
											});
									normalDia.create().show();
								} else {
									if (password.equals(object.get(0).getPassword())){

										SharedPreferences sp = getSharedPreferences("login",Context.MODE_PRIVATE);
										SharedPreferences.Editor editor = sp.edit();
										editor.putBoolean("isLogin",true);
										editor.putString("telephone",telephone);
										editor.putString("password",password);
										editor.commit();
										downloadRunningDate(telephone);

									}else{
										Toast.makeText(getApplicationContext(), "密码错误，请检查您的密码", Toast.LENGTH_LONG).show();
									}
								}
							} else {
								Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
							}
						}
					});
				}
				break;
			case R.id.button_register:
				Intent intent = new Intent(this, RegisterActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}

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
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				dialog.dismiss();
				startActivity(intent);
				finish();
			}
		}).start();
	}

}