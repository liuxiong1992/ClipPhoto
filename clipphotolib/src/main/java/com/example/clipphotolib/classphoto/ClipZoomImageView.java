package com.example.clipphotolib.classphoto;

import com.example.clipphotolib.util.LogUtils;
import com.example.clipphotolib.util.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ClipZoomImageView extends ImageView implements
		OnScaleGestureListener, OnTouchListener,
		ViewTreeObserver.OnGlobalLayoutListener

{

	private static final String TAG = ClipZoomImageView.class.getSimpleName();
	public static float SCALE_MAX = 4.0f;
	private static float SCALE_MID = 2.0f;

	/**
	 * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
	 */
	private float initScale = 1.0f;
	private boolean once = true;

	/**
	 * 用于存放矩阵的9个值
	 */
	private final float[] matrixValues = new float[9];

	/**
	 * 缩放的手势检测
	 */
	private ScaleGestureDetector mScaleGestureDetector = null;
	private final Matrix mScaleMatrix = new Matrix();

	/**
	 * 用于双击检测
	 */
	private GestureDetector mGestureDetector;
	private boolean isAutoScale;

	private int mTouchSlop;

	private float mLastX;
	private float mLastY;

	private boolean isCanDrag;
	private int lastPointerCount;

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

	
	public ClipZoomImageView(Context context) {
		this(context, null);
	}

	public ClipZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setScaleType(ScaleType.MATRIX);
		mGestureDetector = new GestureDetector(context,
				new SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						if (isAutoScale == true)
							return true;

						float x = e.getX();
						float y = e.getY();
						if (getScale() < SCALE_MID) {
							ClipZoomImageView.this.postDelayed(
									new AutoScaleRunnable(SCALE_MID, x, y), 16);
							isAutoScale = true;
						} else {
							ClipZoomImageView.this.postDelayed(
									new AutoScaleRunnable(initScale, x, y), 16);
							isAutoScale = true;
						}

						return true;
					}
				});
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		this.setOnTouchListener(this);
	}

	/**
	 * 自动缩放的任务
	 * 
	 * @author zhy
	 */
	private class AutoScaleRunnable implements Runnable {
		static final float BIGGER = 1.07f;
		static final float SMALLER = 0.93f;
		private float mTargetScale;
		private float tmpScale;

		/**
		 * 缩放的中心
		 */
		private float x;
		private float y;

		/**
		 * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
		 * 
		 * @param targetScale
		 */
		public AutoScaleRunnable(float targetScale, float x, float y) {
			this.mTargetScale = targetScale;
			this.x = x;
			this.y = y;
			if (getScale() < mTargetScale) {
				tmpScale = BIGGER;
			} else {
				tmpScale = SMALLER;
			}

		}

		@Override
		public void run() {
			// 进行缩放
			mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
			checkBorder();
			setImageMatrix(mScaleMatrix);

			final float currentScale = getScale();
			// 如果值在合法范围内，继续缩放
			if (((tmpScale > 1f) && (currentScale < mTargetScale))
					|| ((tmpScale < 1f) && (mTargetScale < currentScale))) {
				ClipZoomImageView.this.postDelayed(this, 16);
			} else
			// 设置为目标的缩放比例
			{
				final float deltaScale = mTargetScale / currentScale;
				mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
				checkBorder();
				setImageMatrix(mScaleMatrix);
				isAutoScale = false;
			}

		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scale = getScale();
		float scaleFactor = detector.getScaleFactor();

		if (getDrawable() == null)
			return true;

		/**
		 * 缩放的范围控制
		 */
		if ((scale < SCALE_MAX && scaleFactor > 1.0f)
				|| (scale > initScale && scaleFactor < 1.0f)) {
			/**
			 * 最大值最小值判断
			 */
			if (scaleFactor * scale < initScale) {
				scaleFactor = initScale / scale;
			}
			if (scaleFactor * scale > SCALE_MAX) {
				scaleFactor = SCALE_MAX / scale;
			}
			/**
			 * 设置缩放比例
			 */
			mScaleMatrix.postScale(scaleFactor, scaleFactor,
					detector.getFocusX(), detector.getFocusY());
			checkBorder();
			setImageMatrix(mScaleMatrix);
		}
		return true;

	}

	/**
	 * 根据当前图片的Matrix获得图片的范围
	 * 
	 * @return
	 */
	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rect = new RectF();
		Drawable d = getDrawable();
		if (null != d) {
			rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mGestureDetector.onTouchEvent(event))
			return true;
		mScaleGestureDetector.onTouchEvent(event);

		float x = 0, y = 0;
		// 拿到触摸点的个数
		final int pointerCount = event.getPointerCount();
		// 得到多个触摸点的x与y均值
		for (int i = 0; i < pointerCount; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}
		x = x / pointerCount;
		y = y / pointerCount;

		/**
		 * 每当触摸点发生变化时，重置mLasX , mLastY
		 */
		if (pointerCount != lastPointerCount) {
			isCanDrag = false;
			mLastX = x;
			mLastY = y;
		}

		lastPointerCount = pointerCount;
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = x - mLastX;
			float dy = y - mLastY;

			if (!isCanDrag) {
				isCanDrag = isCanDrag(dx, dy);
			}
			if (isCanDrag) {
				if (getDrawable() != null) {

					RectF rectF = getMatrixRectF();
					// 如果宽度小于屏幕宽度，则禁止左右移动
					if (rectF.width() <= getWidth() - mHorizontalPadding * 2) {
						dx = 0;
					}
					// 如果高度小雨屏幕高度，则禁止上下移动
					if (rectF.height() <= getHeight() - mVerticalPadding * 2) {
						dy = 0;
					}
					mScaleMatrix.postTranslate(dx, dy);
					checkBorder();
					setImageMatrix(mScaleMatrix);
				}
			}
			mLastX = x;
			mLastY = y;
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			lastPointerCount = 0;
			break;
		}

		return true;
	}

	/**
	 * 获得当前的缩放比例
	 * 
	 * @return
	 */
	public final float getScale() {
		mScaleMatrix.getValues(matrixValues);
		return matrixValues[Matrix.MSCALE_X];
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * 水平方向与View的边距
	 */
	private int mHorizontalPadding;
	/**
	 * 垂直方向与View的边距
	 */
	private int mVerticalPadding;

	@Override
	public void onGlobalLayout() {
		if (once) {
			Drawable d = getDrawable();
			if (d == null)
				return;
			// 垂直方向的边距  注意 这里是实际裁剪的边距( 整个图片的高度 减去这个裁剪区域的高度 再除以2 就是实际裁剪区域 的垂直方向的边距)
//			mVerticalPadding = (getHeight() - Utils.getHeight(widthProportion,heightProportion,getWidth())) / 2;
			calculate();
			
			// mVerticalPadding = (getHeight() - (getWidth() - 2 *
			// mHorizontalPadding)) / 2 ;
			// Utils.showLog("距离垂直方向的边距"+mVerticalPadding + "  高度 = "
			// +getHeight() + " 宽度 = " + getWidth());
			int width = getWidth();
			int height = getHeight();
			// 拿到图片的宽和高
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			float scale = 1.0f;
			if (dw < getWidth() - mHorizontalPadding * 2
					&& dh > getHeight() - mVerticalPadding * 2) {
				scale = (getWidth() * 1.0f - mHorizontalPadding * 2) / dw;
			}

			if (dh < getHeight() - mVerticalPadding * 2
					&& dw > getWidth() - mHorizontalPadding * 2) {
				scale = (getHeight() * 1.0f - mVerticalPadding * 2) / dh;
			}

			if (dw < getWidth() - mHorizontalPadding * 2
					&& dh < getHeight() - mVerticalPadding * 2) {
				float scaleW = (getWidth() * 1.0f - mHorizontalPadding * 2)
						/ dw;
				float scaleH = (getHeight() * 1.0f - mVerticalPadding * 2) / dh;
				scale = Math.max(scaleW, scaleH);
			}

			initScale = scale;
			SCALE_MID = initScale * 2;
			SCALE_MAX = initScale * 4;
			mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
			mScaleMatrix.postScale(scale, scale, getWidth() / 2,
					getHeight() / 2);
			// 图片移动至屏幕中心
			setImageMatrix(mScaleMatrix);
			once = false;
		}

	}
	
	int mHeight=0;
	int mWidth=0;
	
	private  void calculate(){
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
	}

	/**
	 * 剪切图片，返回剪切后的bitmap对象
	 * 
	 * @return
	 */
	public Bitmap clip() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);// Bitmap source, int x, int y, int width, int
						// height)//实际在这里剪切
		LogUtils.d("----22222---mVerticalPadding="+mVerticalPadding+"  mHorizontalPadding="+mHorizontalPadding);
		Log.d("----","----x1="+mHorizontalPadding+"  y1="+mVerticalPadding);
		Log.d("----","----width="+(getWidth() - 2 * mHorizontalPadding)+"  height="+(Utils.getHeight(widthProportion,heightProportion,getWidth())));
		
		Bitmap bitmap1 = Bitmap.createBitmap(bitmap, mHorizontalPadding,
				mVerticalPadding, mWidth,
				mHeight);
/*		
		Bitmap bitmap1 = Bitmap.createBitmap(bitmap, mHorizontalPadding,
				mVerticalPadding, getWidth() - 2 * mHorizontalPadding,
				Utils.getHeight(widthProportion,heightProportion,getWidth()));
*/		// 如果原图 图片还没有回收，强制回收
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
		return bitmap1;
	}

	/**
	 * 边界检测
	 */
	private void checkBorder() {

		RectF rect = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();
		LogUtils.d("rect.width() =  " + rect.width()
				+ " , width - 2 * mHorizontalPadding ="
				+ (width - 2 * mHorizontalPadding));

		// 如果宽或高大于屏幕，则控制范围 ; 这里的0.001是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01
		if (rect.width() + 0.01 >= width - 2 * mHorizontalPadding) {
			if (rect.left > mHorizontalPadding) {
				deltaX = -rect.left + mHorizontalPadding;
			}
			if (rect.right < width - mHorizontalPadding) {
				deltaX = width - mHorizontalPadding - rect.right;
			}
		}
		if (rect.height() + 0.01 >= height - 2 * mVerticalPadding) {
			if (rect.top > mVerticalPadding) {
				deltaY = -rect.top + mVerticalPadding;
			}
			if (rect.bottom < height - mVerticalPadding) {
				deltaY = height - mVerticalPadding - rect.bottom;
			}
		}
		mScaleMatrix.postTranslate(deltaX, deltaY);

	}

	/**
	 * 是否是拖动行为
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	private boolean isCanDrag(float dx, float dy) {
		return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
	}

	public void setHorizontalPadding(int mHorizontalPadding) {
		LogUtils.d("------3");
		this.mHorizontalPadding = mHorizontalPadding;
	}

	public void setVerticalPadding(int mVerticalPadding) {
		LogUtils.d("------4");
		this.mVerticalPadding = mVerticalPadding;
	}

}
