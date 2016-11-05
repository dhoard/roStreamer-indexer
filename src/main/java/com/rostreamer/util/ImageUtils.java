package com.rostreamer.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;

import com.process.blur.study.Filter;

public class ImageUtils
{
	private static HashSet<String> formatNameSet = null;
	
	static
	{
		formatNameSet = new HashSet<String>();
		formatNameSet.add("jpg");
		formatNameSet.add("bmp");
		formatNameSet.add("jpeg");
		formatNameSet.add("wbmp");
		formatNameSet.add("png");
		formatNameSet.add("gif");
	}
	
	public enum ScaleMode { SCALE_TO_FIT, SCALE_TO_FILL };
	
//	public static void convertImageToNTSC(File sourceFile, File destinationFile)
//	throws IOException
//	{
//		BufferedImage sourceImage = ImageIO.read(sourceFile);	
//        BufferedImage destinationImage = convertImageToNTSC(sourceImage);
//		ImageIO.write(destinationImage, getFormatName(destinationFile), destinationFile);
//	}
	
//	public static BufferedImage convertImageToNTSC(BufferedImage sourceImage)
//	throws IOException
//	{	
//        BufferedImage destinationImage = new BufferedImage(sourceImage.getWidth(), (int) Math.round(sourceImage.getHeight() * 0.8888889), BufferedImage.TYPE_INT_RGB);
//        
//        Graphics2D graphics = destinationImage.createGraphics();
//        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        graphics.drawImage(sourceImage, 0, 0, destinationImage.getWidth(), destinationImage.getHeight(), null);
//        graphics.dispose();		
//		
//		return destinationImage;
//	}

	public static BufferedImage readImage(File file)
	throws IOException
	{
		return ImageIO.read(file);
	}
	
	public static void writeImage(BufferedImage image, File file)
	throws IOException
	{
		ImageIO.write(image, getFormatName(file), file);
	}
	
	public static void resizeImage(File source, File destination, int width, int height, ScaleMode scaleMode, int margin, Color fillColor, Filter filter)
	throws IOException
	{
		BufferedImage sourceImage = ImageIO.read(source);
		BufferedImage destinationImage = null;
		
		if (filter != null)
		{
			destinationImage = filter.filter(sourceImage);
		}
		
		destinationImage = resizeImage(destinationImage, width, height, scaleMode, margin, fillColor);
		
		ImageIO.write(destinationImage, getFormatName(destination), destination);
	}
	
	public static BufferedImage resizeImage(BufferedImage image, int width, int height, ScaleMode scaleMode, int margin, Color fillColor)
	throws IOException
	{
		if (scaleMode == ScaleMode.SCALE_TO_FIT)
		{		
			image = scaleImage(image, width - margin, height - margin);
		}
		
        BufferedImage destinationImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);        
		Graphics2D graphics = destinationImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(fillColor);
		graphics.fillRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
		
		if (scaleMode == ScaleMode.SCALE_TO_FIT)
		{
			graphics.drawImage(image, (int) Math.round(((width - image.getWidth()) / 2)), (int) Math.round(((height - image.getHeight()) / 2)), image.getWidth(), image.getHeight(), null);
		}
		else
		{
			graphics.drawImage(image, (int) Math.round(margin / 2), (int) Math.round(margin / 2), destinationImage.getWidth() - margin, destinationImage.getHeight() - margin, null);
		}
		
		graphics.dispose();	
		
		return destinationImage;
	}
	
	public static BufferedImage rotateImage(BufferedImage image, double angle)
	{		
		int width = image.getWidth();
		int height = image.getHeight();
		
		BufferedImage destinationImage = new BufferedImage(width, height, image.getType());
		Graphics2D graphics = destinationImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);		 
		graphics.rotate(Math.toRadians(angle), width / 2, height / 2);		 
		graphics.drawImage(image, null, 0, 0);		
		graphics.dispose();
		
		return destinationImage;
	}	
	
	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius)
	{
	    int width = image.getWidth();
	    int height = image.getHeight();
	    
	    BufferedImage destinationImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D graphics = destinationImage.createGraphics();

	    // This is what we want, but it only does hard-clipping, i.e. aliasing
	    // g2.setClip(new RoundRectangle2D ...)

	    // so instead fake soft-clipping by first drawing the desired clip shape
	    // in fully opaque white with antialiasing enabled...
	    graphics.setComposite(AlphaComposite.Src);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    
	    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    graphics.setColor(Color.WHITE);
	    graphics.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

	    // ... then compositing the image on top,
	    // using the white shape from above as alpha source
	    graphics.setComposite(AlphaComposite.SrcAtop);
	    graphics.drawImage(image, 0, 0, null);
	    graphics.dispose();
	    
	    return convert(destinationImage, BufferedImage.TYPE_INT_RGB);
	}
	
	public static BufferedImage convert(BufferedImage image, int bufImgType)
	{
	    BufferedImage destinationImage = new BufferedImage(image.getWidth(), image.getHeight(), bufImgType);
	    Graphics2D graphics = destinationImage.createGraphics();
	    graphics.drawImage(image, 0, 0, null);
	    graphics.dispose();
	    
	    return destinationImage;
	}
	
	private static BufferedImage scaleImage(BufferedImage image, int width, int height)
	{
        double sourceImageAspectRatio = (double) width / (double) height;
        int sourceImageWidth = image.getWidth();
        int sourceImageHeight = image.getHeight();
        
        double destinationImageAspectRatio = (double) sourceImageWidth / (double) sourceImageHeight;

        if (sourceImageAspectRatio < destinationImageAspectRatio)
        {
            height = (int) Math.round((width / destinationImageAspectRatio));
        }
        else
        {
            width = (int) Math.round((height * destinationImageAspectRatio));
        }

        BufferedImage destinationImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = destinationImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();

        return destinationImage;
    }
	
	private static String getFormatName(File file)
	{
		String name = file.getName();
		name = name.substring(name.lastIndexOf(".") + 1);
		name = name.toLowerCase();
		
		if (formatNameSet.contains(name))
		{
			return name;
		}
		
		throw new IllegalArgumentException("format type [" + name + "] not supported");
	}
}
