package com.xinstars.helper.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class StreamTools
{
	private static final int BUF_SIZE = 1024 * 8;
	
	public static long copy(InputStream in, OutputStream out) throws IOException
	{
		Preconditions.checkNotNull(in);
		Preconditions.checkNotNull(out);
		
		int size = 0;
		long total = 0;
		byte[] buf = new byte[BUF_SIZE];
		while ((size = in.read(buf)) != -1)
		{
			out.write(buf, 0, size);
			total += size;
		}
		
		return total;
	}
	
	public static byte[] toByteArray(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}

	public static HashMap<Closeable, Exception> close(Closeable... closeables)
	{
		HashMap<Closeable, Exception> excptions = null;
	
		for (Closeable closeable : closeables)
		{
			try
			{
				closeable.close();
			}
			catch (Exception e)
			{
				if (excptions == null) excptions = new HashMap<Closeable, Exception>();
				excptions.put(closeable, e);
			}
		}
		return excptions;
	}
}
