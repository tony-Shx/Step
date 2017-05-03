package com.example.henu.step;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.henu.step.Bean.UserInfo;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class InformationActivity extends AppCompatActivity implements View.OnClickListener {

	private Button button_save,button_cancel;
	private EditText editText_username,editText_stature,editText_weight;
	private RadioButton radioButton_boy,radioButton_girl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//第一：默认初始化
		Bmob.initialize(this, "bd7c2a4e820ce954f26ac4b4b2aaa85d");
		setContentView(R.layout.activity_information);
		findView();
	}



	private void findView() {
		editText_username = (EditText) findViewById(R.id.username_userInfo);
		editText_stature = (EditText) findViewById(R.id.stature_userInfo);
		editText_weight = (EditText) findViewById(R.id.weight_userInfo);
		button_save = (Button) findViewById(R.id.button_save);
		button_cancel = (Button) findViewById(R.id.button_cancel);
		radioButton_boy = (RadioButton) findViewById(R.id.radio_boy);
		radioButton_girl = (RadioButton) findViewById(R.id.radio_girl);
		button_save.setOnClickListener(this);
		button_cancel.setOnClickListener(this);
	}

	private void saveInformation(final String username, final String sex, final int stature, final int weight) {
		final UserInfo userInfo = new UserInfo();
		SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		final String telephone = sharedPreferences.getString("telephone", null);
		BmobQuery<UserInfo> query = new BmobQuery<>();
		query.addWhereEqualTo("telephone", telephone);
		query.setLimit(1);
		query.findObjects(new FindListener<UserInfo>() {
			@Override
			public void done(List<UserInfo> list, BmobException e) {
				if (e == null) {
					userInfo.setTelephone(telephone);
					userInfo.setUsername(username);
					userInfo.setSex(sex);
					userInfo.setStature(stature);
					userInfo.setWeight(weight);
					if (!list.isEmpty()) {
						userInfo.update(list.get(0).getObjectId(), new UpdateListener() {
							@Override
							public void done(BmobException e) {
								if (e == null) {
									Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_LONG).show();
									finish();
								} else {
									Toast.makeText(getApplicationContext(), "保存失败！", Toast.LENGTH_LONG).show();
								}
							}
						});
					}else {
						userInfo.save(new SaveListener<String>() {
							@Override
							public void done(String s, BmobException e) {
								if (e == null) {
									Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_LONG).show();
									finish();
								} else {
									Toast.makeText(getApplicationContext(), "保存失败！", Toast.LENGTH_LONG).show();
								}
							}
						});
					}
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.button_save:
				String username = editText_username.getText().toString();
				int stature = Integer.parseInt(editText_stature.getText().toString());
				int weight = Integer.parseInt(editText_weight.getText().toString());
				if(username!=null&&!username.isEmpty()&&!username.equals("")){
					if(stature<0||stature>300){
						Toast.makeText(getApplicationContext(),"请输入合法的身高！",Toast.LENGTH_LONG).show();
					}else{
						if(weight<0||weight>300){
							Toast.makeText(getApplicationContext(),"请输入合法的体重！",Toast.LENGTH_LONG).show();
						}else{
							String sex = "男";
							if(radioButton_girl.isChecked()){
								sex = "女";
							}
							saveInformation(username,sex,stature,weight);
						}
					}
				}else{
					Toast.makeText(getApplicationContext(),"用户名不能为空！",Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.button_cancel:
				finish();
				break;
			default:
				break;
		}
	}
}
