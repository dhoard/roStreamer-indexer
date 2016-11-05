package com.process.blur.study;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class SampleFilter implements Filter
{
	public BufferedImage filter(BufferedImage image)
	{
		Graphics g = image.getGraphics();
		
		try
		{
			g.setColor(Color.WHITE);
		    g.drawLine(0, 0, image.getWidth(), image.getHeight());
		    g.drawLine(0, image.getHeight(), image.getWidth(), 0);
		}
		finally
		{
			if (g != null)
			{
				g.dispose();
			}
		}
		
		return image;
	}
}
