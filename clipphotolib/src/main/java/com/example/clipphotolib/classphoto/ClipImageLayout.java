package com.example.clipphotolib.classphoto;


import com.example.clipphotolib.R;
import com.example.clipphotolib.util.BitmapUtils;
import com.example.clipphotolib.util.LogUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 剪切视图
 */
public class ClipImageLayout extends RelativeLayout {

    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;
//    private int type = 0;//0 16:9  1 其他

    private Context mContext;
    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private int mHorizontalPadding = 1;
    private int mVerticalPadding = 1;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
      //获取边距
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClipImageLayout);
        float padding = typedArray.getDimension(R.styleable.ClipImageLayout_padding, 1);
        mHorizontalPadding=(int)padding;
        mVerticalPadding=(int)padding;
        //获取宽高所占比例
        float widthProportion = typedArray.getFloat(R.styleable.ClipImageLayout_widthRatio, 0);
        float heightProportion = typedArray.getFloat(R.styleable.ClipImageLayout_heightRatio, 0);
        
        int borderWidth = typedArray.getInt(R.styleable.ClipImageLayout_borderWidth, 2);
        int borderColor = typedArray.getColor(R.styleable.ClipImageLayout_borderColor, Color.parseColor("#ffffff"));
        int shadowColor = typedArray.getColor(R.styleable.ClipImageLayout_shadowColor, Color.parseColor("#aa000000"));
        
        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);
        
        setProportion(widthProportion,heightProportion);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        /**
         * 这里，直接写死了图片，
         */
        // mZoomImageView.setImageDrawable(getResources().getDrawable(
        // R.drawable.a));

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);
        
        mClipImageView.setBorderColor(borderColor);
        mClipImageView.setShadowColor(shadowColor);
        mClipImageView.setBorderWidth(borderWidth);
        
        LogUtils.d(" ----init---mHorizontalPadding="+mHorizontalPadding);
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
        mZoomImageView.setVerticalPadding(mVerticalPadding);
        mClipImageView.setVerticalPadding(mVerticalPadding);

    }

    public void setImageDrawable(Drawable drawable) {
        mZoomImageView.setImageDrawable(drawable);
    }

    public void setImageDrawable(String path) {
    	//显示本地图片  设置本地图片
//        String showPath = ImageDownloader.Scheme.FILE.wrap(path);
    	BitmapUtils bitmapUtils = new BitmapUtils(mContext);
    	Bitmap bitmap = bitmapUtils.decodeFile(path);
    	mZoomImageView.setImageBitmap(bitmap);
//        ImageLoader.getInstance().displayImage(showPath, mZoomImageView);
//		mZoomImageView.setImageDrawable(drawable);
    }

	/**
	 * 设置宽高比例
	 * @param widthProportion
	 * @param heightProportion
	 */
	public void setProportion(float widthProportion,float heightProportion) {
		mClipImageView.setProportion(widthProportion, heightProportion);
		mZoomImageView.setProportion(widthProportion, heightProportion);
	}

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

}
