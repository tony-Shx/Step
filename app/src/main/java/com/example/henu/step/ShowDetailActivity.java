package com.example.henu.step;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.henu.step.Bean.UserInfo;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.http.SyncHttpHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ShowDetailActivity extends AppCompatActivity {
	private ImageView imageView_touXiang_show;
	private TextView textView_username,textView_telephone,textView_sex,textView_hight,textView_weight;
	private UserInfo userInfo;
	private Dialog dialog;
	private static final int PHOTO_REQUEST_GALLERY = 1;
	private static final int PHOTO_REQUEST_CUT = 2;
	private static final String TAG = ShowDetailActivity.class.getSimpleName();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_detail);
		findView();
		initData();
		imageView_touXiang_show.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
				getAlbum.setType("image/*");
				startActivityForResult(getAlbum, PHOTO_REQUEST_GALLERY);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
			case PHOTO_REQUEST_GALLERY:
				if(data!=null){
					startPhotoZoom(data.getData());
				}else{
					Log.i("onActivityResult: "," ");
				}
				break;
			case PHOTO_REQUEST_CUT:
				if(data!=null){
					if(data.getParcelableExtra("data") instanceof Bitmap){
						Bitmap bitmap = data.getParcelableExtra("data");
						File file = new File(getExternalCacheDir(),"temp.jpg");
						try {
							FileOutputStream fos = new FileOutputStream(file);
							bitmap.compress(Bitmap.CompressFormat.JPEG,80,fos);
							fos.flush();
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						final BmobFile bmobFile = new BmobFile(file);
						bmobFile.upload(new UploadFileListener() {
							@Override
							public void done(BmobException e) {
								if(e==null){
									Log.i(TAG, "bmobFile.upload: success"+bmobFile.getFileUrl());
									SharedPreferences preference = getSharedPreferences("login",Context.MODE_PRIVATE);
									SharedPreferences.Editor editor = preference.edit();
									editor.putString("txurl",bmobFile.getFileUrl());
									editor.commit();
									Picasso.with(ShowDetailActivity.this).load(bmobFile.getFileUrl()).into(imageView_touXiang_show);
									UserInfo userInfo1 = new UserInfo();
									userInfo1.setTouXiang(bmobFile.getFileUrl());
									userInfo1.setWeight(userInfo.getWeight());
									userInfo1.setStature(userInfo.getStature());
									userInfo1.update(userInfo.getObjectId(), new UpdateListener() {
										@Override
										public void done(BmobException e) {
										if(e==null){
											dialog.dismiss();
											Toast.makeText(ShowDetailActivity.this,"上传成功",Toast.LENGTH_LONG).show();
										}else{
											Log.e(TAG, "bmobFile.upload: ", e);
											dialog.dismiss();
											Toast.makeText(ShowDetailActivity.this,"上传失败"+e.getMessage(),Toast.LENGTH_LONG).show();
										}
										}
									});

								}else{
									Log.e(TAG, "bmobFile.upload: ", e);
									dialog.dismiss();
									Toast.makeText(ShowDetailActivity.this,"上传失败"+e.getMessage(),Toast.LENGTH_LONG).show();
								}
							}
						});
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setTitle("温馨提示");
						builder.setMessage("正在上传头像......");
						builder.setView(new ProgressBar(this));
						builder.setCancelable(false);
						dialog = builder.show();
					}else{
						Log.i("onActivityResult: ","false");
					}
				}
				break;
			default:
				break;
		}
	}

	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private void initData() {
		SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		String telephone = preferences.getString("telephone","未知");
		textView_telephone.setText(telephone);
		new InitDataFormBmob().execute(telephone);
	}




	private void findView() {
		imageView_touXiang_show = (ImageView) findViewById(R.id.imageView_touXiang_show);
		textView_username = (TextView) findViewById(R.id.textView_username);
		textView_telephone = (TextView) findViewById(R.id.textView_telephone);
		textView_sex = (TextView) findViewById(R.id.textView_sex);
		textView_hight = (TextView) findViewById(R.id.textView_hight);
		textView_weight = (TextView) findViewById(R.id.textView_weight);
	}

	FindListener<UserInfo> findListener = new FindListener<UserInfo>() {
		@Override
		public void done(List<UserInfo> list, BmobException e) {
			if (e==null){
				if(list.isEmpty()){
					Toast.makeText(ShowDetailActivity.this,"未查询到用户信息", Toast.LENGTH_LONG).show();
					textView_username.setText("未设置用户名");
					textView_sex.setText("未设置性别");
					textView_hight.setText("未设置");
					textView_weight.setText("未设置");
				}else{
					userInfo = list.get(0);
					if(userInfo.getTouXiang()!=null){
						Picasso.with(ShowDetailActivity.this).load(userInfo.getTouXiang()).into(imageView_touXiang_show);
					}
					if(userInfo.getUsername()!=null){
						textView_username.setText(userInfo.getUsername());
					}else{
						textView_username.setText("未设置用户名");
					}
					if(userInfo.getSex()!=null){
						textView_sex.setText(userInfo.getSex());
					}else{
						textView_sex.setText("未设置性别");
					}
					if(userInfo.getTelephone()!=null){
						textView_telephone.setText(userInfo.getTelephone());
					}else{
						textView_telephone.setText("未知");
					}
					if(userInfo.getStature()>0){
						textView_hight.setText(userInfo.getStature()+"cm");
					}else{
						textView_hight.setText("未设置");
					}
					if(userInfo.getWeight()>0){
						textView_weight.setText(userInfo.getWeight()+"kg");
					}else{
						textView_weight.setText("未设置");
					}
				}
			}else{
				Toast.makeText(ShowDetailActivity.this,"查询出错", Toast.LENGTH_LONG).show();
			}
		}
	};

	private class InitDataFormBmob extends AsyncTask<String,Void,Void>{

		@Override
		protected Void doInBackground(String... params) {
			UserInfo userInfo = new UserInfo();
			BmobQuery<UserInfo> query = new BmobQuery<>();
			query.addWhereEqualTo("telephone",params[0]);
			query.findObjects(findListener);
			return null;
		}
	}
}
