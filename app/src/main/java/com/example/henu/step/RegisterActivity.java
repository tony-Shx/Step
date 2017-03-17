package com.example.henu.step;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.henu.step.Bean.User;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity implements View.OnClickListener {

	private EditText editText_telephone, editText_password, editText_code;
	private Button button_getcode, button_submit_login;
	private Thread thread_sec;
	private Dialog dialog_loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		findView();
		//第一：默认初始化
		Bmob.initialize(this, "bd7c2a4e820ce954f26ac4b4b2aaa85d");
		SMSStart();
	}

	private void findView() {
		editText_telephone = (EditText) findViewById(R.id.editText_telephone);
		editText_code = (EditText) findViewById(R.id.editText_code);
		editText_password = (EditText) findViewById(R.id.editText_password);
		button_getcode = (Button) findViewById(R.id.button_getCode);
		button_submit_login = (Button) findViewById(R.id.button_login_submit);
		button_submit_login.setOnClickListener(this);
		button_getcode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String telephone = editText_telephone.getText().toString();
		String password = editText_password.getText().toString();
		String code = editText_code.getText().toString();
		switch (v.getId()) {
			case R.id.button_getCode:
				if (telephone.length() == 11) {
					SMSSDK.getVerificationCode("86", telephone);
					button_getcode.setClickable(false);
					button_getcode.setTextColor(Color.BLACK);
					updateUI();
				} else {
					Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
					// 获取编辑框焦点
					editText_telephone.setFocusable(true);
					editText_telephone.requestFocus();
					//打开软键盘
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
					editText_telephone.selectAll();
				}
				break;
			case R.id.button_login_submit:
				//关闭软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
				}
				if (password.length() > 20 || password.length() < 6) {
					Toast.makeText(this, "密码格式不正确！", Toast.LENGTH_LONG).show();
					// 获取编辑框焦点
					editText_password.setFocusable(true);
					editText_password.requestFocus();
					//打开软键盘
					if (imm != null) {
						imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
					}
					editText_password.selectAll();
				} else if (code.length() < 4) {
					Toast.makeText(this, "验证码格式不正确！", Toast.LENGTH_LONG).show();
					// 获取编辑框焦点
					editText_code.setFocusable(true);
					editText_code.requestFocus();
					//打开软键盘
					if (imm != null) {
						imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
					}
					editText_code.selectAll();
				} else {
					//显示对话框！
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.dialog_loading,
							(ViewGroup) findViewById(R.id.dialog));
					dialog_loading = new AlertDialog.Builder(this).setTitle("提交数据").setView(layout).setCancelable(false)
							.show();
					dialog_loading.setCancelable(true);
					checkUser();
					//SMSSDK.submitVerificationCode("86", telephone, code);
				}
				break;
			default:
				break;
		}
	}


	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			StringBuffer sb = new StringBuffer();
			sb.append(msg.arg1).append("秒");
			button_getcode.setTextColor(Color.GRAY);
			button_getcode.setText(sb);
			if (msg.arg1 <= 0) {
				button_getcode.setText("点击获取");
				button_getcode.setClickable(true);
				button_getcode.setTextColor(Color.BLACK);
			}
		}
	};

	private void updateUI() {
		thread_sec = new Thread(new Runnable() {
			int sec = 70;

			public void run() {
				while (sec >= 1) {
					Message msg = new Message();
					sec--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					msg.arg1 = sec;
					handler.sendMessage(msg);
				}
			}
		});
		thread_sec.start();
	}

	private void SMSStart() {
		SMSSDK.initSDK(this, "188508ca27fa2", "8b83395eda0dff7871e3af6a329d33c0");
		EventHandler eh = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					//回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						//提交验证码成功
						checkUser();
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						//获取验证码成功

					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						//返回支持发送验证码的国家列表
						System.out.println(data.toString());
					}
				} else {
					((Throwable) data).printStackTrace();
				}
			}
		};
		SMSSDK.registerEventHandler(eh);
	}


	private void checkUser(){
		//检查用户是否存在！！
		BmobQuery<User> query = new BmobQuery<User>();
		//查询表中是否有telephone的数据
		query.addWhereEqualTo("telephone", editText_telephone.getText().toString());
		//返回10条数据，如果不加上这条语句，默认返回10条数据
		query.setLimit(10);
		//执行查询方法
		query.findObjects(new FindListener<User>() {
			@Override
			public void done(List<User> object, BmobException e) {
				if (e == null) {
					System.out.println("查询成功：共" + object.size() + "条数据。");
					if (object.size() == 0) {
						SaveUser();
					} else {
						dialog_loading.cancel();
						//显示提示对话框
						AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
						dialog.setIcon(android.R.drawable.ic_dialog_info)
								.setTitle("温馨提示")
								.setMessage("该手机号已被注册，请直接登录或者找回密码")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								})
								.create();
						dialog.show();
					}
				} else {
					Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
				}
			}
		});
	}

	private void SaveUser() {
		System.out.println("!!执行到保存数据！！");
		User user = new User();
		user.setUsername("");
		user.setTelephone(editText_telephone.getText().toString());
		user.setPassword(editText_password.getText().toString());
		user.save(new SaveListener<String>() {
			@Override
			public void done(String objectId, BmobException e) {
				if (e == null) {
					System.out.println("添加数据成功，返回objectId为：" + objectId);
					dialog_loading.cancel();
					//显示提示对话框
					AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
					dialog.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("温馨提示")
							.setMessage("注册成功，是否直接用此账号登录？")
							.setPositiveButton("是", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(getApplicationContext(), MainActivity.class);
									startActivity(intent);
									finish();
								}
							})
							.setNegativeButton("否", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							})
							.create();
					dialog.show();
					//Toast.makeText(getApplicationContext(), "注册成功，请返回登录", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "注册失败，请检查网络连接", Toast.LENGTH_LONG).show();
					System.out.println("done: 创建数据失败：" + e.getMessage());
				}
			}
		});
	}


}
