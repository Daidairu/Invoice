package com.xinstars.helper.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


public class ByteConvert
{
	private final static Charset DEFAULT_DECODE = Charset.forName("UTF-16LE");
	
	/**
	 * Convert integer to byte array, if expected byte array size < 4 then will
	 * discard those bytes that greater the size.
	 * 
	 * <p> example
	 * <br> intToByte(257, 1) will return byte[]{1}.
	 * <br> intToByte(257, 2) will return byte[]{1,1}.
	 * 
	 * @param value  the integer will be converted.
	 * @param size  expected byte array size.
	 * @return converted byte array
	 * @throws if size < 0 or size > 5 will throw illegal argument excption.
	 */
	public static byte[] intToByte(int value, int size)
	{
		Preconditions.checkArgument(size > 0 && size < 5, "size must be less than 5 and greater than 0.");
		
		byte[] result = new byte[size];
		result[0] = (byte) (value >> ((size -1) * 8) & 0xFF);
		if (size > 1) result[1] = (byte) (value >> ((size -2) * 8) & 0xFF);
		if (size > 2) result[2] = (byte) (value >> ((size -3) * 8) & 0xFF);
		if (size > 3) result[3] = (byte) (value >> ((size -4) * 8) & 0xFF);
		return result;
	}
	
	public static byte[] littleEndianLongToByte(long value)
	{
		return littleEndianLongToByte(value, 8);
	}
	
	public static byte[] littleEndianLongToByte(long value, int size)
	{
		Preconditions.checkArgument(size > 0 && size < 9, "size must be less than 9 and greater than 0.");

		byte[] result = new byte[size];
		result[0] = (byte) (value & 0xFFL);
		if (size > 1) result[1] = (byte) ((value >> 8) & 0xFFL);
		if (size > 2) result[2] = (byte) ((value >> 16) & 0xFFL);
		if (size > 3) result[3] = (byte) ((value >> 24) & 0xFFL);
		if (size > 4) result[4] = (byte) ((value >> 32) & 0xFFL);
		if (size > 5) result[5] = (byte) ((value >> 40) & 0xFFL);
		if (size > 6) result[6] = (byte) ((value >> 48) & 0xFFL);
		if (size > 7) result[7] = (byte) ((value >> 56) & 0xFFL);
		
		return result;
	}
	
	public static int byteToInt(byte[] byteArray, int offset, int len)
	{
		Preconditions.checkArgument(byteArray != null && byteArray.length > 0, "byteArray must not be null and length greater than 0.");
		Preconditions.checkArgument(len > 0 && len < 5, "len must less than 5 and greater than 0.");
		Preconditions.checkPositionInexes(offset, offset + len - 1, byteArray.length - 1);

		int result = byteArray[offset] & 0xFF;
		if (len > 1) result = (result << 8) | (byteArray[offset + 1] & 0xFF);
		if (len > 2) result = (result << 8) | (byteArray[offset + 2] & 0xFF);
		if (len > 3) result = (result << 8) | (byteArray[offset + 3] & 0xFF);
		return result;
	}
	
	public static long littleEndianByteToLong(byte[] byteArray, int offset, int len)
	{
		Preconditions.checkArgument(byteArray != null && byteArray.length > 0, "byteArray must not be null and length greater than 0.");
		Preconditions.checkArgument(len > 0 && len < 9, "len must less than 9 and greater than 0.");
		Preconditions.checkPositionInexes(offset, offset + len - 1, byteArray.length - 1);
		
		long result = byteArray[offset + len - 1] & 0xFF;
		if (len > 1) result = (result << 8) | (byteArray[offset + len - 2] & 0xFFL);
		if (len > 2) result = (result << 8) | (byteArray[offset + len - 3] & 0xFFL);
		if (len > 3) result = (result << 8) | (byteArray[offset + len - 4] & 0xFFL);
		if (len > 4) result = (result << 8) | (byteArray[offset + len - 5] & 0xFFL);
		if (len > 5) result = (result << 8) | (byteArray[offset + len - 6] & 0xFFL);
		if (len > 6) result = (result << 8) | (byteArray[offset + len - 7] & 0xFFL);
		if (len > 7) result = (result << 8) | (byteArray[offset + len - 8] & 0xFFL);
		return result;
	}
	
	public static long littleEndianByteToLong(InputStream input) throws IOException
	{
		Preconditions.checkNotNull(input);
		Preconditions.checkArgument(input.available() > 7, "input stream avalible is not enough.");
		
		byte[] buffer = new byte[8];
		input.read(buffer, 0, 8);
		return littleEndianByteToLong(buffer, 0, 8);
	}
	
	public static byte[] stringToBytes(String value, boolean containsLength)
	{
		return stringToByte(value, containsLength, DEFAULT_DECODE);
	}
	
	public static byte[] stringToByte(String value, boolean sizePrefix, Charset charset)
	{
		Preconditions.checkNotNull(value);
		Preconditions.checkNotNull(charset);
		
		if(sizePrefix)
		{
			byte[] partOfString = value.getBytes(charset);
			byte[] partOfSize = intToByte(partOfString.length, 2);
			return merge(partOfSize, partOfString);
		}
		else
		{
			return value.getBytes(charset);
		}
	}
	
	public static byte[] merge(byte[]... byteArrays)
	{
		Preconditions.checkNotNull(byteArrays);
		
		int newSize = 0;
		for (int i = 0; i < byteArrays.length; i++)
		{
			if (byteArrays[i] != null) 
				newSize += byteArrays[i].length;
		}

		int startIndex = 0;
		byte[] result = new byte[newSize];
		for (int j = 0; j < byteArrays.length; j++)
		{
			byte[] bs = byteArrays[j];
			if (bs != null)
			{
				System.arraycopy(bs, 0, result, startIndex, bs.length);
				startIndex += bs.length;
			}
		}

		return result;
	}
}
