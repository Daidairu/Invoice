package com.xinstars.ads.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.helper.utils.Debug;

import android.os.AsyncTask;

public class HttpPostTask extends AsyncTask<String, Integer, HttpResponse>{
	
	ITaskListener _taskListener; 
	
	@Override
	protected HttpResponse doInBackground(String... params) {
		// TODO Auto-generated method stub
		return postData(params[0],params[1],params[2]);
	}

	protected void onPostExecute(HttpResponse response){
		
		if( response != null ) {
			//Debug.log(AdsConstants.ADS_TAG, AdsConstants.STRING_HTTP_RESPONSE_CODE+ response.getStatusLine().getStatusCode());
			
			if (response.getStatusLine().getStatusCode() == 200){
				BufferedReader reader;
				try {
					reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
					String str = reader.readLine();
					if( _taskListener != null ) _taskListener.onTaskFinish(str);
				} catch (UnsupportedEncodingException e) {
					Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_RESPONSE, e);
					if( _taskListener != null ) _taskListener.onTaskFail(AdsConstants.HTTPPOST_RESPONSE_ERROR);
				} catch (IllegalStateException e) {
					Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_RESPONSE, e);
					if( _taskListener != null ) _taskListener.onTaskFail(AdsConstants.HTTPPOST_RESPONSE_ERROR);
				} catch (IOException e) {
					Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_RESPONSE, e);
					if( _taskListener != null ) _taskListener.onTaskFail(AdsConstants.HTTPPOST_RESPONSE_ERROR);
				} catch (Exception e) {
					Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_RESPONSE, e);
					if( _taskListener != null ) _taskListener.onTaskFail(AdsConstants.HTTPPOST_RESPONSE_ERROR);
				}
				
			} else {
				Debug.log(true, AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_RESPONSE_FAIL + response.getStatusLine().getStatusCode());
				if( _taskListener != null ) _taskListener.onTaskFail(response.getStatusLine().getStatusCode());
			}
		} else {
			//已在上一步處理
		}
					
			
	}
	
	protected void onProgressUpdate(Integer... progress){
	}

	public HttpResponse postData(String url, String tag, String value ) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(tag, value));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			return httpclient.execute(httppost);
			  
		} catch (ClientProtocolException e) {
			Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_REQUEST, e);
			if( _taskListener != null ) _taskListener.onTaskFail(AdsConstants.HTTPPOST_INTERNAL_ERROR);
		} catch (IOException e) {
			Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_REQUEST, e);
			if( _taskListener != null ) _taskListener.onTaskFail(AdsConstants.HTTPPOST_NETWORK_ERROR);
		} catch (Exception e) {
			Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_HTTP_REQUEST, e);
			if( _taskListener != null ) _taskListener.onTaskFail(AdsConstants.HTTPPOST_UNKNOWN_ERROR);
		}
		
		return null;
	}

	public void setListener(ITaskListener listener) {
		_taskListener = listener ; 
	}
}