package com.rostreamer.json;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JSONObject extends LinkedHashMap<String, Object>
{
	private static final long serialVersionUID = -1420993895300780550L;
	
	public JSONObject()
	{
		super();
	}
	
	public JSONObject(Map<String, Object> map)
	{
		super(map);
	}
	
	public String toString()
	{
		return toJSON();
	}
	
	public String toJSON()
	{
		return toJSON(false);
	}
	
	public String toJSON(boolean naturalKeyOrder)
	{
		try
		{
			return JSONEncoder.toJSON(this, naturalKeyOrder);
		}
		catch (IOException ioe)
		{
			throw new RuntimeException("Exception encoding JSON", ioe);
		}
	}
	
	public Appendable write(Appendable appendable)
	throws IOException
	{
		return write(appendable, false);
	}
	
	public Appendable write(Appendable appendable, boolean naturalKeyOrder)
	throws IOException
	{
		JSONEncoder.writeJSON(this, appendable, naturalKeyOrder);
		
		return appendable;
	}
}