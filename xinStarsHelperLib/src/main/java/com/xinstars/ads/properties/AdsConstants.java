package com.xinstars.ads.properties;

public class AdsConstants {
	//Tag
	public static final String ADS_TAG = "XinStarsAds";
	
	// Ads Type
	public static final int AD_TYPE_BANNER = 0;
	public static final int AD_TYPE_INTERSTITIAL = 1;
	public static final int AD_TYPE_VIDEO = 2;

	// Ads Layout
	public static final String LAYOUT_PREFIX = "com_xinstars_ads_";
	public static final String LAYOUT_BANNER = "banner";
	public static final String LAYOUT_INTERSTITIAL = "interstitial";
	public static final String LAYOUT_VIDEO = "video";

	//LoadAD Error Code
	public static final int LOAD_AD_FAILED = 0;
	public static final int LOAD_AD_EMPTY_URL = 1;
	public static final int LOAD_AD_GET_PROFILE_FAILED = 2;

	//HTTP POST Error Code
	public static final int HTTPPOST_JSON_FORMATE_ERROR = 0;
	public static final int HTTPPOST_INTERNAL_ERROR = 1;
	public static final int HTTPPOST_NETWORK_ERROR = 2;
	public static final int HTTPPOST_UNKNOWN_ERROR = 3;
	public static final int HTTPPOST_RESPONSE_ERROR = 4;

	//Common Error Code
	public static final int ERROR_CODE_INTERNAL_ERROR = 0;
	public static final int ERROR_CODE_INVALID_REQUEST = 1;
	public static final int ERROR_CODE_NETWORK_ERROR = 2;
	public static final int ERROR_CODE_NO_FILL = 3;	//不應該發生

	//Event Type
	public static final int EVENT_TYPE_SHOW = 0;
	public static final int EVENT_TYPE_CLOSE = 1;
	public static final int EVENT_TYPE_CLICK = 2;
	public static final int EVENT_TYPE_INSTALL_APP = 3;

	public static final int INSTALL_CHECK_TIME = 10*60*1000;	//應用是否已安裝的檢查時間

	//RequestCode for ActivityResult
	public static final int REQUEST_CODE_BANNER = 1;
	public static final int REQUEST_CODE_INTERSTITIAL = 2;
	public static final int REQUEST_CODE_VIDEO = 3;
	
	//String
	public static final String STRING_CALL_ENABLE_DEBUG = "Call enabledDebugLog(true) to see more info.";
	public static final String STRING_CALL_INIT = "You should call init(...) before any other API call!";
	public static final String STRING_CALL_LOADAD = "No ad now or you should to call loadAd() first!";
	public static final String STRING_ALREADY_INIT = "The BannerAd has init, call destroy first";
	
	public static final String STRING_LAYOUT_NOT_FOUND = "Can not find layout of ";
	public static final String STRING_ID_NOT_FOUND = "Can not find id of ";
	
	public static final String STRING_ERROR_HTTP_REQUEST = "Http request error!";
	public static final String STRING_ERROR_HTTP_RESPONSE = "Http response error!";
	public static final String STRING_ERROR_HTTP_RESPONSE_FAIL = "Http response failed. response code: ";
	public static final String STRING_ERROR_GET_AD_PROFILE = "Get Ad profile failed. " + STRING_CALL_ENABLE_DEBUG;
	public static final String STRING_ERROR_GET_AD_BANNER = "Get Ad banner failed. " + STRING_CALL_ENABLE_DEBUG;
	public static final String STRING_ERROR_SET_DEFAUT_IMAGE = "Set default image failed.";
	public static final String STRING_ERROR_DOWNLOAD_IMAGE = "Download image failed.";
	public static final String STRING_ILLEGLE_REFRESH_TIME = "The refresh time should not less than #1 seconds, it will be set to #1";
	public static final String STRING_ILLEGLE_ADSIZE = "The #1 value(#2) of AdSize is invalid, it will be set to default value(AdSize.ORIGINAL_SIZE).";
	
	public static final String STRING_SET_DEFAUT_IMAGE = "Set default image: ";
	public static final String STRING_ANY_ERROR = " If any error occur, ";
	public static final String STRING_BANNER_AD_INIT_DONE = "BannerAd init done!" + STRING_ANY_ERROR + STRING_CALL_ENABLE_DEBUG;
	public static final String STRING_WAIT_ANOTHER_REQUEST = "Wait another request, ignore this!";
	public static final String STRING_SET_REFRESH_TIMER = "Set refresh timer:";
	public static final String STRING_STOP_TIMER = "Stop timer";
	public static final String STRING_DOWNLOAD_IMAGE_SUCCESS = "Download image successful.";
	public static final String STRING_DOWNLOAD_IMAGE = "Download image: ";
	public static final String STRING_HTTP_RESPONSE_CODE = "Http response code: ";
	
	public static final String NAME_LAYOUT_BANNER = "'xinstars_imageview_banner'";
	public static final String NAME_IMAGE_BANNER = "'xinstars_imageview_banner'";
	public static final String NAME_IMAGE_CLOSE = "'xinstars_imagebutton_close'";

	
	
	
	
	
	
}
