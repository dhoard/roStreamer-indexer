package com.process.blur.study;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FilterChain implements Filter
{
	private List<Filter> list = new ArrayList<Filter>();
	
	public FilterChain()
	{
		
	}
	
	public FilterChain add(Filter filter)
	{
		list.add(filter);
		
		return this;
	}
	
	public BufferedImage filter(BufferedImage image)
	{
		for (Filter filter : list)
		{
			image = filter.filter(image);
		}
		
		return image;
	}
}
