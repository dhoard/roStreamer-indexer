package com.rostreamer.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils
{	
	public static String getSHA1(int i)
	throws IOException
	{
		return getHash("SHA-1", String.valueOf(i));
	}
	
	public static String getSHA1(String string)
	throws IOException
	{
		return getHash("SHA-1", string);
	}
	
	public static String getSHA1(File file)
	throws IOException
	{
		return getHash("SHA-1", file);
	}	
	
	public static String getHash(String algorithm, int i)
	throws IOException
	{
		return getHash(algorithm, String.valueOf(i));
	}
	
	public static String getHash(String algorithm, String string)
	throws IOException
	{
		MessageDigest messageDigest = null;
		
		try
		{
			messageDigest = MessageDigest.getInstance(algorithm);
		}
		catch (NoSuchAlgorithmException nsae)
		{
			throw new IOException(nsae);
		}
		
		messageDigest.update(string.getBytes("UTF-8"));
	    byte [] messageDigestBytes = messageDigest.digest();
		StringBuilder stringBuilder = new StringBuilder();   
	    
	    for (int i=0; i<messageDigestBytes.length; i++)
	    {
	    	stringBuilder.append(Integer.toString((messageDigestBytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	 
	    return stringBuilder.toString().toLowerCase();		
	}
	
	private static String getHash(String algorithm, File file)
	throws IOException
	{
		MessageDigest messageDigest = null;
		
		try
		{
			messageDigest = MessageDigest.getInstance(algorithm);
		}
		catch (NoSuchAlgorithmException nsae)
		{
			throw new IOException(nsae);
		}		
	    
		InputStream inputStream = null;
	    
	    byte [] dataBytes = new byte[1024];
	    int bytesRead = 0;
	    StringBuilder stringBuilder = new StringBuilder();
	    
	    try
	    {
	    	inputStream = new BufferedInputStream(new FileInputStream(file));
	     
		    while ((bytesRead = inputStream.read(dataBytes)) != -1)
		    {
		    	messageDigest.update(dataBytes, 0, bytesRead);
		    }
		 
		    byte [] messageDigestBytes = messageDigest.digest();
		    
		    for (int i=0; i<messageDigestBytes.length; i++)
		    {
		    	stringBuilder.append(Integer.toString((messageDigestBytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
	 
		    return stringBuilder.toString().toLowerCase();
	    }
	    finally
	    {
	    	if (inputStream != null)
	    	{
	    		try
	    		{
	    			inputStream.close();
	    		}
	    		catch (Throwable throwable)
	    		{
	    			// DO NOTHING
	    		}
	    		
	    		inputStream = null;
	    	}
	    }
	}
}
