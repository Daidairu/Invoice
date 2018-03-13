package com.xinstars.helper.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class NotificationServiceScheduler extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if(!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_BOOT_COMPLETED))
		{
			NotificationServiceLauncher.startSchedule(context);
		}
	}
}
