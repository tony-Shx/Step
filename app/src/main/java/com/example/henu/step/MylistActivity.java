package com.example.henu.step;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.henu.step.Bean.Run;
import com.example.henu.step.DataBase.DatebaseAdapter;
import com.example.henu.step.Util.listAdapter;

import java.util.ArrayList;

public class MylistActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mylist);
		listView = (ListView) this.findViewById(R.id.listview_mylist);
		DatebaseAdapter db = new DatebaseAdapter(this);
		ArrayList<Run> list = db.findAll();
		listView.setAdapter(new listAdapter(this,list));
		listView.setOnItemClickListener(this);

	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this,RecordShowActivity.class);
		intent.putExtra("position",position);
		startActivity(intent);
	}
}
