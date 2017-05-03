package com.example.henu.step;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.henu.step.Bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/4/15.
 */

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editText_old,editText_new,editText_newt;
    private Button button_change;
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        editText_old = (EditText) findViewById(R.id.editText_old);
        editText_new  = (EditText) findViewById(R.id.editText_change_new);
        editText_newt = (EditText) findViewById(R.id.editText_password_newt);
        button_change = (Button) findViewById(R.id.button_change);
        final SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        final String password = preferences.getString("password",null);
        final String telephone = preferences.getString("telephone",null);
        final User user = new User();
        user.setTelephone(telephone);
        button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.equals(editText_old.getText().toString())){
                    final String password1 = editText_new.getText().toString();
                    String password2 = editText_newt.getText().toString();
                    if(password1.equals(password2)){
                        BmobQuery<User> query = new BmobQuery<User>();
                        query.addWhereEqualTo("telephone",telephone);
                        query.setLimit(1);
                        query.findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> list, BmobException e) {
                                if(e==null){
                                    if(list.isEmpty()){
                                        Toast.makeText(getApplicationContext(),"当前未登录，请检查",Toast.LENGTH_LONG).show();
                                    }else{
                                        User user1 = new User();
                                        user1.setPassword(password1);
                                        user1.update(list.get(0).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString("password",password1);
                                                    editor.commit();
                                                    Toast.makeText(getApplicationContext(),"密码修改成功",Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(getApplicationContext(),"密码修改失败，请检查网络连接",Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(),"网络连接有误，请检查",Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(),"两次密码输入不一致，请检查",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"原密码输入有误，请检查",Toast.LENGTH_LONG).show();
                }
            }
        });

    }




}
