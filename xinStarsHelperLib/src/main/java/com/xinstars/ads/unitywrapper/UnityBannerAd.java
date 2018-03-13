package com.xinstars.ads.unitywrapper;

import com.xinstars.ads.BannerAd;
import com.xinstars.ads.IAdListener;
import com.xinstars.ads.data.AdRequest;
import com.xinstars.ads.data.AdSize;

import android.app.Activity;

public class UnityBannerAd {
	private BannerAd bannerAd;
	private IAdListener _adListener;
	private Activity _activity;

	/**
	 * 建立 UnityBannerAd
	 *
	 * @param activity 目前運作中的 Activity。
	 * @param adListener callback 接口。
	 */
	public UnityBannerAd (Activity activity, IAdListener adListener) {
		bannerAd = new BannerAd();
		_activity = activity;
		_adListener = adListener;
	}

	/**
	 * 初始化
	 * @param adID 申請的 adID。
	 * @param refreshTime 廣告更新時間，若值為 0 則不更新廣告，若不為 0 則必須大於 MIN_REFRESH_TIME(30秒)，預設為 45秒。
	 * @param showCloseButton 是否顯示關閉按鈕，預設關閉。
	 * @param showDefaultImage 載不到廣告時，是否顯示預設圖，預設不顯示。
	 * @param enableDebugLog 是否開啟偵錯 Log，預設不打開。
	 */
	public void init(final String adID) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.init(_activity,adID,_adListener);
			}
		});
	}
	public void init(final String adID, final int refreshTime) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.init(_activity,adID,refreshTime,_adListener);
			}
		});
	}
	public void init(final String adID, final int refreshTime, final boolean showCloseButton) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.init(_activity,adID,refreshTime,showCloseButton,_adListener);
			}
		});
	}
	public void init(final String adID, final int refreshTime, final boolean showCloseButton, final boolean showDefaultImage) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.init(_activity,adID,refreshTime,showCloseButton,showDefaultImage,_adListener);
			}
		});
	}
	public void init(final String adID, final int refreshTime, final boolean showCloseButton, final boolean showDefaultImage, final boolean enableDebugLog) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.init(_activity,adID,refreshTime,showCloseButton,showDefaultImage,enableDebugLog,_adListener);
			}
		});
	}

	/**
	 * 撈取 Banner 廣告。
	 * <p>
	 * 根據設定的廣告條件撈取廣告。
	 * @param adRequest 廣告條件 。
	 */
	public void loadAd() {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.loadAd();
			}
		});
	}
	
	public void loadAd(final AdRequest request) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.loadAd(request);
			}
		});
	}

	/**
	 * 顯示 Banner。
	 * <p>
	 * 根據對齊設定顯示 Banner。
	 * @param alignment 對齊設定，Gravity.NO_GRAVITY | Gravity.TOP | Gravity.CENTER | Gravity.BOTTOM。 
	 * @see BannerAd.setAlignment(int)。
	 */
	public void show() {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.show();
			}
		});
	}

	public void show(final int alignment) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.show(alignment);
			}
		});
	}
	/**
	 * 隱藏 BannerAD。
	 */
	public void hide() {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.hide();
			}
		});
	}

	/**
	 * 將 BannerAD 從目前的 Layout 移除。這方法應該在 Activity的 onDestroy()中被呼叫。
	 */
	public void destroy() {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.destroy();
			}
		});
	}
	
	/**
	 * 設定 callback 接口。
	 * @param adListener callback 接口。
	 */
	public void setAdListener(final IAdListener adListener) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setAdListener(adListener);
			}
		});
	}
	
	/**
	 * 設定 Banner 顯示尺寸。
	 * <p>
	 * 設定需求的尺寸, 例如: 原圖尺寸(AdSize.ORIGINAL)、填滿螢幕寬度(AdSize.FILL_WIDTH)、自訂AdSize(width,height)。
	 * @param adSize 廣告尺寸。
	 */
	public void setAdSize(final AdSize adSize) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setAdSize(adSize);
			}
		});
	}
	/**
	 * 設定對齊位置。
	 * <p>
	 * 根據對齊設定顯示 Banner。
	 * @param alignment 對齊設定，支援 Gravity.NO_GRAVITY | Gravity.TOP | Gravity.CENTER | Gravity.BOTTOM 。
	 */
	public void setAlignment(final int alignment) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setAlignment(alignment);
			}
		});
	}
	/**
	 * 設定 更新時間。
	 * <p>
	 * 若值為 0 則不更新廣告，若不為 0 則必須大於 MIN_REFRESH_TIME(30秒)，預設為 45秒。
	 * @param time 0 或 大於 MIN_REFRESH_TIME 的秒數。
	 */
	public void setRefreshTime(final int time) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setRefreshTime(time);
			}
		});
	}
	/**
	 * 設定 Banner 偏移位置。
	 * <p>
	 * 設定 Banner 偏移位置。
	 * @param x 偏移位置 x。
	 * @param y 偏移位置 y。
	 */
	public void setTranslationXY(final int x, final int y) {
		_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setTranslationXY(x, y);
			}
		});
	}
	
}
