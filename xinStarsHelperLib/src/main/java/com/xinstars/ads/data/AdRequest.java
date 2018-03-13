package com.xinstars.ads.data;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.ads.properties.AdsProperties;
import com.xinstars.helper.utils.Debug;

/* AdRequest
 * 產生撈取廣告的 JSON 字串
 * 基本參數: App ID、語系(國別)、申請的 AdID (保留)、關鍵字組(保留)
 * 附加資訊: Android OS、Device Model
 */

public final class AdRequest
{
	private String _gameID; 	//AppID(ex: app package_name)
	private String _locale;		//語系 Locale.getISO3Country()
	private String _adID; 		//申請的 AdID (保留)
	private String _keywords;	//關鍵字組 (保留)
	
	private AdRequest(Builder builder)
	{
		this._gameID = builder._gameID;
		this._locale = builder._locale;
		this._keywords = builder._keywords;
	}

	public String getGameId()
	{
		return _gameID;
	}

	public String getLocale()
	{
		return _locale;
	}
	
	public String getAdID()
	{
		return _adID;
	}
	
	public List<String> getKeywords()
	{
		List<String> items = Arrays.asList(_keywords.split(","));
		return items;
    }

	public String toJSONString() {
		JSONObject jsonObject= new JSONObject();
	    try {
	        jsonObject.put("GameID", getGameId());
	        jsonObject.put("Locale", getLocale());
	        jsonObject.put("AdID", getAdID());
	        //jsonObject.put("Keywords", new JSONArray(getKeywords()));
	        
	        //附加資訊
	        jsonObject.put("AndroidOS", android.os.Build.VERSION.RELEASE);
	        jsonObject.put("DeviceModel", android.os.Build.BRAND + "_" + android.os.Build.MODEL);
	        
	        Debug.log(AdsConstants.ADS_TAG, "AdRequest:" + jsonObject.toString());
	        return jsonObject.toString();
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return "";
	    }
	}
	
	public static class Builder
	{
		private String _gameID; 	//AppID(ex: app package_name)
		private String _locale;		//語系 Locale.getISO3Country()
		private String _keywords;	//保留

		public Builder()
		{
		}

		public Builder setGameID(String gameID)
		{
			this._gameID = gameID;
			return this;
		}

		public Builder setLocale(String value)
		{
			this._locale = value;
			return this;
		}

		//多個關鍵字用","隔開
		public Builder setKeywords(String value)
		{
			this._keywords = value;
			return this;
		}

		public AdRequest build()
		{
			if( !AdsProperties.checkSDKInit() ) return null;
			
			AdRequest request = new AdRequest(this);
			if(request._gameID==null) request._gameID = AdsProperties.APPLICATION_CONTEXT.getPackageName();
			if(request._locale==null) request._locale = Locale.getDefault().getISO3Country();
			if(request._adID==null) request._adID = AdsProperties.AD_ID;
			return request;
		}
	}
}