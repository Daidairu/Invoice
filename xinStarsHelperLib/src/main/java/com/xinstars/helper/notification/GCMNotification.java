package com.xinstars.helper.notification;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.xinstars.helper.R;
import com.xinstars.helper.R.drawable;
import com.xinstars.helper.R.id;
import com.xinstars.helper.R.layout;
import com.xinstars.helper.utils.Debug;
import com.xinstars.helper.utils.Preconditions;
import com.xinstars.helper.utils.StreamTools;
import com.xinstars.helper.utils.UITools;

public class GCMNotification
{
	public final static String TAG = GCMNotification.class.getSimpleName();
	public final static String INTENT_EXTRA_DATA = "extra_data";
	public final static int LEVEL_TIMEBASE_NORMAL = 0;		// 有時效性的普通訊息等級
	public final static int LEVEL_NORMAL = 1;					// 無時效性的普通訊息等級
	public final static int LEVEL_TIMEBASE_IMPORTANT = 2;		// 有時效性的重要訊息等級
	public final static int LEVEL_IMPORTANT = 3;				// 無時效性的重要訊息等級
	
	private final static int LARGE_ICON_RESOURCE_ID = drawable.com_xinstars_large_icon;
	private final static int SMALL_ICON_RESOURCE_ID = drawable.com_xinstars_icon;

	/** Bundle data key */
	private static final String BUNDLE_SID = "index";
	private static final String BUNDLE_TARGET = "target";
	private static final String BUNDLE_LEVEL = "level";
	private static final String BUNDLE_TITTLE = "title";
	private static final String BUNDLE_MESSAGE = "message";
	private static final String BUNDLE_ICON = "icon";
	private static final String BUNDLE_IMAGE = "image";
	private static final String BUNDLE_ACTION = "click";
	private static final String BUNDLE_LINK = "link";
	private static final String BUNDLE_EXTRA_DATA = "tag";
	
	/** Notification target name*/
	private static final String TARGET_APP_USERS = "APP_USER";
	private static final String TARGET_SINGLE_USER = "SINGLE_USER";

	private final long mSerialID;
	private final String mTarget;
	private final String mTittle;
	private final String mContent;
	private final Bitmap mIcon;
	private final Bitmap mImage;
	private final String mData;
	private final String mLink;
	private final int mLvel;
	private final Context mContext;
	@SuppressWarnings("unused")
    private final String mAction; // 目前尚無功能

	public GCMNotification(Context context, Intent intent) throws Exception
	{
		Preconditions.checkNotNull(context);
		mContext = context;
		
		Bundle bundle = intent.getExtras();
		mTarget = bundle.getString(BUNDLE_TARGET);
		mSerialID = Long.parseLong(bundle.getString(BUNDLE_SID));
		mLvel = Integer.parseInt(bundle.getString(BUNDLE_LEVEL));
		mTittle = getTittle(bundle);
		mContent = getMessage(bundle);
		mIcon = getBitmap(bundle, BUNDLE_ICON);
		mImage = getBitmap(bundle, BUNDLE_IMAGE);
		mAction = bundle.getString(BUNDLE_ACTION);
		mLink = bundle.getString(BUNDLE_LINK);
		mData = bundle.getString(BUNDLE_EXTRA_DATA);
	}
	
	public String getTarget()
	{
		return mTarget;
	}
	
	public long getSID()
	{
		return mSerialID;
	}
	
	private String getTittle(Bundle gcmBundle)
	{
		String tittle = gcmBundle.getString(BUNDLE_TITTLE);
		if (TextUtils.isEmpty(tittle))
		{
			PackageManager manager = mContext.getPackageManager();
			ApplicationInfo appInfo = mContext.getApplicationInfo();
			tittle = (String) manager.getApplicationLabel(appInfo);
		}
		return tittle;
	}

	private String getMessage(Bundle gcmBundle)
	{
		return gcmBundle.getString(BUNDLE_MESSAGE);
	}

	private Bitmap getBitmap(Bundle gcmBundle, String key)
	{
		Bitmap image = null;
		String bitmapInfo = gcmBundle.getString(key);
		if (!TextUtils.isEmpty(bitmapInfo))
		{
			if (bitmapInfo.startsWith("http://") || bitmapInfo.startsWith("https://"))
			{
			    image = getBitmapFromURL(bitmapInfo);
			}
			else
			{
				if (image == null) image = getBitmapFromAsset(bitmapInfo);
				if (image == null) image = getBitmapFromResource(bitmapInfo);
			}
		}

		return image;
	}

	private Bitmap getBitmapFromURL(String url)
	{
		InputStream imageStream = null;
		Bitmap image = null;
		try
		{
			imageStream = new URL(url).openConnection().getInputStream();
			if (imageStream != null) image = BitmapFactory.decodeStream(imageStream);
		}
		catch (Exception e)
		{
			Debug.log(TAG, e.getMessage(), e);
		}
		finally
		{
			StreamTools.close(imageStream);
		}
		return image;
	}

	private Bitmap getBitmapFromAsset(String fileName)
	{
		AssetManager assetManager = null;
		InputStream imageStream = null;
		Bitmap image = null;
		try
		{
			assetManager = mContext.getAssets();
			imageStream = assetManager.open(fileName);

			if (imageStream != null) image = BitmapFactory.decodeStream(imageStream);
		}
		catch (IOException e)
		{
			Debug.logWarning(TAG, e.getMessage(), e);
		}
		finally
		{
			StreamTools.close(imageStream);
		}

		return image;
	}

	private Bitmap getBitmapFromResource(String resourceName)
	{
		int bitmapId = getResourcceId(resourceName);
		if (bitmapId != 0)
			return BitmapFactory.decodeResource(mContext.getResources(), bitmapId);
		else
			return null;
	}

	private int getResourcceId(String resourceName)
	{
		int resourceId = 0;
		Resources resources = mContext.getResources();
		resourceId = resources.getIdentifier(resourceName, "drawable", mContext.getPackageName());

		if (resourceId == 0)
		{
			try
			{
				resourceId = android.R.drawable.class.getField(resourceName).getInt(null);
			}
			catch (Exception e)
			{
				Debug.logWarning(TAG, e.getMessage(), e);
			}
		}

		return resourceId;
	}
	
	public void showIfNeed()
	{
		long lastSID = getLastSID(mTarget);
		if(mSerialID > lastSID)
		{
			show();
			showToast();
			setLastSID(mTarget, mSerialID);
		}
	}
	
	private long getLastSID(String target)
	{
		if(target.equals(TARGET_APP_USERS))
			return NotificationRecord.getAppUserNotificationSID(mContext);
		else if(target.equals(TARGET_SINGLE_USER))
			return NotificationRecord.getSingleUserNotificationSID(mContext);
		return Long.MAX_VALUE;
	}
	
	private void setLastSID(String target, long value)
	{
		if(target.equals(TARGET_APP_USERS))
			NotificationRecord.setAppUserNotificationSID(mContext, value);
		else if(target.equals(TARGET_SINGLE_USER))
			NotificationRecord.setSingleUserNotificationSID(mContext, value);
	}

	public void show()
	{
		try
		{
			Intent notificationIntent = null;
			if (!TextUtils.isEmpty(mLink))
			{
				notificationIntent = new Intent(Intent.ACTION_VIEW);
				notificationIntent.setData(Uri.parse(mLink));
			}
			else
			{
				notificationIntent = getLunchActivityIntent();
				notificationIntent.putExtra(INTENT_EXTRA_DATA, mData);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			}
			PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent,
			        PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
			        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
			        .setLights(Color.BLUE, 1000, 2000)
			        .setSmallIcon(SMALL_ICON_RESOURCE_ID)
			        .setContentIntent(pendingIntent)
			        .setAutoCancel(true);

			// 只有在Android API11以上才能自訂notification的外觀
			if (Build.VERSION.SDK_INT >= 11)
				builder.setContent(getCustomContentView());
			else
				setContent(builder);

			NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			if(mLvel == LEVEL_IMPORTANT || mLvel == LEVEL_TIMEBASE_IMPORTANT)
				manager.notify(nextNotificationIndex(), builder.build());
			else
				manager.notify(0, builder.build());
		}
		catch (Exception e)
		{
			Debug.logWarning(TAG, "[show] : " + e.getMessage(), e);
		}
	}
	
	private Intent getLunchActivityIntent()
	{
		String packageName = mContext.getPackageName();
		PackageManager packageManager = mContext.getPackageManager();
		
		return packageManager.getLaunchIntentForPackage(packageName);
	}
	
	private int nextNotificationIndex()
	{
		int next = NotificationRecord.getImportantNotificationIndex(mContext);
		next = (next == Integer.MAX_VALUE)? 1:next + 1;
		NotificationRecord.setImportantNotificationIndex(mContext, next);
		return next;
	}

	private RemoteViews getCustomContentView()
	{
		RemoteViews contentView = new RemoteViews(mContext.getPackageName(), layout.com_xinstars_notification);

		if (mIcon != null)
			contentView.setImageViewBitmap(id.notification_large_icon, mIcon);
		else
			contentView.setImageViewResource(id.notification_large_icon, LARGE_ICON_RESOURCE_ID);
		
		if(mImage != null)
		{
			contentView.setImageViewBitmap(id.notification_full_image, mImage);
			contentView.setViewVisibility(id.notification_full_image, View.VISIBLE);
			contentView.setViewVisibility(id.notification_tittle, View.GONE);
			contentView.setViewVisibility(id.notification_content, View.GONE);
			contentView.setViewVisibility(id.notification_time, View.GONE);
		}
		else
		{
			contentView.setTextViewText(id.notification_tittle, mTittle);
			contentView.setTextViewText(id.notification_content, mContent);
			contentView.setTextViewText(id.notification_time, getCurrentTime());
		}

		return contentView;
	}
	
	private void setContent(NotificationCompat.Builder builder)
	{
		builder.setContentTitle(mTittle);
		builder.setContentText(mContent);
	}

	private String getCurrentTime()
	{
		long current = System.currentTimeMillis();
		return DateFormat.getTimeFormat(mContext).format(new Date(current));
	}

	public void showToast()
	{
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {

			@Override
			public void run()
			{
				Toast toast = createToast();
				toast.show();
			}
		});
	}

	@SuppressLint("InflateParams")
	protected Toast createToast()
	{
		Toast toast = new Toast(mContext);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.com_xinstars_toast_notification, null);
		TextView tittleView = (TextView) view.findViewById(id.toast_notification_title);
		TextView contentView = (TextView) view.findViewById(id.toast_notification_content);
		ImageView iconView = (ImageView) view.findViewById(id.toast_notification_icon);
		ImageView fullImage = (ImageView) view.findViewById(id.toast_notification_full_image);

		if(mImage != null)
		{
			fullImage.setImageBitmap(mImage);
			fullImage.setVisibility(View.VISIBLE);
			tittleView.setVisibility(View.GONE);
			contentView.setVisibility(View.GONE);
			iconView.setVisibility(View.GONE);
		}
		else
		{
			tittleView.setText(mTittle);
			contentView.setText(mContent);
			
			if(mIcon != null)
				iconView.setImageBitmap(mIcon);
			else
				iconView.setImageResource(LARGE_ICON_RESOURCE_ID);
		}
		
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_LONG);

		int offsetY = -UITools.convertToPixel(mContext, 72);
		toast.setGravity(Gravity.CENTER, 0, offsetY);
		return toast;
	}
	
	public static interface IconResourceID
	{
		public int get();
	}
}
