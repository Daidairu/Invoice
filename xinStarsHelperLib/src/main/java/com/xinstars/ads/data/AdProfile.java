package com.xinstars.ads.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.helper.utils.Debug;

/* AdProfile
 * 要求廣告的回傳 JSON 字串
 * 基本資料: 廣告網址、商店ID、更新時間(保留)、廣告流水號
 */
public class AdProfile
{
	private String _banner_Url;
	private String _store_ID;
	private int _refreshTime;		//保留: 是否要由 Server 控制更新時間
	private String _ad_SerialNo;	//廣告流水號
	
	public AdProfile(JSONObject jsonObject) {
		Debug.log(AdsConstants.ADS_TAG, "AdProfile="+jsonObject);
		try {
			_banner_Url = jsonObject.getString("Banner_Url");
		} catch (JSONException e) {
			Debug.logError(AdsConstants.ADS_TAG, "AdProfile JSON Exception!", e);
		}
		try {
			_store_ID = jsonObject.getString("Store_ID");
		} catch (JSONException e) {
			Debug.logError(AdsConstants.ADS_TAG, "AdProfile JSON Exception!", e);
		}
		
		try {
			_refreshTime = jsonObject.getInt("RefreshTime");
		} catch (JSONException e) {
			Debug.logError(AdsConstants.ADS_TAG, "AdProfile JSON Exception!", e);
		}
		
		try {
			_ad_SerialNo = jsonObject.getString("SerialNo");
		} catch (JSONException e) {
			Debug.logError(AdsConstants.ADS_TAG, "AdProfile JSON Exception!", e);
		}
	}
	public String getAd_SerialNo() {
		return _ad_SerialNo;
	}
	public void setAd_SerialNo(String ad_SerialNo) {
		_ad_SerialNo = ad_SerialNo;
	}
	public String getBanner_Url() {
		return _banner_Url;
	}
	public void setBanner_Url(String banner_Url) {
		_banner_Url = banner_Url;
	}
	public String getStore_ID() {
		return _store_ID;
	}
	public void setStore_ID(String store_ID) {
		_store_ID = store_ID;
	}
	public int getRefreshTime() {
		return _refreshTime;
	}
	public void setRefreshTime(int refreshTime) {
		_refreshTime = refreshTime;
	}
}
  
