package com.example.henu.step;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.henu.step.Bean.Setting;
import com.example.henu.step.Util.settingListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/12 0012.
 */
public class SecondFragment extends Fragment {

    private List<Setting> settingList =new ArrayList<>() ;
    private ListView listView;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initSettings();
        if(view == null){
            view = inflater.inflate(R.layout.fragment_second,container,false);
        }else{
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        settingListAdapter adapter =  new settingListAdapter(getActivity(),R.layout.setting_listview_item,settingList);
        listView = (ListView)view.findViewById(R.id.setting_listView);
        listView.setAdapter(adapter);
        listView.setDivider(new ColorDrawable(Color.WHITE));
        listView.setDividerHeight(1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String TAG = settingList.get(i).getName();
                if(TAG=="个人信息修改"){
                    Intent intent = new Intent(getActivity(),InformationActivity.class);
                    startActivity(intent);
                }
                if(TAG=="密码修改"){
                    Intent intent = new Intent(getActivity(),ChangePasswordActivity.class);
                    startActivity(intent);
                }
                if(TAG=="注销登录"){
                    SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isLogin",false);
                    editor.commit();
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                    getActivity().finish();
                }
                if(TAG=="关于我们"){
//                    Intent intent = new Intent(getActivity(),TeamActivity.class);
//                    startActivity(intent);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("关于我们");
                    builder.setView(R.layout.dialog_about_us);
                    builder.setCancelable(true);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
        return view;
    }
    public void initSettings(){
        Setting personalMessage = new Setting("个人信息修改");
        settingList.add(personalMessage);
        Setting passwordMessage = new Setting("密码修改");
        settingList.add(passwordMessage);
        Setting logout = new Setting("注销登录");
        settingList.add(logout);
        Setting aboutUs = new Setting("关于我们");
        settingList.add(aboutUs);
    }
}
