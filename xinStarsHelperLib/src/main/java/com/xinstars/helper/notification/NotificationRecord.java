package com.xinstars.helper.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class NotificationRecord
{
	private static final String PREFERENCE_NAME = NotificationRecord.class.getName();

	private static final long DEFAULT_LONG = 0L;
	private static final String DEFAULT_STRING = "";

	private static final String GCM_ID = "gcm_id";
	private static final String START_ACTIVITY = "start_activity";
	private static final String REGISTER_ID = "register_id";
	private static final String SINGLE_USER_NOTIFICATION_SID = "for_single_user_notification_serial_number";
	private static final String APP_USER_NOTIFICATION_SID = "for_app_user_notification_serial_number";
	private static final String NEED_UPDATE_REGISTER_ID = "need_update_register_id";
	private static final String IMPORTANT_NOTIFICATION_INDEX = "import_notification_index";

	public static String getPackageName(Context context)
	{
		return context.getPackageName();
	}

	public static String getGCMID(Context context)
	{
		SharedPreferences preference = getPreference(context);
		return preference.getString(GCM_ID, DEFAULT_STRING);
	}

	/**
	 * 設定GCM_ID，如果給的值為空值或者空字串則取消推播服務的排程，
	 * 如果給的值與之前的值不一樣則推播服務會重新向伺服器註冊。
	 * register id to be true.
	 * @param context
	 * @param value
	 */
	public static void setGCMID(Context context, String value)
	{
		if (TextUtils.isEmpty(value))
			NotificationServiceLauncher.cancelSchedule(context);
		else if (!getGCMID(context).equals(value))
		    NotificationRecord.setNeedUpdateRegisterID(context, true);

		SharedPreferences preferences = getPreference(context);
		Editor editor = preferences.edit();
		editor.putString(GCM_ID, value);
		editor.commit();
	}

	public static String getStartActivity(Context context)
	{
		SharedPreferences preference = getPreference(context);
		return preference.getString(START_ACTIVITY, DEFAULT_STRING);
	}

	public static void setStartActivity(Context context, String value)
	{
		SharedPreferences preferences = getPreference(context);
		Editor editor = preferences.edit();
		editor.putString(START_ACTIVITY, value);
		editor.commit();
	}

	public static long getRegisterID(Context context)
	{
		SharedPreferences preferences = getPreference(context);
		return preferences.getLong(REGISTER_ID, DEFAULT_LONG);
	}

	public static void setRegisterID(Context context, long value)
	{
		SharedPreferences preferences = getPreference(context);
		Editor editor = preferences.edit();
		editor.putLong(REGISTER_ID, value);
		editor.commit();
	}

	public static long getAppUserNotificationSID(Context context)
	{
		SharedPreferences preferences = getPreference(context);
		return preferences.getLong(APP_USER_NOTIFICATION_SID, DEFAULT_LONG);
	}

	public static void setAppUserNotificationSID(Context context, long value)
	{
		SharedPreferences preferences = getPreference(context);
		Editor editor = preferences.edit();
		editor.putLong(APP_USER_NOTIFICATION_SID, value);
		editor.commit();
	}

	public static long getSingleUserNotificationSID(Context context)
	{
		SharedPreferences preferences = getPreference(context);
		return preferences.getLong(SINGLE_USER_NOTIFICATION_SID, DEFAULT_LONG);
	}

	public static void setSingleUserNotificationSID(Context context, long value)
	{
		SharedPreferences preferences = getPreference(context);
		Editor editor = preferences.edit();
		editor.putLong(SINGLE_USER_NOTIFICATION_SID, value);
		editor.commit();
	}

	public static int getImportantNotificationIndex(Context context)
	{
		SharedPreferences preference = getPreference(context);
		return preference.getInt(IMPORTANT_NOTIFICATION_INDEX, 1);
	}

	public static void setImportantNotificationIndex(Context context, int value)
	{
		SharedPreferences preferences = getPreference(context);
		Editor editor = preferences.edit();
		editor.putInt(IMPORTANT_NOTIFICATION_INDEX, value);
		editor.commit();
	}

	private static SharedPreferences getPreference(Context context)
	{
		return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public static boolean getNeedUpdateRegisterID(Context context)
	{
		SharedPreferences preferences = getPreference(context);
		return preferences.getBoolean(NEED_UPDATE_REGISTER_ID, true);
	}

	public static void setNeedUpdateRegisterID(Context context, boolean value)
	{
		SharedPreferences preferences = getPreference(context);
		Editor editor = preferences.edit();
		editor.putBoolean(NEED_UPDATE_REGISTER_ID, value);
		editor.commit();
	}
}
