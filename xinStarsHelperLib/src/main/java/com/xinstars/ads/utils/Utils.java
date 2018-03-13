package com.xinstars.ads.utils;

import com.xinstars.ads.properties.AdsConstants;
import com.xinstars.helper.utils.Debug;

import android.content.Context;
import android.content.pm.PackageManager;

public class Utils {
	
	public static boolean isAppInstalled(Context context, String packageName) {
		boolean isAppInstalled;
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            isAppInstalled = true;
        }
        catch (PackageManager.NameNotFoundException e) {
        	isAppInstalled = false;
        }
        Debug.log(AdsConstants.ADS_TAG, packageName + " isAppInstalled:" + isAppInstalled);
        return isAppInstalled;
    }
}