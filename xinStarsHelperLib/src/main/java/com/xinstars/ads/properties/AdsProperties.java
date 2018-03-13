package com.xinstars.ads.properties;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import com.xinstars.ads.IAdListener;
import com.xinstars.helper.R;
import com.xinstars.helper.utils.Debug;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class AdsProperties {
	
	public static String PLAYER_STORE_MARKET_URL = "market://details?id=";
	public static String PLAYER_STORE_WEB_URL = "https://play.google.com/store/apps/details?id=";
	public static int DEFAULT_BANNER_DRAWABLE_ID = R.drawable.xinstars_banner;
	public static String DEFAULT_STORE_ID = "game.xin.hd.free";
	
	
	public static String AD_SERVER_URL = "http://210.244.17.60/XinStarsAdsService/XinStarsAdsService.asmx/";
	public static String BANNER_URL = AD_SERVER_URL + "GetAdProfile";
	public static String BANNER_TAG = "adRequest";
	public static String EVENT_URL = AD_SERVER_URL + "SetAdEvent";
	public static String EVENT_TAG = "adEvent";
	public static String INTERSTITIAL_URL = null;
	public static String VIDEO_URL = null;
	public static String AD_ID = null;		//申請的 AdID
	public static String APP_ID = null;		//保留
	public static String GAMER_ID = null;	//保留
		
	public static WeakReference<Activity> CURRENT_ACTIVITY = null;
	public static Context APPLICATION_CONTEXT = null;
	
	public static Activity getCurrentActivity() {
		if (CURRENT_ACTIVITY != null ) {
			return CURRENT_ACTIVITY.get();
		} 
		return null;
	}

	/**
	 * 檢查 SDK 是否已經完成初始化
	 */
	public static boolean checkSDKInit() {
		if (getCurrentActivity() == null ) {
			Debug.logError(true,AdsConstants.ADS_TAG, AdsConstants.STRING_CALL_INIT, null );
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 清除部分 SDK 初始化紀錄
	 */
	public static void reset() {
		AdsProperties.APPLICATION_CONTEXT = null;
		AdsProperties.CURRENT_ACTIVITY = null;
	}
	
	/**
	 * 取得SDK內預設的Banner
	 * <p>
	 * 撈不到廣告或發生錯誤時，改顯示預設圖 
	 * @param adSize 
	 */
	public static Drawable getDefauleDrawableImage() {
		return AdsProperties.APPLICATION_CONTEXT.getResources().getDrawable(AdsProperties.DEFAULT_BANNER_DRAWABLE_ID);  
	}
	
	public static IAdListener getEmptyAdListener() {
		return new IAdListener() {
			@Override
			public void onAdLoaded() {}

			@Override
			public void onAdLoadFailed(int errorCode) {}

			@Override
			public void onAdOpened() {}

			@Override
			public void onAdClosed() {}

			@Override
			public void onAdLeftApplication() {}
		};
	}
	
}