# 图片剪裁——自定义比例

这个demo是在	
<a href="http://blog.csdn.net/liudao7994/article/details/53694204" target="_blank">android 自定义比例裁剪图片(拍照 相册)方式 自定义比例</a>
	的基础上改的，主要是完成当宽高比小于ClipImageLayout的宽高比时出现的问题

###使用方式 studio
前面的版本有问题，请直接用3.1以后的版本，第一次做依赖库……

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
		    compile 'com.github.liuxiong1992:ClipPhoto:v3.1'
		}

### 效果




### 布局文件
	<!--  宽比高  27:48 = 1080:1920 边距50dp -->
    <com.example.clipphotolib.classphoto.ClipImageLayout
        android:id="@+id/id_clipImageLayout"
        app:clip_padding="50dp"
        app:clip_widthRatio="27"   
        app:clip_heightRatio="48"
        app:clip_borderWidth="2"
        app:clip_borderColor="#00A0E4"
        app:clip_shadowColor="#aa000000"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent" >
    </com.example.clipphotolib.classphoto.ClipImageLayout>

#### 各属性的含义
	<declare-styleable name="ClipImageLayout">
        <attr name="clip_padding" format="dimension"></attr>    <!--  边距  -->
        <attr name="clip_widthRatio" format="float"></attr>  	<!--  宽度占周长的比例  -->
        <attr name="clip_heightRatio" format="float"></attr>  	<!--  高度占周长的比例 -->
        <attr name="clip_borderWidth" format="integer"></attr>  	<!--  线框的线宽 -->
        <attr name="clip_borderColor" format="color"></attr>  	<!--  线框的颜色 -->
        <attr name="clip_shadowColor" format="color"></attr>  	<!--  阴影的颜色 -->
    </declare-styleable>


	
	