package com.xinstars.helper.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;

import com.xinstars.helper.utils.ByteConvert;
import com.xinstars.helper.utils.Debug;
import com.xinstars.helper.utils.StreamTools;

public class UpdateRegisterRequest implements INotificationRequest
{
	public final static String TAG = UpdateRegisterRequest.class.getSimpleName();
	private Context mContext;
	private String mGCMID;
	
	public UpdateRegisterRequest(Context context)
	{
		mContext = context;
		mGCMID = NotificationRecord.getGCMID(context);
	}
	
	public String getGCMID()
	{
		return mGCMID;
	}
	
	@Override
	public byte[] toByteArray()
	{
		String packageName = mContext.getPackageName();
		String gcmID = mGCMID;
		long appUserNoticationSID = NotificationRecord.getAppUserNotificationSID(mContext);
		long singleUserNotificationSID = NotificationRecord.getSingleUserNotificationSID(mContext);

		Debug.log(TAG, "Request content \nApp name : " + packageName + "\nGcmID : " + gcmID
		        + "\nApp user notification SID : " + appUserNoticationSID + "\nSingle user notification SID : "
		        + singleUserNotificationSID);
		
		ByteArrayOutputStream buffer = null;
		byte[] result = null;

		try
		{
			// 1byte 協定編號10
			// 2byte + n Byte app名稱(含字串byte長度)
			// 2byte + n Byte app名稱(含字串byte長度)
			// 8byte app notification serial number
			// 8byte gcm notification serial number
			buffer = new ByteArrayOutputStream();
			buffer.write(10);
			buffer.write(ByteConvert.stringToBytes(packageName, true));
			buffer.write(ByteConvert.stringToBytes(gcmID, true));
			buffer.write(ByteConvert.littleEndianLongToByte(appUserNoticationSID));
			buffer.write(ByteConvert.littleEndianLongToByte(singleUserNotificationSID));
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