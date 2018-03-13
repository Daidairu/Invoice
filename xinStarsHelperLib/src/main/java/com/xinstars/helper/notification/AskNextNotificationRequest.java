package com.xinstars.helper.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;

import com.xinstars.helper.utils.ByteConvert;
import com.xinstars.helper.utils.Debug;
import com.xinstars.helper.utils.StreamTools;

public class AskNextNotificationRequest implements INotificationRequest
{
	public final static String TAG = AskNextNotificationRequest.class.getSimpleName();
	private Context mContext;
	
	public AskNextNotificationRequest(Context context)
	{
		mContext = context;
	}
	
	@Override
	public byte[] toByteArray()
	{
		// 1byte  協定編號1
		// 8byte  Server給的註冊ID
		// 8bytes 通知流水編號(FOR APP)
		// 8byte  通知流水編號(FOR GCM)
		
		long registerID = NotificationRecord.getRegisterID(mContext);
		long singleUserNotificationSID = NotificationRecord.getSingleUserNotificationSID(mContext);
		long appUserNotificationSID = NotificationRecord.getAppUserNotificationSID(mContext);

		Debug.log(TAG, "[ToByteArray]\n  Register id : " + registerID + "\n  Gcm Notification SID : "
		        + singleUserNotificationSID + "\n  App Notification SID : " + appUserNotificationSID);
		
		ByteArrayOutputStream buffer = null;
		byte[] result = null;

		try
		{
			buffer = new ByteArrayOutputStream();
			buffer.write(1);
			buffer.write(ByteConvert.littleEndianLongToByte(registerID));				// server端名稱: ikey
			buffer.write(ByteConvert.littleEndianLongToByte(appUserNotificationSID)); 	// server端名稱: appIndex
			buffer.write(ByteConvert.littleEndianLongToByte(singleUserNotificationSID));// server端名稱: gcmIndex
			result = buffer.toByteArray();
		}
		catch (IOException e)
		{
			result = new byte[0];
			Debug.log(TAG, e.getMessage());
		}
		finally
		{
			if (buffer != null) StreamTools.close(buffer);
		}

		return result;
	}
}