package com.xinstars.helper.utils;

import android.support.annotation.Nullable;

public class Preconditions
{
	private final static int CHAR_SIZE_OF_OBJECT = 16;
	
	public static <T> T checkNotNull(T reference)
	{
		if (reference == null) throw new NullPointerException();
		return reference;
	}
	
	public static void checkArgument(boolean expression,@Nullable Object errorMsg)
	{
		if (!expression) throw new IllegalArgumentException(String.valueOf(expression));
	}
	
	public static void checkPositionInexes(int start, int end, int size)
	{
		if(start < 0 || end < start || size < end)
		{
			throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size));
		}
	}
	
	public static String badPositionIndex(int index, int size, String desc)
	{
		if (index < 0)
			return format("%s (%s) must not be negative.", desc, index);
		else if (size < 0)
			throw new IllegalArgumentException("negative: " + size);
		else // index > size
			return format("%s (%s) must not be greater than size (%s)", desc, index, size);
	}
	
	public static String badPositionIndexes(int start, int end, int size)
	{
		if(start < 0 || start > size)
			return badPositionIndex(start, size, "start index");
		else if(end < 0 || end > size)
			return badPositionIndex(end, size, "end index");
		else // end < start
			return format("end index (%s) must not be less than start index(%s)", end, start);
	}
	
	public static String format(String template, @Nullable Object... args)
	{
		template = String.valueOf(template); // null -> "null"

		StringBuilder builder = new StringBuilder(template.length() + args.length * CHAR_SIZE_OF_OBJECT);
		int nextStart = 0;
		int i = 0;
		while (i < args.length)
		{
			int replaceStart = template.indexOf("%s", nextStart);
			if (replaceStart == -1) break;
			builder.append(template, nextStart, replaceStart);
			builder.append(args[i++]);
			nextStart += 2;
		}
		if (nextStart < template.length()) builder.append(template, nextStart, template.length() - 1);

		if (i < args.length)
		{
			builder.append(" [").append(args[i++]);
			while (i < args.length)
			{
				builder.append(", ").append(args[i++]);
			}
			builder.append(']');
		}
		return builder.toString();
	}
}
