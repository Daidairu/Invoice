package com.xinstars.ads.view;

import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.helper.utils.Debug;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {
	public static void removeViewFromParent(View view) {
		if (view != null) {
			try {
				((ViewGroup)view.getParent()).removeView(view);
			}
			catch (Exception e) {
				Debug.log( AdsConstants.ADS_TAG, "Error while removing view from it's parent: " + e.getMessage());
			}
		}
	}
}
