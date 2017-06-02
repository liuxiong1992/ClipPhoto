package com.example.clipphotolib.classphoto;

import com.example.clipphotolib.util.LogUtils;
import com.example.clipphotolib.util.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * @author zhy http://blog.csdn.net/lmj623565791/article/details/39761281
 *         绘制阴影截图视图
 */
public class ClipImageBorderView extends View {
	/**
	 * 水平方向与View的边距
	 */
	private int mHorizontalPadding;
	/**
	 * 垂直方向与View的边距
	 */
	private int mVerticalPadding;
	/**
	 * 绘制的矩形的宽度
	 */
	private int mWidth;
	/**
	 * 绘制的矩形的高度
	 */
	private int mHeight;
	/**
	 * 边框的颜色，默认为白色
	 */
	private int mBorderColor = Color.parseColor("#FFFFFF");
	/**
	 * 边框的颜色，默认为#aa000000
	 */
	private int mShadowColor = Color.parseColor("#aa000000");
	/**
	 * 边框的宽度 单位dp
	 */
	private int mBorderWidth = 1;
	/**
	 * 图片的比例 宽度
	 */
	private float widthProportion = 10;
	/**
	 * 图片的比例高度
	 */
	private float heightProportion = 7;

	/**
	 * 设置宽高比例
	 * @param widthProportion
	 * @param heightProportion
	 */
	public void setProportion(float widthProportion,float heightProportion) {
		this.widthProportion = widthProportion;
		this.heightProportion = heightProportion;
	}

	

	private Paint mPaint;

	public ClipImageBorderView(Context context) {
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(widthProportion*1.0/heightProportion-getWidth()*1.0/getHeight()<0){  //设定的宽高比小于控件的宽高比，
			//  计算矩形区域的高度   保持垂直边距不变
			mHeight = getHeight() - 2 * mVerticalPadding;
			// 计算矩形区域的宽度 
			mWidth = Utils.getWidth(widthProportion,heightProportion, mHeight);
			//计算距离屏幕水平边界 的边距 
			mHorizontalPadding = (getWidth()- mWidth) / 2;
			LogUtils.d("----设定的宽高比小于控件的宽高比");
		}else {
			//保持水平边距不变
			// 计算矩形区域的宽度    控件的宽度-2*水平边距  
			mWidth = getWidth() - 2 * mHorizontalPadding;
			//  计算矩形区域的高度 
			mHeight= Utils.getHeight(widthProportion,heightProportion, mWidth);
			//计算距离屏幕垂直边界 的边距 
			mVerticalPadding = (getHeight() - mHeight) / 2;
		}	
		
		// mVerticalPadding = (getHeight() - mWidth) / 2;
		// mVerticalPadding = (getHeight() - (getWidth() - 2 *
		// mHorizontalPadding)) / 2;
		mPaint.setColor(mShadowColor);
		mPaint.setStyle(Style.FILL);
		// 绘制左边1
		canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
		// 绘制右边2
		canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(),
				getHeight(), mPaint);
		// 绘制上边3
		canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding,
				mVerticalPadding, mPaint);
		// 绘制下边4
		canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding,
				getWidth() - mHorizontalPadding, getHeight(), mPaint);
		// 绘制外边框
		mPaint.setColor(mBorderColor);
		mPaint.setStrokeWidth(mBorderWidth);
		mPaint.setStyle(Style.STROKE);
		canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()
				- mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);
		
		LogUtils.d("----1111---mVerticalPadding="+mVerticalPadding+"  mHorizontalPadding="+mHorizontalPadding);

	}
	
	//设置线框的线宽
	public void setBorderWidth(int width){
		this.mBorderWidth=width;
	}
	//设置线框的颜色
	public void setBorderColor(int color){
		this.mBorderColor=color;
	}
	//设置阴影的颜色
	public void setShadowColor(int color){
		this.mShadowColor=color;
	}
	//设置宽度占比
	public void setHorizontalPadding(int mHorizontalPadding) {
		LogUtils.d("------5");
		this.mHorizontalPadding = mHorizontalPadding;

	}
	//设置高度占比
	public void setVerticalPadding(int mVerticalPadding) {
		LogUtils.d("------6");
		this.mVerticalPadding = mVerticalPadding;
	}

}
