package com.example.clipphotolib.util;

public class Utils {
	
	/**
	 * 获取控件的高度
	 * @param widthProportion
	 * @param heightProportion
	 * @param width
	 * @return
	 */
	public static int getHeight(float widthProportion,float heightProportion,int width) {
		LogUtils.d("----widthProportion="+widthProportion+"  heightProportion="+heightProportion+"  width="+width);
		float temp = width / widthProportion;
		
		return (int)(temp * heightProportion);
	}
	
	/**
	 * 获取控件的宽度
	 * @param widthProportion
	 * @param heightProportion
	 * @param height
	 * @return
	 */
	public static int getWidth(float widthProportion,float heightProportion,int height) {
		LogUtils.d("----widthProportion="+widthProportion+"  heightProportion="+heightProportion+"  height="+height);
		float temp = height / heightProportion;
		
		return (int) (temp * widthProportion);
	}

}
