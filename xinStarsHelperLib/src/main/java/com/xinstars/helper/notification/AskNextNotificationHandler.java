package com.xinstars.helper.notification;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xinstars.helper.utils.ByteConvert;
import com.xinstars.helper.utils.CommonTools;
import com.xinstars.helper.utils.Debug;
import com.xinstars.helper.utils.JSONTools;

public class AskNextNotificationHandler implements INotificationRequestHandler
{
	public static final String TAG = AskNextNotificationHandler.class.getSimpleName();
	
	private static final int NEED_UPDATA_REGISTER_ID = 10;
	private static final int INITIALIZE_NOTIFICATION_SERIAL_NUMBER = 11;
	private static final int NOTIFICATION = 12;
	
	private Context mContext;
	
	public AskNextNotificationHandler(Context context)
	{
		mContext = context;
	}
			
	@Override
	public void handle(byte[] byteArray)
	{
		try
		{
			int responseType = byteArray[0] & 0xFF;
			switch (responseType)
			{
			case NEED_UPDATA_REGISTER_ID:
				Debug.log(TAG, "[Handle] Response : need update register id.");
				setNeedUpdateRegisterId();
				break;
			case INITIALIZE_NOTIFICATION_SERIAL_NUMBER:
				Debug.log(TAG, "[Handle] Response : initialize notification SID.");
				initNotificationSID(byteArray);
				break;
			case NOTIFICATION:
				Debug.log(TAG, "[Handle] Response : show notification.");
				showNotification(byteArray);
				break;
			default:
				break;
			}

		}
		catch (Exception e)
		{
			Debug.log(TAG, e.getMessage());
		}
	}

	private void initNotificationSID(byte[] byteArray)
    {
	    long notificationSID = ByteConvert.littleEndianByteToLong(byteArray, 1, 8);
	    NotificationRecord.setAppUserNotificationSID(mContext, notificationSID);
	    NotificationRecord.setSingleUserNotificationSID(mContext, notificationSID);
	    Debug.log(TAG, "[initNotificationSID] \nInit SID : " + notificationSID);
    }

	private void setNeedUpdateRegisterId()
    {
	    NotificationRecord.setNeedUpdateRegisterID(mContext, true);
    }
	
	private void showNotification(byte[] byteArray)
	{
		final ArrayList<Bundle> notificationContents = new ArrayList<Bundle>();
		final Charset charset = Charset.forName("UTF-16LE");
		try
		{
			int readOffset = 1;
			while (readOffset < byteArray.length)
			{
				int size = ByteConvert.byteToInt(byteArray, readOffset, 2);
				JSONObject jsonObject = new JSONObject(new String(byteArray, readOffset + 2, size, charset));
				notificationContents.add(JSONTools.toBundle(jsonObject));
				readOffset = readOffset + size + 2;
			}
		}
		catch (Exception e)
		{
			Debug.logWarning(TAG, e.getMessage(), e);
		}
		
		for (Bundle content : notificationContents)
		{
			try
			{
				Debug.log(TAG, "[ShowNotification]\n  content : " + CommonTools.bundleToString(content));
				Intent intent = new Intent().putExtras(content);
				GCMNotification notification = new GCMNotification(mContext, intent);
				notification.showIfNeed();
			}
			catch (Exception e)
			{
				Debug.logWarning(TAG, e.getMessage(), e);
			}
		}		
	}	
	
}