package com.xinstars.ads.data;

import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.helper.utils.Debug;
import com.xinstars.helper.utils.UITools;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public final class AdSize
{
  
  public static final int ORIGINAL_SIZE = -1;					//圖片原始尺寸
  public static final int SCREEN_WIDTH = -2;					//螢幕寬
  public static final int SCREEN_HEIGHT = -3;					//螢幕高
  public static final int AUTO_HEIGHT = -4;						//自動高
  
  public static final AdSize ORIGINAL = new AdSize(ORIGINAL_SIZE, ORIGINAL_SIZE);		//圖片原始尺寸
  public static final AdSize FILL_WIDTH = new AdSize(SCREEN_WIDTH, AUTO_HEIGHT);		//螢幕寬+自動高
  public static final AdSize FULL_SCREEN = new AdSize(SCREEN_WIDTH, SCREEN_HEIGHT);		//全屏(Banner不應該使用這個)
  
  private int _width;
  private int _height;

  
  /* TODO: SmartSize
   * 指定一個基底版面, 並設定尺寸後, 隨著不同解析度, 自動縮放
   */
//  private boolean _isSmartSize = false;	
//  private int _baseScreenWidth;
//  private int _baseScreenHeight;
//
//  public boolean isSmartSize() {
//	  return _isSmartSize;
//  }
//
//  public void setAutoSize(boolean isSmartSize, int baseScreenWidth, int baseScreenHeight ) {
//	  _isSmartSize = isSmartSize;
//	  _baseScreenWidth = baseScreenWidth;
//	  _baseScreenHeight = baseScreenHeight;
//  }

  
  public AdSize(int width, int height)
  {
    if ((width < 0) && (width != ORIGINAL_SIZE) && (width != SCREEN_WIDTH)) {
      Debug.logWarning(true, AdsConstants.ADS_TAG, 
    		  AdsConstants.STRING_ILLEGLE_ADSIZE.replace("#1","width").replace("#2",""+width),null);
      
      width = ORIGINAL_SIZE;
    }
    if ((height < 0) && (height != ORIGINAL_SIZE) && (height != SCREEN_HEIGHT) && (height != AUTO_HEIGHT)){
      Debug.logWarning(true, AdsConstants.ADS_TAG, 
    		  AdsConstants.STRING_ILLEGLE_ADSIZE.replace("#1","height").replace("#2",""+height),null);
      height = ORIGINAL_SIZE;
    }
    this._width = width;
    this._height = height;
  }

  public int getWidth()
  {
    return this._width;
  }

  public int getPixelWidth(Context context, int originalValue)
  {
	  switch (this._width) {
		  case ORIGINAL_SIZE:
		  	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, originalValue, context.getResources().getDisplayMetrics() );	
		  case SCREEN_WIDTH:
		  	return context.getResources().getDisplayMetrics().widthPixels;
  		  default:
	  		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this._width, context.getResources().getDisplayMetrics() );
	  }
  }

  public int getHeight()
  {
    return this._height;
  }

  public int getPixelHeight(Context context, int originalValue)
  {
	  switch (this._width) {
	    case ORIGINAL_SIZE:
	  		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, originalValue, context.getResources().getDisplayMetrics() );
	    case SCREEN_HEIGHT:
			return context.getResources().getDisplayMetrics().heightPixels;
	    case AUTO_HEIGHT:
			return -1;	
	  	default:
	  		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this._height, context.getResources().getDisplayMetrics() );
	  }
  }

  public ScaleType getScaleType() {
	  
//	  if ( isFullWidth() && isAutoHeight() ) return ScaleType.CENTER_INSIDE;
//	  else return ScaleType.FIT_XY;
	  return ScaleType.FIT_XY;
  }
  
  public boolean isFullWidth()
  {
    return this._width == SCREEN_WIDTH;
  }

  public boolean isFullScreen()
  {
    return this._width == SCREEN_WIDTH && this._height == SCREEN_HEIGHT;
  }
  
  public boolean isAutoHeight()
  {
    return this._height == AUTO_HEIGHT;
  }
  
  
  /* RelativeLayout、ImageView 設定對齊與尺寸
   *  
   */
  @SuppressWarnings("deprecation")
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public RelativeLayout.LayoutParams getLayoutParams(Context context, ImageView view, int alignment) {
	  
	  int param_w;
	  int param_h;
	  
	  param_w = ( isFullScreen() || isFullWidth() ) ? LayoutParams.FILL_PARENT : LayoutParams.WRAP_CONTENT;
	  param_h = ( isFullScreen() ) ? LayoutParams.FILL_PARENT : LayoutParams.WRAP_CONTENT;
	  
	  RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(param_w,param_h);
	  
	  if(view != null){
		if(this._width>0 && view.getDrawable()!=null) layoutParams.width = getPixelWidth(context, view.getDrawable().getIntrinsicWidth());
		
		if(this._height>0 && view.getDrawable()!=null) layoutParams.height = getPixelHeight(context, view.getDrawable().getIntrinsicHeight());
		else if( isAutoHeight() ) {
			if(view.getDrawable()!=null&&view.getDrawable().getIntrinsicWidth()!=0){
				int temp_w = getPixelWidth(context, view.getDrawable().getIntrinsicWidth()); 
				layoutParams.height = (int)((temp_w*view.getDrawable().getIntrinsicHeight())/view.getDrawable().getIntrinsicWidth());
				//Debug.log(AdsConstants.ADS_TAG, "banner="+view.getDrawable().getIntrinsicWidth()+"x"+view.getDrawable().getIntrinsicHeight());
				//Debug.log(AdsConstants.ADS_TAG, "layoutParams="+temp_w+"x"+layoutParams.height);
			}
			
			//TODO: 若同時是全螢幕寬, 在螢幕旋轉後, 高要重新計算
//			if( isFullWidth()  ) {
//				view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
//				    @Override
//				    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
//				                    int oldTop, int oldRight, int oldBottom) {
//				        // TODO Auto-generated method stub  
//				    	Debug.log(AdsConstants.ADS_TAG, "addOnLayoutChangeListener left="+left);
//				    	Debug.log(AdsConstants.ADS_TAG, "addOnLayoutChangeListener right="+right);
//				    	Debug.log(AdsConstants.ADS_TAG, "addOnLayoutChangeListener oldLeft="+oldLeft);
//				    	Debug.log(AdsConstants.ADS_TAG, "addOnLayoutChangeListener oldRight="+oldRight);
//				    }
//				});
//			}
		}
		
	  }	
	  if( (alignment & Gravity.TOP) == Gravity.TOP ) {
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
      }
	
	  if( (alignment & Gravity.BOTTOM) == Gravity.BOTTOM ) {
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
      }
		
	  if( (alignment & Gravity.CENTER) == Gravity.CENTER ) {
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
	  }
	  return layoutParams;
  }

  /* ImageView 設定尺寸
   * 
   */
  @SuppressWarnings("deprecation")
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public RelativeLayout.LayoutParams getLayoutParams(Context context, ImageView view) {
	  
	  int param_w;
	  int param_h;
	  
	  param_w = ( isFullScreen() || isFullWidth() ) ? LayoutParams.FILL_PARENT : LayoutParams.WRAP_CONTENT;
	  param_h = ( isFullScreen() ) ? LayoutParams.FILL_PARENT : LayoutParams.WRAP_CONTENT;
	  
	  RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(param_w,param_h);
	  
	  if(view != null){
		if(this._width>0 && view.getDrawable()!=null) layoutParams.width = getPixelWidth(context, view.getDrawable().getIntrinsicWidth());
		
		if(this._height>0 && view.getDrawable()!=null) layoutParams.height = getPixelHeight(context, view.getDrawable().getIntrinsicHeight());
		else if( isAutoHeight() ) {
			if(view.getDrawable()!=null&&view.getDrawable().getIntrinsicWidth()!=0){
				int temp_w = getPixelWidth(context, view.getDrawable().getIntrinsicWidth()); 
				layoutParams.height = (int)((temp_w*view.getDrawable().getIntrinsicHeight())/view.getDrawable().getIntrinsicWidth());
				//Debug.log(AdsConstants.ADS_TAG, "banner="+view.getDrawable().getIntrinsicWidth()+"x"+view.getDrawable().getIntrinsicHeight());
				//Debug.log(AdsConstants.ADS_TAG, "layoutParams="+temp_w+"x"+layoutParams.height);
			}
			
			//TODO: 若同時是全螢幕寬, 在螢幕旋轉後, 高要重新計算
//			if( isFullWidth()  ) {
//				view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
//				    @Override
//				    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
//				                    int oldTop, int oldRight, int oldBottom) {
//				        // TODO Auto-generated method stub  
//				    	Debug.log(AdsConstants.ADS_TAG, "addOnLayoutChangeListener left="+left);
//				    	Debug.log(AdsConstants.ADS_TAG, "addOnLayoutChangeListener right="+right);
//				    	Debug.log(AdsConstants.ADS_TAG, "addOnLayoutChangeListener oldLeft="+oldLeft);
//				    	Debug.log(AdsConstants.ADS_TAG, "addOnLayoutChangeListener oldRight="+oldRight);
//				    }
//				});
//			}
		}
	  }	
	  return layoutParams;
  }
  
  /* FrameLayout 設定對齊
   * 
   */
  public FrameLayout.LayoutParams setLayoutGravity(FrameLayout.LayoutParams flp, int alignment) {
	  switch(alignment) {
	  case Gravity.TOP:
	  default:	  
		  flp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		  break;

	  case Gravity.BOTTOM:
		  flp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		  break;

	  case Gravity.CENTER:
		  flp.gravity = Gravity.CENTER;
		  break;
	  }
	  return flp;  
  }

  /* 將 ImageView 根據的對應的螢幕解析度重新設定尺寸
   */
  @SuppressWarnings("deprecation")
  public void scaleImage(Context context, ImageView view, int originalDP ) {
	    // Get bitmap from the the ImageView.
	    Bitmap bitmap = null;
	    int width = 0;
	    int height = 0;
	    try {
	        Drawable drawing = view.getDrawable();
	        bitmap = ((BitmapDrawable) drawing).getBitmap();
	        width = bitmap.getWidth();
	        height = bitmap.getHeight();
	    } catch (Exception e) {
	        return;
	    }
	    
	    int bounding = UITools.convertToPixel(context, originalDP);
	    
	    float xScale = ((float) bounding) / width;
	    float yScale = ((float) bounding) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;
	    
	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView 
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    width = scaledBitmap.getWidth(); // re-use
	    height = scaledBitmap.getHeight(); // re-use
	    BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	    
	    // Apply the scaled bitmap
	    view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams(); 
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}
}