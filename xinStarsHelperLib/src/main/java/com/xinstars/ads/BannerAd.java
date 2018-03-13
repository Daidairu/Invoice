package com.xinstars.ads;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.xinstars.ads.data.AdEvent;
import com.xinstars.ads.data.AdProfile;
import com.xinstars.ads.data.AdRequest;
import com.xinstars.ads.data.AdSize;
import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.ads.properties.AdsProperties;
import com.xinstars.ads.utils.DownloadImageTask;
import com.xinstars.ads.utils.HttpPostTask;
import com.xinstars.ads.utils.ITaskListener;
import com.xinstars.ads.utils.Utils;
import com.xinstars.ads.view.ViewUtils;
import com.xinstars.helper.R;
import com.xinstars.helper.utils.Debug;
import com.xinstars.helper.utils.UITools;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 展示橫幅廣告。
 * 目前的實作取消掉繼承 ViewGroup，採用自動 Layout 的方式，優點是用戶不需要自行管理 View 的生成，缺點是用戶無法自訂或控制更多 View 的內建功能。
 */
public class BannerAd /*extends ViewGroup*/ 
{
	//廣告參數
	private IAdListener _adListener;
	private AdSize _adSize;
	private String _storeID;
	private AdRequest _adRequest;
	private AdProfile _adProfile;


	//自動 Layout 元件、屬性暫存
	//private ViewGroup _layout_banner, _layout_root;
	private RelativeLayout _layout_banner;
	private ImageView _view_banner, _view_banner_btn; 
	private boolean _showCloseButton;
	private int _alignment = Gravity.TOP;
	private HttpPostTask httpPostTask;

	//定時更新暫存
	private boolean _isRefreshTest = false;	//開發時期使用
	private int _refreshTime = (_isRefreshTest ? 5 : 45 )*1000;
	private final int MIN_REFRESH_TIME = (_isRefreshTest ? 3 : 30 )*1000;
	private Timer _refreshTimer;
	private TimerTask _timerTask;
	private boolean isTimerRepeating;		//是否以啟動更新定時器

	private boolean _enableDebugLog = false;
	private boolean _showDefaultImage = false;
	private boolean _isInit = false;		//是否完成初始化
	private boolean _isAdrequest = false;	//是否正在撈取廣告
	private boolean _isAdReady = false;		//廣告是否準備好了
	private boolean _isShowing = false;		//是否正在顯示廣告
	private Activity _activity;
	

	/**
	 * 建立 BannerAd 物件
	 */
	public BannerAd()
	{
		//    super(activity);
	}

	/**
	 * 初始化
	 * <p>
	 * 從 SDK內的 /res/layout/com_xinstars_ads_banner.xml 建立 BannerAd View 並疊加到目前的 Layout 上。
	 * @param activity 目前運作中的 Activity。
	 * @param adID 申請的 adID。
	 * @param refreshTime 廣告更新時間，若值為 0 則不更新廣告，若不為 0 則必須大於 MIN_REFRESH_TIME(30秒)，預設為 45秒。
	 * @param showCloseButton 是否顯示關閉按鈕，預設關閉。
	 * @param showDefaultImage 載不到廣告時，是否顯示預設圖，預設不顯示。
	 * @param enableDebugLog 是否開啟偵錯 Log，預設不打開。
	 * @param adListener callback 接口。
	 * @see BannerAd.showBannerButton()。
	 */
	public void init(Activity activity, String adID){
		init(activity,adID,getRefreshTime(),_showCloseButton,_showDefaultImage,_enableDebugLog,null);
	}
	public void init(Activity activity, String adID, IAdListener adListener){
		init(activity,adID,getRefreshTime(),_showCloseButton,_showDefaultImage,_enableDebugLog,adListener);
	}
	public void init(Activity activity, String adID, int refreshTime, IAdListener adListener) {
		init(activity,adID,refreshTime,_showCloseButton,_showDefaultImage,_enableDebugLog,adListener);
	}
	public void init(Activity activity, String adID, int refreshTime, boolean showCloseButton, IAdListener adListener) {
		init(activity,adID,refreshTime,showCloseButton,_showDefaultImage,_enableDebugLog,adListener);
	}
	public void init(Activity activity, String adID, int refreshTime, boolean showCloseButton, boolean showDefaultImage, IAdListener adListener) {
		init(activity,adID,refreshTime,showCloseButton,showDefaultImage,_enableDebugLog,adListener);
	}
	public void init(Activity activity, String adID, int refreshTime, boolean showCloseButton, boolean showDefaultImage, boolean enableDebugLog, IAdListener adListener) {
		if( _isInit ) {
			
			return;
		}
		
		_activity = activity;
		AdsProperties.CURRENT_ACTIVITY = new WeakReference<Activity>(_activity);
		AdsProperties.APPLICATION_CONTEXT = _activity.getApplicationContext();
		AdsProperties.AD_ID = adID;

		//先建立預設的 AdSize 與 AdRequest 來完成初始化設定
		_adSize = AdSize.ORIGINAL;
		_adRequest = new AdRequest.Builder().build();

		enableDebugLog(enableDebugLog);
		setRefreshTime(refreshTime);
		setAdListener(adListener);
		enableDefaultImage(showDefaultImage);
		enableBannerButton(showCloseButton);
		initLayout();

		_isInit = true;
		Debug.log(true, AdsConstants.ADS_TAG, AdsConstants.STRING_BANNER_AD_INIT_DONE);
	}

	/**
	 * 設定 callback 接口。
	 * @param adListener callback 接口。
	 */
	public void setAdListener(IAdListener adListener) {
		_adListener = adListener;
	}

	/**
	 * 設定是否開啟偵錯 Log，預設不打開。
	 * @param enableDebugLog 是否開啟偵錯 Log，預設不打開。
	 */
	public void enableDebugLog(boolean enableDebugLog) {
		_enableDebugLog = enableDebugLog;
		Debug.enableLog(enableDebugLog);
	}

	public boolean getDebugSetting() {
		return _enableDebugLog;
	}
	
	/**
	 * 設定載不到廣告時，是否顯示預設圖，預設不顯示。
	 * @param showDefaultImage 載不到廣告時，是否顯示預設圖，預設不顯示。
	 */
	public void enableDefaultImage(boolean showDefaultImage) {
		_showDefaultImage = showDefaultImage;
	}

	public boolean getDefaultImageSetting() {
		return _showDefaultImage;
	}
	
	/**
	 * 自動建立 BannerAd 的 Layout。
	 * <p>
	 * 從 SDK內的 /res/layout/com_xinstars_ads_banner.xml 建立 BannerAd View 並疊加到目前的 Layout 上。
	 */
	@SuppressLint("InlinedApi")
	private void initLayout() {
		if( !AdsProperties.checkSDKInit() ) return;
			
		if( _layout_banner != null ) destroy(); 

//setContentView: 在 Unity 會閃屏, 改用 addContentView 
/*		
	//取得目前 activity 的 Layout
		_layout_root = (ViewGroup) ((ViewGroup) _activity.findViewById(android.R.id.content)).getChildAt(0);
		ViewUtils.removeViewFromParent(_layout_root);

	//取出 SDK 內的 banner layout
		 
		int res_id;
	//方法一: 將/res/ 下的資源, 複製到 App 專案的 /res/底下
		//res_id = _activity.getResources().getIdentifier(AdsConstants.LAYOUT_PREFIX +AdsConstants.LAYOUT_BANNER, "layout", _activity.getPackageName());
		
	//方法二: 將 SDK 作成一個 Lib project 讓 App 專案引用
		res_id = R.layout.com_xinstars_ads_banner;
		
		if( res_id == 0 ) {
			Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_LAYOUT_NOT_FOUND+AdsConstants.LAYOUT_PREFIX +AdsConstants.LAYOUT_BANNER, null );
		    return; 
		}
		_layout_banner = (ViewGroup) LayoutInflater.from(_activity).inflate(res_id, null);
		
	//將  _layout_banner 與 _layout_root 疊加在一起
		RelativeLayout rl = new RelativeLayout(_activity);
		rl.addView(_layout_root);
		rl.addView(_layout_banner,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		_activity.setContentView(rl);
*/
		
//addContentView
		int res_id;
		res_id = R.layout.com_xinstars_ads_banner;
		
		if( res_id == 0 ) {
			Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_LAYOUT_NOT_FOUND+AdsConstants.LAYOUT_PREFIX+AdsConstants.LAYOUT_BANNER, null );
		    return; 
		}
		
		_layout_banner = (RelativeLayout) LayoutInflater.from(_activity).inflate(res_id, null);
        _activity.addContentView(_layout_banner,new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        
	//設定 Banner 的 Play商店連結
		res_id = R.id.xinstars_imageview_banner;
		if( res_id == 0 ) {
			Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ID_NOT_FOUND+AdsConstants.NAME_IMAGE_BANNER, null );
		    return; 
		}
		
		_view_banner = (ImageView)_activity.findViewById(res_id);
		
		if( _view_banner != null ){
			_view_banner.setOnClickListener(new Button.OnClickListener(){ 
				@Override
				public void onClick(View v) {
					//如果這個 App 之前還沒安裝過, 啟動一個 10 分鐘的計時器, 檢查 App 是否被安裝
					//[注意] 目前沒有完全偵測玩家是否在點擊廣告後安裝應用，但應該可以應付大部分的狀況
					if( !Utils.isAppInstalled(_activity, _storeID) ) {
						new Timer(true).schedule(new TimerTask(){
							public void run() {
								if( Utils.isAppInstalled(_activity, _storeID) ) 
									if( _adProfile !=null ) AdEvent.send(AdsConstants.AD_TYPE_BANNER,AdsConstants.EVENT_TYPE_INSTALL_APP,_adProfile.getAd_SerialNo());
							}
						}, AdsConstants.INSTALL_CHECK_TIME);
					}
					
					if( _adListener != null ) _adListener.onAdLeftApplication();
					if( _adProfile !=null ) AdEvent.send(AdsConstants.AD_TYPE_BANNER,AdsConstants.EVENT_TYPE_CLICK,_adProfile.getAd_SerialNo());

					try {
						_activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AdsProperties.PLAYER_STORE_MARKET_URL+_storeID)));
					} catch (android.content.ActivityNotFoundException anfe) {
						_activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AdsProperties.PLAYER_STORE_WEB_URL+_storeID)));
					} finally {
						pause();
					}
				}         
			});
		} else {
			Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ID_NOT_FOUND+AdsConstants.NAME_IMAGE_BANNER, null);
		}
		
	//設定關閉按鈕	
		getBannerButton();
	}
	
	/**
	 * 是否顯示關閉按鈕。
	 * <p>
	 * 是否顯示關閉按鈕。
	 * @param showCloseButton 是否顯示關閉按鈕。
	 */
	public void enableBannerButton(boolean showCloseButton){
		_showCloseButton = showCloseButton;
		showBannerButton();
	}

	public boolean getButtonSetting(){
		return _showCloseButton;
	}
	
	private void getBannerButton() {

		if(_view_banner_btn == null ){
			
			int res_id = R.id.xinstars_imagebutton_close;
			
			if( res_id == 0 ) {
				Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ID_NOT_FOUND+AdsConstants.NAME_IMAGE_CLOSE, null );
			    return; 
			}
			
			_view_banner_btn = (ImageView)_activity.findViewById(res_id);
			_adSize.scaleImage(_activity,_view_banner_btn,32);
			
			if( _view_banner_btn != null ){
				_view_banner_btn.setOnClickListener(new Button.OnClickListener(){ 
					@Override
					public void onClick(View v) {
						hide();
					}         
				});
			} else {
				if(_showCloseButton) Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ID_NOT_FOUND+AdsConstants.NAME_IMAGE_CLOSE, null);
			}
		} 

		showBannerButton();
	}

	private void showBannerButton() {
		if( _view_banner_btn != null )
			_view_banner_btn.setVisibility(_showCloseButton ? View.VISIBLE : View.INVISIBLE);
	}
	/**
	 * 設定 Banner 顯示尺寸。
	 * <p>
	 * 設定需求的尺寸, 例如: 原圖尺寸(AdSize.ORIGINAL)、填滿螢幕寬度(AdSize.FILL_WIDTH)、自訂AdSize(width,height)。
	 * @param adSize 廣告尺寸。
	 */
	public void setAdSize(AdSize adSize)
	{
		if( !checkLayout(false) ) return;

		_adSize = adSize;
		_view_banner.setScaleType(_adSize.getScaleType() );
		_view_banner.setLayoutParams(_adSize.getLayoutParams(_activity,_view_banner));
		Debug.log(AdsConstants.ADS_TAG, "Set AdSize="+_adSize.getWidth() + "x" + _adSize.getHeight());
	}

	/**
	 * 設定對齊位置。
	 * <p>
	 * 根據對齊設定顯示 Banner。
	 * @param alignment 對齊設定，支援 Gravity.NO_GRAVITY | Gravity.TOP | Gravity.CENTER | Gravity.BOTTOM 。
	 */
	public void setAlignment(int alignment)
	{
		if( !checkLayout(false) ) return;
		_alignment = alignment;
		_layout_banner.setLayoutParams(_adSize.setLayoutGravity((FrameLayout.LayoutParams)_layout_banner.getLayoutParams(), alignment));
	}

	public int getAlignment()
	{
		return _alignment;
	}

	/**
	 * 設定 Banner 偏移位置。
	 * <p>
	 * 設定 Banner 偏移位置。
	 * @param x 偏移位置 x。
	 * @param y 偏移位置 y。
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setTranslationXY( int x, int y ){
		if( !checkLayout(false) ) return;
		_layout_banner.setTranslationX(UITools.convertToDp(_activity, x));
		_layout_banner.setTranslationY(UITools.convertToDp(_activity, y));
	}

	/**
	 * 設定 更新時間。
	 * <p>
	 * 若值為 0 則不更新廣告，若不為 0 則必須大於 MIN_REFRESH_TIME(30秒)，預設為 45秒。
	 * @param time 0 或 大於 MIN_REFRESH_TIME 的秒數。
	 */
	public void setRefreshTime( int time ) {
		if(time!=0 && (time*1000 < MIN_REFRESH_TIME ) ) 
			Debug.logWarning(true, AdsConstants.ADS_TAG, AdsConstants.STRING_ILLEGLE_REFRESH_TIME.replace("#1",""+(MIN_REFRESH_TIME/1000)),null);
		
		_refreshTime = time == 0 ? time : Math.max(time*1000, MIN_REFRESH_TIME);
	}

	public int getRefreshTime() {
		return (_refreshTime/1000);
	}

	/**
	 * 撈取 Banner 廣告。
	 * <p>
	 * 根據設定的廣告條件撈取廣告。
	 * @param adRequest 廣告條件 。
	 */
	public void loadAd()
	{
		loadAd(null);
	}
	
	public void loadAd(AdRequest adRequest)
	{
		if( !AdsProperties.checkSDKInit() ) return;
		if( adRequest == null ) adRequest = new AdRequest.Builder().build();
		
		_adRequest = adRequest;
		if( !_isInit ) return;

		getAdProfile();
	}

	private void getAdProfile() {
		if( isLoading() ) {
			Debug.logWarning(true, AdsConstants.ADS_TAG, AdsConstants.STRING_WAIT_ANOTHER_REQUEST, null);
			return;
		}

		_isAdrequest = true;
		
		httpPostTask = new HttpPostTask();
		httpPostTask.setListener(
				new ITaskListener() {
					@Override
					public void onTaskFinish( String result ) {
						try {
							_adProfile = new AdProfile(new JSONObject(result));
							_storeID = _adProfile.getStore_ID();
							getAdData();
						} catch (JSONException e) {
							if( !_enableDebugLog ) Debug.log(true, AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_GET_AD_PROFILE);
							else Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_RESPONSE, e);
							if( _adListener != null ) _adListener.onAdLoadFailed(AdsConstants.LOAD_AD_GET_PROFILE_FAILED);
							_isAdrequest = false;
						}
					}

					@Override
					public void onTaskFail(int errorCode) {
						if( !_enableDebugLog ) Debug.log(true, AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_GET_AD_PROFILE);
						if( _adListener != null ) _adListener.onAdLoadFailed(AdsConstants.LOAD_AD_GET_PROFILE_FAILED);
						//可能是網路錯誤, 不做後續處理
						_isAdrequest = false;
					}
				}
				);

		httpPostTask.execute(
				AdsProperties.BANNER_URL,
				AdsProperties.BANNER_TAG, 
				_adRequest.toJSONString()
				);
	}

	private void getAdData() {
		if( !checkLayout() ) return;

		//取不到廣告時，顯示SDK內預設的廣告
		if(_adProfile.getBanner_Url() == null && _adProfile.getBanner_Url().length() == 0) {
			if( !_enableDebugLog ) Debug.log(true, AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_GET_AD_BANNER);
			if( _adListener != null ) _adListener.onAdLoadFailed(AdsConstants.LOAD_AD_EMPTY_URL);
			setDefaultImage();
			_isAdrequest = false;
		} else {
			DownloadImageTask task = new DownloadImageTask(_view_banner);
			task.setListener(
					new ITaskListener() {
						@Override
						public void onTaskFinish( String result ) {
							if( _adListener != null ) _adListener.onAdLoaded();
							//自動高需要在取得圖片後設定顯示尺寸
							if( _adSize.isAutoHeight()) _view_banner.setLayoutParams(_adSize.getLayoutParams(_activity,_view_banner));
							_isAdrequest = false;
							_isAdReady = true;
							show();
						}

						@Override
						public void onTaskFail(int errorCode) {
							if( !_enableDebugLog ) Debug.log(true, AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_GET_AD_BANNER);
							if( _adListener != null ) _adListener.onAdLoadFailed(errorCode);
							//可能是網址錯誤或暫時性的網路問題, 先顯示預設圖
							setDefaultImage();
							_isAdrequest = false;
						}
					}
				);
			task.execute(_adProfile.getBanner_Url());
		}
	}

	private void setDefaultImage() {
		if(!_showDefaultImage) {
			resume();	//啟動計時器
			return;
		}
		
		Debug.log(AdsConstants.ADS_TAG, AdsConstants.STRING_SET_DEFAUT_IMAGE+AdsProperties.getDefauleDrawableImage());
		try {
			_view_banner.setImageDrawable(AdsProperties.getDefauleDrawableImage());	//塞預設圖
			_storeID = AdsProperties.DEFAULT_STORE_ID;
			_isAdReady = true;
			show();
		} catch (Exception e) {
			Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_SET_DEFAUT_IMAGE, e);
			resume();	//啟動計時器
		}
	}

	/**
	 * 是否正在要求廣告。
	 * @return 是否正在要求廣告。
	 */
	public boolean isLoading()
	{
		return _isAdrequest;
	}

	/**
	 * 是否已經初始化完成。
	 * @return 是否已經初始化完成。
	 */
	public boolean isInit()
	{
		return _isInit;
	}
	
	public boolean canShow()
	{
		return _isAdReady;
			
	}
	
	private boolean checkAdReady() {
		if(!canShow()){
			Debug.logWarning(true, AdsConstants.ADS_TAG, AdsConstants.STRING_CALL_LOADAD, null );
			return false;
		} else {
			return true;
		}
	}
	/**
	 * 顯示 Banner。
	 * @param alignment 對齊設定。
	 * @see BannerAd.show(int,boolean)。
	 */
	public void show() {
		show( _alignment, true );
	}

	/**
	 * 顯示 Banner。
	 * @param alignment 對齊設定。
	 * @see BannerAd.show(int,boolean)。
	 */
	public void show(int alignment) {
		show(alignment,false);
	}

	/**
	 * 顯示 Banner。
	 * <p>
	 * 根據對齊設定顯示 Banner。
	 * @param alignment 對齊設定，Gravity.NO_GRAVITY | Gravity.TOP | Gravity.CENTER | Gravity.BOTTOM。 
	 * @param fourceRefresh 是否強迫更新顯示配置屬性。
	 * @see BannerAd.setAlignment(int)。
	 */
	public void show(int alignment, boolean fourceRefresh) 
	{
		if( !checkLayout() ) return;
		if( !checkAdReady() ) return;
		_isShowing = true;
		
		if( _alignment != alignment || fourceRefresh ) setAlignment(alignment);
		if( _layout_banner !=null ) _layout_banner.setVisibility(View.VISIBLE);
		if( _adListener != null ) _adListener.onAdOpened();
		if( _adProfile !=null ) AdEvent.send(AdsConstants.AD_TYPE_BANNER,AdsConstants.EVENT_TYPE_SHOW,_adProfile.getAd_SerialNo());	//對應要求廣告的次數(Server紀錄)
		resume();
	}

	/**
	 * 隱藏 BannerAD。
	 */
	public void hide()
	{
		if( !checkAdReady() ) return;
		
		if(_isShowing){
			_isShowing = false;
			if( _layout_banner !=null ) _layout_banner.setVisibility(View.GONE);
			if( _adListener != null ) _adListener.onAdClosed();
			if( _adProfile !=null ) AdEvent.send(AdsConstants.AD_TYPE_BANNER,AdsConstants.EVENT_TYPE_CLOSE,_adProfile.getAd_SerialNo());
			pause();
		}
	}

	/**
	 * 暫停 Banner 廣告更新，移除更新定時器。這方法應該在 Activity的 onPause()中被呼叫。
	 */
	public void pause()
	{
		stopTimer();
	}

	/**
	 * 恢復 Banner 廣告更新，啟動更新定時器。這方法應該在 Activity的 onResume()中被呼叫。
	 */
	public void resume()
	{
		if( _isInit ) startTimer();
	}

	private void startTimer() {
		if( _refreshTime == 0 || !_isShowing ) return;
		if( isTimerRepeating ) stopTimer();	//確定同一時間只有一個 Timer 在執行

		Debug.log(AdsConstants.ADS_TAG, AdsConstants.STRING_SET_REFRESH_TIMER + (_refreshTime/1000));
		
		//Timer(boolean isDaemon) true 說明這個timer以daemon方式運行（優先級低，程序結束timer也自動結束）
		_refreshTimer = new Timer(true);
		initializeTimerTask();
		_refreshTimer.schedule(_timerTask,_refreshTime,_refreshTime);// delay, period 
		isTimerRepeating = true;
	}

	private void initializeTimerTask() {
		_timerTask = new TimerTask() {
			public void run() {
				getAdProfile();
			}
		};
	}

	private void stopTimer() {
		if (_refreshTimer != null) {
			//Debug.log(AdsConstants.ADS_TAG, AdsConstants.STRING_STOP_TIMER);
			_refreshTimer.cancel();
			_refreshTimer = null;
		}
		isTimerRepeating = false;
	}

	/**
	 * 將 BannerAD 從目前的 Layout 移除。這方法應該在 Activity的 onDestroy()中被呼叫。
	 */
	public void destroy()
	{
		stopTimer();
		ViewUtils.removeViewFromParent(_layout_banner);
		AdsProperties.reset();
		_view_banner_btn = null;
		_view_banner = null;
		_layout_banner = null;
		_isInit = false;
		_isAdReady = false;
	}

	private boolean checkLayout() {
		return checkLayout(true);
	}

	private boolean checkLayout( boolean reInit ) {
		if( reInit && _layout_banner == null ) initLayout();
		return ( _layout_banner != null );
	}

	
	//  protected void onLayout(boolean changed, int l, int t, int r, int b)
	//  {
	//    View adView = getChildAt(0);
	//    if ((adView != null) && (adView.getVisibility() != View.GONE))
	//    {
	//      int w = adView.getMeasuredWidth();
	//      int h = adView.getMeasuredHeight();
	//      int x = (r - l - w) / 2;
	//      int y = (b - t - h) / 2;
	//      adView.layout(x, y, x + w, y + h);
	//    }
	//  }

	//  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	//  {
	//    int w = 0;
	//    int h = 0;
	//    View adView = getChildAt(0);
	//    if ((adView != null) && (adView.getVisibility() != View.GONE))
	//    {
	//      measureChild(adView, widthMeasureSpec, heightMeasureSpec);
	//      w = adView.getMeasuredWidth();
	//      h = adView.getMeasuredHeight();
	//    }
	//    else
	//    {
	//      AdSize adSize = getAdSize();
	//      if (adSize != null)
	//      {
	//        Context context = getContext();
	//        w = adSize.getPixelWidth(_activity,_view_banner.getDrawable().getBounds().width());
	//        h = adSize.getPixelHeight(_activity,_view_banner.getDrawable().getBounds().height());
	//      }
	//    }
	//    w = Math.max(w, getSuggestedMinimumWidth());
	//    h = Math.max(h, getSuggestedMinimumHeight());
	//    setMeasuredDimension(View.resolveSize(w, widthMeasureSpec), View.resolveSize(h, heightMeasureSpec));
	//  }

}
