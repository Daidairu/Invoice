package com.xinstars.helper.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class Debug
{
	//[Boxer Add] 加入強制顯示 Log 的方式，SDK 應該會有必須顯示的 Log、LogWarning、LogError 資訊
	private static boolean ENABLE_LOG = false;
	
	public static void enableLog(boolean enabled)
	{
		ENABLE_LOG = enabled;
	}

	public static boolean isDebuggable() {
		return ENABLE_LOG;
	}

	public static void log(String tag, String message)
	{
		log(false, tag, message);
    }

	public static void log(boolean forceLog, String tag, String message)
	{
		if (ENABLE_LOG || forceLog ) Log.d(tag, message);
	}

	public static void log(String tag, String message, Throwable excption)
	{
		log(false, tag, message, excption);
	}
	
	public static void log(boolean forceLog, String tag, String message, Throwable excption)
	{
		if (ENABLE_LOG || forceLog ) Log.d(tag, message, excption);
	}

	public static void logWarning(String tag, String message, Throwable exception)
	{
		logWarning(false, tag, message, exception);
	}
	
	public static void logWarning(boolean forceLog, String tag, String message, Throwable exception)
	{
		if (ENABLE_LOG || forceLog ) Log.w(tag, message, exception);
	}

	public static void logError(String tag, String message, Throwable exception)
	{
		logError(false, tag, message, exception);
	}
	
	public static void logError(boolean forceLog, String tag, String message, Throwable exception)
	{
		if (ENABLE_LOG || forceLog ) Log.e(tag, message, exception);
	}
	
	public static String getCurrentTime()
	{
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		return timeStamp;
	}
	
	public static void logFile(Context context, String fileName, Object message)
	{
		if(!ENABLE_LOG) return;
		
		String path = Environment.getExternalStorageDirectory().getPath() + "/.XinGame/data/" + fileName + ".txt";
		File file = new File(path);
		
		BufferedWriter writer = null;
		try
		{
			if (!file.exists()) file.createNewFile();
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.append(getCurrentTime()).append(" ").append(String.valueOf(message));
			writer.newLine();
		}
		catch (Exception e)
		{
			log("Debug", e.getMessage());
		}
		finally
		{
			if (writer != null) StreamTools.close(writer);
		}
	}

}
