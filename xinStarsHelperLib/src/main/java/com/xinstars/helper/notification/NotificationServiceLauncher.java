package com.xinstars.helper.notification;

import com.xinstars.helper.utils.Debug;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

public class NotificationServiceLauncher extends BroadcastReceiver
{
	private static final String TAG = NotificationServiceLauncher.class.getSimpleName();

	/**
	 * 啟動排程
	 * <p>
	 * 啟動後每隔固定時間便喚醒推播服務，時間間隔由AndroidManifest中設定 < meta-data
	 * android:name="com.xinstars.helper.NotificationServiceElapsedTime"
	 * android:value="時間間隔" />。但是當NotificationRecord中的GCM_ID為空值則此次啟動為無效的。
	 * @param context
	 */
	public static void startSchedule(Context context)
	{
		int elapsedTime = NotificationService.Settings.getServiceElapsedTime(context);
		startSchedule(context, elapsedTime);
	}

	/**
	 * 啟動排程
	 * <p>
	 * 指定間隔時間並啟動排程，啟動後每隔固定時間喚醒推播服務。但是當NotificationRecord中的GCM_ID為空值則此次啟動為無效的。
	 * @param context
	 * @param elapsedSec 啟動服務的間隔時間，以秒為單位。
	 */
	public static void startSchedule(Context context, int elapsedSec)
	{
		if (!validate(context)) return;

		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, NotificationServiceLauncher.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), elapsedSec * 1000,
		        pendingIntent);

		Debug.log(TAG, "[StartSchedule] elapsed time : " + elapsedSec);
	}

	/**
	 * 取消排程
	 * @param context
	 */
	public static void cancelSchedule(Context context)
	{
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, NotificationServiceLauncher.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

		if (pendingIntent != null)
		{
			alarmMgr.cancel(pendingIntent);
			pendingIntent.cancel();
		}
	}

	public static boolean validate(Context context)
	{
		// 因為在service向server註冊時需要GCM ID
		// 所以要先檢查GCM ID是否已經被設定
		return !TextUtils.isEmpty(NotificationRecord.getGCMID(context));
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		NotificationService.runService(context, intent);
	}
}
