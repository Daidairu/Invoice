package com.xinstars.ads.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.ads.properties.AdsProperties;
import com.xinstars.ads.utils.HttpPostTask;
import com.xinstars.ads.utils.ITaskListener;
import com.xinstars.helper.utils.Debug;

/* AdEvent
 * 產生廣告回call 的 JSON 字串
 * 基本參數: 廣告類型、活動類型、廣告流水號
 * 廣告類型: 橫幅、插屏、 影片
 * 活動類型: 展示、關閉、點擊、安裝
 */
public final class AdEvent
{
	private int _adType; 		//廣告類型
	private int _eventType; 	//活動類型
	private String ad_SerialNo; //廣告流水號
	
	public int getEventType() {
		return _eventType;
	}

	public void setEventType(int _eventType) {
		this._eventType = _eventType;
	}

	public int getAdType() {
		return _adType;
	}

	public void setAdType(int _adType) {
		this._adType = _adType;
	}
	
	public void setAd_SerialNo(String ad_SerialNo) {
		this.ad_SerialNo = ad_SerialNo;
	}
	
	public String getAd_SerialNo() {
		return ad_SerialNo;
	}
	
	private AdEvent(int adType, int eventType, String ad_SerialNo)
	{
		setAdType(adType);
		setEventType(eventType);
		setAd_SerialNo(ad_SerialNo);
	}
	
	public String toJSONString() {
		JSONObject jsonObject= new JSONObject();
	    try {
	        jsonObject.put("AdType", getAdType());
	        jsonObject.put("EventType", getEventType());
	        jsonObject.put("SerialNo", getAd_SerialNo());
	        
	        Debug.log(AdsConstants.ADS_TAG, "AdEvent:" + jsonObject.toString());
	        return jsonObject.toString();
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return "";
	    }
	}
	
	public static void send(int adType, int eventType, String ad_SerialNo){
		HttpPostTask httpPostTask = new HttpPostTask();
		httpPostTask.setListener(
				new ITaskListener() {
					@Override
					public void onTaskFinish( String result ) {
						Debug.log(AdsConstants.ADS_TAG, "AdEvent Send Result:" + result);	
					}

					@Override
					public void onTaskFail(int errorCode) {
						Debug.log(AdsConstants.ADS_TAG, "AdEvent send failed:" + errorCode);	
					}
				}
				);

		httpPostTask.execute(
				AdsProperties.EVENT_URL,
				AdsProperties.EVENT_TAG, 
				new AdEvent(adType,eventType,ad_SerialNo).toJSONString()
				);
	}

	
}