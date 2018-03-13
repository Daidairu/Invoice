package com.xinstars.ads.utils;

import java.io.InputStream;

import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.helper.utils.Debug;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
  ImageView bmImage;
  ITaskListener _taskListener; 
  
  public DownloadImageTask(ImageView bmImage) {
      this.bmImage = bmImage;
  }

  protected Bitmap doInBackground(String... urls) {
      String urldisplay = urls[0];
      Debug.log(AdsConstants.ADS_TAG, AdsConstants.STRING_DOWNLOAD_IMAGE+urldisplay);
      Bitmap mIcon = null;
      try {
        InputStream in = new java.net.URL(urldisplay).openStream();
        mIcon = BitmapFactory.decodeStream(in);
      } catch (Exception e) {
          Debug.logError(AdsConstants.ADS_TAG, AdsConstants.STRING_ERROR_DOWNLOAD_IMAGE, e);
      }
      return mIcon;
  }

  protected void onPostExecute(Bitmap result) {
	  //Debug.log(AdsConstants.ADS_TAG, AdsConstants.STRING_DOWNLOAD_IMAGE + "onPostExecute");
	  if( result !=null ) {
		  bmImage.setImageBitmap(result);
		  if( _taskListener != null ) _taskListener.onTaskFinish(AdsConstants.STRING_DOWNLOAD_IMAGE_SUCCESS);
	  } else {
		  if( _taskListener != null ) _taskListener.onTaskFail(AdsConstants.LOAD_AD_FAILED);
	  }
  }

  public void setListener(ITaskListener listener) {
	  _taskListener = listener ; 
  }
}