package com.xinstars.helper.utils;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class CommonTools
{
	public final static String TAG = CommonTools.class.getSimpleName();

	public static String getUniqueId(Context context)
	{
		String uniqueId;
		uniqueId = (context == null) ? "" : getIMEI(context);
		if (!TextUtils.isEmpty(uniqueId)) return "I_" + uniqueId;

		Debug.log(TAG, "Can not find IEMI, try to find serial number.");
		uniqueId = getSerialNumber();
		if (!TextUtils.isEmpty(uniqueId)) return "S_" + uniqueId;

		Debug.log(TAG, "Can not find valid serial number, return empty.");
		
		//[Boxer modify]
		uniqueId = "T_" + new Date().getTime();
		return uniqueId;
	}

	/**
	 * 返回裝置的IMEI(for GSM)
	 * <p>
	 * 若TELEPHONY_SERVICE服務不存在、或無讀取手機狀態權限(permission.READ_PHONE_STATE)則返回空字串
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context)
	{
		if (context == null) throw new IllegalArgumentException("Contex cant be null");

		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		boolean permission = (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context,
		        Manifest.permission.READ_PHONE_STATE));

		if (manager == null || !permission)
			return "";
		else
			return (manager.getDeviceId() == null) ? "" : manager.getDeviceId();
	}

	/**
	 * 返回裝置的serial number
	 * @return 若裝置的Android version < 9或不為合法的serial Number則返回空字串
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static String getSerialNumber()
	{
		if (Build.VERSION.SDK_INT >= 9)
		{
			String serialNum = Build.SERIAL;
			if (checkValidSerialNumber(serialNum))
				return serialNum;
			else
				return "";
		}
		else
		{
			return "";
		}
	}

	/**
	 * <p>
	 * 檢查SerialNumber是否為合法的，目前有發現部分手機會出現000000000000， 所以符合一般SerialNumber中
	 * 若SerialNumber字符全為0則視為非法的
	 * </p>
	 * <p>
	 * Serial number check :
	 * <p>
	 * https://android.googlesource.com/platform/cts/+/master
	 * /tests/tests/os/src/android/os/cts/BuildTest.java#159
	 * <p>
	 * Serial Number Bug :
	 * <p>
	 * Serial Number reset to 000000000000 in Samsung Galaxy S2 (GT - I9100)
	 * after Stock 4.0.4 upgrade
	 * https://code.google.com/p/android/issues/detail?id=35193
	 * @param serialNum
	 * @return
	 */
	public static boolean checkValidSerialNumber(String serialNum)
	{
		Pattern validPattern = Pattern.compile("^([0-9A-Za-z]{6,20})$");
		if (validPattern.matcher(serialNum).matches())
		{
			Pattern allZeroPattern = Pattern.compile("^(0{6,20})$");
			Matcher allZeroMatcher = allZeroPattern.matcher(serialNum);
			if (allZeroMatcher.find() && allZeroMatcher.group().equals(serialNum))
				return false;
			else
				return true;
		}
		return false;
	}
	
	public static int getIntentIntExtra(Activity activity, String key, int defaultValue)
	{
		Intent intent = activity.getIntent();
		if(intent != null)
			return intent.getIntExtra(key, defaultValue);
		return defaultValue;
	}
	
	public static String getIntentStringExtra(Activity activity, String key, String defaultValue)
	{
		Intent intent = activity.getIntent();
		if (intent != null) 
			return getNotNullString(intent.getStringExtra(key), defaultValue);
		return defaultValue;
	}
	
	public static String getNotNullString(String original, String alt)
	{
		if(original == null)
			return alt;
		else
			return original;
	}
	
	public static boolean trySendText2App(Context context,String appName, String subject, String text)
	{
		try
        {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent.putExtra(Intent.EXTRA_TEXT, text);
			
			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
			for (ResolveInfo activity: activityList)
			{
				if(activity.activityInfo.name.contains(appName))
				{
					ActivityInfo activityInfo = activity.activityInfo;
					ComponentName componentName = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setComponent(componentName);
					context.startActivity(intent);
					return true;
				}
			}
        }
        catch (Exception e)
        {
	        Log.d(TAG, e.getMessage());
        }
		return false;
	}
	
	public static String getTestStr(Activity activity)
	{
		if(activity != null)
			return "get string from javaClass successfully." + activity.getPackageName();
		else
			return "get string from javaClass successfully. activity is null.";
	}

	public static String intentToString(Intent intent)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Intent {\n");
		builder.append("Action : ").append(String.valueOf(intent.getAction())).append("\n");
		builder.append("Categories : ").append(String.valueOf(intent.getCategories())).append("\n");
		builder.append("Data : ").append(String.valueOf(intent.getData())).append("\n");
		builder.append("ExtraData : ").append(CommonTools.bundleToString(intent.getExtras())).append("\n");
		builder.append("}");
	
		return builder.toString();
	}

	public static String bundleToString(Bundle bundle)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Bundle {\n");
		for (String key : bundle.keySet())
		{
			builder.append("[Key : ").append(key).append(", Value : ").append(bundle.get(key)).append("]\n");
		}
		builder.append("}");
		return builder.toString();
	}
}
