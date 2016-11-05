package com.rostreamer.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JSONEncoder
{	
	private final static char [] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	public static String toJSON(Object object)
	throws IOException
	{
		return toJSON(object, false);
	}
	
	public static String toJSON(Object object, boolean naturalKeyOrder)
	throws IOException
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		writeJSON(object, stringBuilder, naturalKeyOrder);
		
		return stringBuilder.toString();
	}	
	
	public static void writeJSON(Object object, Appendable appendable)
	throws IOException
	{
		writeJSON(object, appendable, false);
	}
	
	public static void writeJSON(Object object, Appendable appendable, boolean naturalKeyOrder)
	throws IOException
	{
		if (object == null)
		{
			appendable.append("null");
		}
		else if (object instanceof Map)
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) object;
			
			appendable.append("{");
			
			if (map.size() > 0)
			{
				if ((naturalKeyOrder == true) && ((map instanceof TreeMap) == false))
				{
					map = new TreeMap<String, Object>(map);
				}
				
				Iterator<String> iterator = map.keySet().iterator();
				
				while (iterator.hasNext())
				{
					String key = iterator.next();
					Object value = map.get(key);
					
					writeJSON(key, appendable);
					
					appendable.append(":");
					
					writeJSON(value, appendable, naturalKeyOrder);
					
					if (iterator.hasNext())
					{
						appendable.append(",");
					}
				}
			}
			
			appendable.append("}");
		}
		else if (object instanceof List)
		{
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) object;
			
			appendable.append("[");
					
			if (list.size() > 0)
			{
				Iterator<Object> iterator = list.iterator();
				
				while (iterator.hasNext())
				{
					writeJSON(iterator.next(), appendable, naturalKeyOrder);
					
					if (iterator.hasNext())
					{					
						appendable.append(",");
					}
				}
			}
					
			appendable.append("]");
		}
		else if (object instanceof String)
		{
			writeJSON((String) object, appendable);
		}
		else if (object instanceof Number)
		{
			if (object instanceof Double)
			{
				if (((Double) object).isInfinite() || ((Double) object).isNaN())
				{
					appendable.append("null");
				}
				else
				{
					appendable.append(object.toString());
				}
			}
			else if (object instanceof Float)
			{
				if (((Float) object).isInfinite() || ((Float) object).isNaN())
				{
					appendable.append("null");
				}
				else
				{
					appendable.append(object.toString());
				}				
			}			
			else
			{			
				appendable.append(object.toString());
			}
		}
		else if (object instanceof Boolean)
		{
			if (((Boolean) object).booleanValue() == true)
			{
				appendable.append("true");
			}
			else
			{
				appendable.append("false");
			}
		}
		else
		{
			throw new RuntimeException("Invalid object type [" + object.getClass().getName() + "]");
		}
	}
	
	private static void writeJSON(String string, Appendable appendable)
	throws IOException
	{
		appendable.append("\"");
		
		char [] characters = string.toCharArray();
	    
		for (char ch : characters)
	    {
	        switch (ch)
	        {
	        	case '"':
	        	{
	                appendable.append("\\\"");
	                break;
	        	}
	            case '\\':
	            {
	                appendable.append("\\\\");
	                break;
	            }
	            case '/':
	            {
	        		appendable.append("\\/");
	        		break;
	            }		            
	            case '\b':
	            {
	                appendable.append("\\b");
	                break;
	            }
	            case '\f':
	            {
	                appendable.append("\\f");
	                break;
	            }
	            case '\n':
	            {
	                appendable.append("\\n");
	                break;
	            }
	            case '\r':
	            {
	                appendable.append("\\r");
	                break;
	            }
	            case '\t':
	            {
	                appendable.append("\\t");
	                break;
	            }
	            default:
	            {
	            	//
	            	// Be overly aggressive ... but safe
	            	//
	            	// Convert any character outside of the
	            	// basic ASCII printable character set
	            	// to the Unicode escape sequence
	            	//
	            	
	            	if ((ch >= 32) && (ch <= 126))
	            	{
	            		// Append the basic ASCII printable character
	            		appendable.append(ch);
	            	}
	            	else
	            	{
	            		// Append the unicode escape prefix
	            		appendable.append("\\u");
	            		
	            		 // Append first 4-bit grouping
	            		appendable.append(hex[(ch >> 12) & 0xF]);
	            		
	            		// Append second 4-bit grouping
	            		appendable.append(hex[(ch >> 8) & 0xF]);
	            		
	            		// Append third 4-bit grouping
	            		appendable.append(hex[(ch >> 4) & 0xF]);
	            		
	            		// Append fourth 4-but grouping
	            		appendable.append(hex[ch & 0xF]);
	            	}
	            }
	        }
	    }

		appendable.append("\"");
	}	
}
