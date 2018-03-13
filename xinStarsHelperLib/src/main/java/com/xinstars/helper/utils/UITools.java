package com.xinstars.helper.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class UITools
{
	public static int convertToPixel(Context context, int dp)
	{
		return (int) (dp * getDensity(context));
	}

	public static int convertToDp(Context context, int pixel)
	{
		return (int) (pixel / getDensity(context));
	}

	public static float getDensity(Context context)
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.density;
	}
}
