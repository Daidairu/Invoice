package com.xinstars.helper.notification;

import android.content.Context;

import com.xinstars.helper.utils.ByteConvert;
import com.xinstars.helper.utils.Debug;

public class UpdateRegisterHandler implements INotificationRequestHandler
{
	public final static String TAG = UpdateRegisterHandler.class.getSimpleName();
	private final static int SUCCESSFUL_REGISTER = 1;
	private Context mContext;
	private String mRequestGCMID;
	
	public UpdateRegisterHandler(Context context)
	{
		mContext = context;
	}
	
	public void setRequestGCMID(String value)
	{
		mRequestGCMID = value;
	}
	
	@Override
	public void handle(byte[] byteArray)
	{
		try
        {
			int responseType = byteArray[1];
			switch (responseType)
			{
			case SUCCESSFUL_REGISTER:
				Debug.log(TAG, "[Handle] : sucessful register.");
				setRegisterID(byteArray);
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

	private void setRegisterID(byte[] byteArray)
    {
		// 確認收到結果時與送出時GCM_ID是否一致，不一致則放棄這次的registerID
		if (!NotificationRecord.getGCMID(mContext).equals(mRequestGCMID)) return;
		
	    long registerID = ByteConvert.littleEndianByteToLong(byteArray, 2, 8);
	    
	    NotificationRecord.setRegisterID(mContext, registerID);
	    NotificationRecord.setNeedUpdateRegisterID(mContext, false);
	    Debug.log(TAG, "[SetRigisterID]\n  register id : " + registerID);
    }
}