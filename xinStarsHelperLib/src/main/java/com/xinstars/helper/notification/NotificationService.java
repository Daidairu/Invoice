package com.xinstars.helper.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.xinstars.helper.utils.ByteConvert;
import com.xinstars.helper.utils.Debug;
import com.xinstars.helper.utils.Preconditions;
import com.xinstars.helper.utils.StreamTools;

public class NotificationService extends IntentService
{
	public final static String TAG = NotificationService.class.getSimpleName();
	
	private static WakeLock mWakeLock;
	private static Object mLock = new Object();
	
	public static void runService(Context context, Intent intent)
	{
		Debug.log(TAG, "[RunService] start notification service");
		
		// 請求Wake鎖
		synchronized (mLock)
        {
	        if(mWakeLock == null)
	        {
	        	PowerManager powerMgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        	mWakeLock = powerMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
	        }
	        mWakeLock.acquire();
        }
		
		// 啟動service
		intent.setClass(context, NotificationService.class);
		context.startService(intent);
	}
	
	public NotificationService()
    {
	    super(TAG);
    }

	@Override
    protected void onHandleIntent(Intent arg0)
    {
		Debug.log(TAG, "[onHandleIntent] Notification service started.");
		Context context = getApplicationContext();
		INotificationRequest request;
		INotificationRequestHandler handler;
		
		// 檢查是否需要更新RegisterID
		// 是，請求新的RegisterID
		// 否，請求最新的推播
		if (NotificationRecord.getNeedUpdateRegisterID(context))
		{
			request = new UpdateRegisterRequest(context);
			handler = new UpdateRegisterHandler(context);
			UpdateRegisterRequest updateRequest = (UpdateRegisterRequest) request;
			UpdateRegisterHandler updateHandler = (UpdateRegisterHandler) handler;
			updateHandler.setRequestGCMID(updateRequest.getGCMID());
		}
		else
		{
			request = new AskNextNotificationRequest(context);
			handler = new AskNextNotificationHandler(context);
		}

		// 建立請求並執行發送請求、處理回應
		NotificationRequesTask task = new NotificationRequesTask(request, handler);
		task.start();

		// 釋放Wake鎖
		synchronized (mLock)
        {
            if(mWakeLock != null && mWakeLock.isHeld())
            	mWakeLock.release();
        }
    }
	
	public static class NotificationRequesTask 
	{
		private final static String TAG = NotificationRequesTask.class.getSimpleName();
		private final static String SERVER_IP = "210.64.216.90";
		private final static int SERVER_PORT = 80;
		private final static int CONNECTION_TIMEOUT = 1000;
		private final static int READ_TIMEOUT = 5 * 1000;
		private final static int READ_BUF_SIZE = 256;
		private final static int TRY_READ_TIMES = 15;
		
		private INotificationRequest mRequest;
		private INotificationRequestHandler mHandler;
		private AtomicBoolean mCanceled;
		
		public NotificationRequesTask(INotificationRequest request, INotificationRequestHandler handler)
		{
			mRequest = request;
			mHandler = handler;
			mCanceled = new AtomicBoolean(false);
			Debug.log(TAG, "[Init]\n  request : " + mRequest.getClass().getSimpleName() + "\n  handler : "
			        + mHandler.getClass().getSimpleName());
		}
		
		public void cancel()
		{
			mCanceled.set(true);
			Debug.log(TAG, "[Cancel] cancel the request.");
		}
		
		public boolean isCanceled()
		{
			return mCanceled.get();
		}
		
		public void start()
		{
			SocketAddress address = null;
			Socket socket = null;
			OutputStream output = null;
			InputStream input = null;
			byte[] result = null;
			
			try
            {
				address = new InetSocketAddress(SERVER_IP, SERVER_PORT);
	            socket = new Socket();
	            
	            socket.setTcpNoDelay(true);
	            socket.setSoTimeout(READ_TIMEOUT);
	            socket.connect(address, CONNECTION_TIMEOUT);
	            
	            output = socket.getOutputStream();
	            input = socket.getInputStream();
	            
	            if (socket.isConnected() && !isCanceled())
                {
	            	writeTo(output, mRequest.toByteArray());
	            	result = readFrom(input);
                }
            }
			catch (Exception e) 
			{
				Debug.logWarning(TAG, e.getMessage(), e);
				cancel();
			}
			finally
			{
				StreamTools.close(input, output);
				tryCloseSocket(socket);
			}
		
			if(!isCanceled() && result != null && result.length > 0)
			{
				mHandler.handle(result);
			}
		}
		
		private void writeTo(OutputStream out, byte[] data) throws IOException 
		{
			Preconditions.checkNotNull(data);
			Debug.log(TAG, "[WriteTo]\n  send data : " + Arrays.toString(data));
			
			out.write(ByteConvert.intToByte(data.length, 3));
			out.write(data);
			out.flush();
		}
		
		private byte[] readFrom(InputStream in) throws IOException, InterruptedException
		{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						
			byte[] header = new byte[3];
			in.read(header, 0, 3);
			
			int size = ByteConvert.byteToInt(header, 0, 3);
			byte[] buf = new byte[READ_BUF_SIZE];
			
			int tryTimes = 0;
			int totalRead = 0;
			while(totalRead < size)
			{
				int readSize = in.read(buf);
				tryTimes = readSize > 0? 0:tryTimes + 1;
				if (tryTimes > TRY_READ_TIMES) throw new IOException("Read time too long.");
				
				byteArrayOutputStream.write(buf, 0, readSize);
				totalRead += readSize;
				Thread.sleep(200);
			}

			byte[] result = byteArrayOutputStream.toByteArray();
			Debug.log(TAG, "[ReadFrom]\n  receive data : " + Arrays.toString(result));

			return result;
		}
	
		private void tryCloseSocket(Socket socket)
		{
			try
            {
				if(socket != null)
					socket.close();
            }
            catch (Exception e)
            {
            	Debug.log(TAG, e.getMessage());
            }
		}
	}

	public static class Settings
	{
		private static final String SERVICE_ELAPSED_TIME_SETTING = "com.xinstars.helper.NotificationServiceElapsedTime";
		private static final int DEFAULT_ELAPSED_TIME = 60;
		
		private static int mElapsedTime = 0;
		public static int getServiceElapsedTime(Context context)
		{
			if(mElapsedTime == 0)
			{
				try
                {
					ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					        PackageManager.GET_META_DATA);
					mElapsedTime = info.metaData.getInt(SERVICE_ELAPSED_TIME_SETTING, DEFAULT_ELAPSED_TIME);
                }
                catch (Exception e)
                {
                	mElapsedTime = DEFAULT_ELAPSED_TIME;
                }
			}
			return mElapsedTime;
		}
	}
}
