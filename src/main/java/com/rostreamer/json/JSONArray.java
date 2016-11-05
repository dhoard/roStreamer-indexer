package com.rostreamer.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONArray extends ArrayList<Object>
{
	private static final long serialVersionUID = -954894222748905214L;

	public JSONArray()
	{
		super();
	}
	
	public JSONArray(List<Object> list)
	{
		super(list);
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
