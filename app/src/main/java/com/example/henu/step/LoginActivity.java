package com.example.henu.step;

import android.app.Activity;
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
import android.widget.Toast;

import com.example.henu.step.Bean.User;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends Activity implements View.OnClickListener {

	private Button button_register, button_login;
	private EditText editText_telephone_login, editText_password_login;

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
										Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
										SharedPreferences sp = getSharedPreferences("login",Context.MODE_PRIVATE);
										SharedPreferences.Editor editor = sp.edit();
										editor.putBoolean("isLogin",true);
										editor.putString("telephone",telephone);
										editor.putString("password",password);
										editor.commit();
										Intent intent = new Intent(getApplicationContext(), MainActivity.class);
									    startActivity(intent);
									    finish();
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

}