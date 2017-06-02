package com.example.clipphoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.clipphotolib.classphoto.ClipImageLayout;
import com.example.clipphotolib.util.BitmapUtils;
import com.example.clipphotolib.util.LogUtils;

/**
 * 图片剪切页面
 */
public class PhotoClipActivity extends Activity {
	private ClipImageLayout mClipImageLayout;
	private String noticeImagePath = null;
	private Button bt_cancel, bt_ok;
	private String tempCropFilePath;
	private Context context;
	private String classId;
	private BitmapUtils bitmapUtils;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_clip);
		context = this;
		init();

	}

	private void init() {
		bt_cancel = (Button) findViewById(R.id.bt_photo_cancle);
		bt_ok = (Button) findViewById(R.id.bt_photo_ok);
		Intent intent = getIntent();
		String path = (String) intent.getExtras().get("path");

		bitmapUtils = new BitmapUtils(context);

		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
//		mClipImageLayout.setProportion(6, 10);//直接设置比例
		mClipImageLayout.setImageDrawable(path);

		// 图片选择 需要去裁剪的图片路径
		tempCropFilePath = getFilePath();

		LogUtils.d("获取地址 = " + tempCropFilePath);
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 剪切图片
				bitmap = mClipImageLayout.clip();
				BitmapUtils bitmaputil = new BitmapUtils(
						getApplicationContext());

				if (bitmap != null) {
					// 压缩保存图片
					bitmaputil.saveBitmapInSD(tempCropFilePath, bitmap);
					// sendData();//上传
					Intent intent = new Intent();
					intent.putExtra("path", tempCropFilePath);
					setResult(RESULT_OK, intent);
					finish();
					recycle();
				}

			}
		});
	}

	public String getMydir() {
		return (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) || !Environment
				.isExternalStorageRemovable()) ? Environment
				.getExternalStorageDirectory().getPath() + File.separator + ""
				: context.getCacheDir().getPath() + File.separator + "";
	}

	// 获取图片路径
	public String getFilePath() {
		LogUtils.d("-----根目录路径  = " + getMydir());
		String path = getMydir();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		path = path + timeStamp + ".jpg";
		return path;
	}

	@Override
	protected void onDestroy() {
		recycle();
		super.onDestroy();
	}

	public void recycle() {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();// 回收bitmap
			bitmap = null;
			System.gc();
		}
	}

}
