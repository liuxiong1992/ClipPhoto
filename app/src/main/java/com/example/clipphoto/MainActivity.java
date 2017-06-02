package com.example.clipphoto;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.example.clipphotolib.util.BitmapUtils;
import com.example.clipphotolib.util.LogUtils;
import com.example.clipphotolib.util.PhotoUtil;

public class MainActivity extends Activity {
	private PhotoUtil photoUtil;
	private ImageView iv_bg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iv_bg = (ImageView) findViewById(R.id.iv_bg);

	}

	public static String getAvartarDiskCacheDir(Context context) {
		return (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) || !Environment
				.isExternalStorageRemovable()) ? Environment
				.getExternalStorageDirectory().getPath()
				+ File.separator
				+ "imagetest/" : context.getCacheDir().getPath()
				+ File.separator + "imagetest/";
	}

	public void show(View view) {
		photoUtil = new PhotoUtil(MainActivity.this);
		photoUtil.showDialog();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		// 相册返回
		if (PhotoUtil.CAMRA_SETRESULT_CODE == requestCode) {
			if (resultCode == RESULT_OK) {
				// 相册选中图片路径
				String cameraPath = photoUtil.getCameraPath(data);
				Bitmap bitmap = photoUtil.readBitmapAutoSize(cameraPath);
				iv_bg.setImageBitmap(bitmap);
				String str = photoUtil.bitmaptoString(bitmap);
				LogUtils.d("相相册选中路径  = " + cameraPath);
				startClipActivity(cameraPath);
			}
		}
		// 相机返回
		else if (com.example.clipphotolib.util.PhotoUtil.PHOTO_SETRESULT_CODE == requestCode) {
			if (resultCode == RESULT_OK) {
				String photoPath = photoUtil.getPhotoPath();
				Bitmap bitmap = photoUtil.readBitmapAutoSize(photoPath);
				String str = photoUtil.bitmaptoString(bitmap);
				iv_bg.setImageBitmap(bitmap);
				LogUtils.d("相机选中路径  = " + photoPath);
				startClipActivity(photoPath);

			}
		}
		// 裁剪返回
		else if (com.example.clipphotolib.util.PhotoUtil.PHOTO_CORPRESULT_CODE == requestCode) {
			if (resultCode == RESULT_OK) {
				LogUtils.d("裁剪返回  = ");
				String path = data.getStringExtra("path");
				BitmapUtils bitmapUtils = new BitmapUtils(getApplicationContext());
		    	Bitmap bitmap = bitmapUtils.decodeFile(path);
		    	iv_bg.setImageBitmap(bitmap);
			}
		}

	}

	public void startClipActivity(String path) {

		Intent intent = new Intent(this, PhotoClipActivity.class);
		intent.putExtra("path", path);
		startActivityForResult(intent, com.example.clipphotolib.util.PhotoUtil.PHOTO_CORPRESULT_CODE);
	}
}
