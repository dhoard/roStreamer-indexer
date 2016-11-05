package com.process.blur.study;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * @author gloomy fish
 * Vignette - a photograph whose edges shade off gradually
 * 
 */
public class VignetteFilter implements Filter
{
	private int vignetteWidth;
	private int fade;
	private Color vignetteColor;
	
	public VignetteFilter()
	{
		setVignetteWidth(20);
		setFade(35);
		setVignetteColor(Color.BLACK);
	}
	
	public VignetteFilter(int width, int fade, Color vignetteColor)
	{
		setVignetteWidth(width);
		setFade(fade);
		setVignetteColor(vignetteColor);
	}
	
	public void setVignetteWidth(int vignetteWidth)
	{
		this.vignetteWidth = vignetteWidth;
	}
	
	public int getVignetteWidth()
	{
		return vignetteWidth;
	}	

	public void setFade(int fade)
	{
		this.fade = fade;
	}
	
	public int getFade()
	{
		return fade;
	}
	
	public void setVignetteColor(Color vignetteColor)
	{
		this.vignetteColor = vignetteColor;
	}
	
	public Color getVignetteColor()
	{
		return vignetteColor;
	}
	
	public BufferedImage filter(BufferedImage src)
	{
		int width = src.getWidth();
        int height = src.getHeight();

        BufferedImage dest = createCompatibleDestImage( src, null );

        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        getRGB( src, 0, 0, width, height, inPixels );
        int index = 0;
        for(int row=0; row<height; row++) {
        	int ta = 0, tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
            	
                int dX = Math.min(col, width - col);
                int dY = Math.min(row, height - row);
                index = row * width + col;
        		ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                if ((dY <= vignetteWidth) & (dX <= vignetteWidth))
                {
                    double k = 1 - (double)(Math.min(dY, dX) - vignetteWidth + fade) / (double)fade;
                    outPixels[index] = superpositionColor(ta, tr, tg, tb, k);
                    continue;
                }

                if ((dX < (vignetteWidth - fade)) | (dY < (vignetteWidth - fade)))
                {
                	outPixels[index] = (ta << 24) | (vignetteColor.getRed() << 16) | (vignetteColor.getGreen() << 8) | vignetteColor.getBlue();
                }
                else
                {
                    if ((dX < vignetteWidth)&(dY>vignetteWidth))
                    {
                        double k = 1 - (double)(dX - vignetteWidth + fade) / (double)fade;
                        outPixels[index] = superpositionColor(ta, tr, tg, tb, k);
                    }
                    else
                    {
                        if ((dY < vignetteWidth)&(dX > vignetteWidth))
                        {
                            double k = 1 - (double)(dY - vignetteWidth + fade) / (double)fade;
                            outPixels[index] = superpositionColor(ta, tr, tg, tb, k);
                        }
                        else
                        {
                        	outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
                        }
                    }
                }
            }
        }
        
        setRGB( dest, 0, 0, width, height, outPixels );
        return dest;
	}
	
	private int superpositionColor(int ta, int red, int green, int blue, double k) {
		red = (int)(vignetteColor.getRed() * k + red *(1.0-k));
		green = (int)(vignetteColor.getGreen() * k + green *(1.0-k));
		blue = (int)(vignetteColor.getBlue() * k + blue *(1.0-k));
		int color = (ta << 24) | (clamp(red) << 16) | (clamp(green) << 8) | clamp(blue);
		return color;
	}
	
	private int clamp(int value) {
		return value > 255 ? 255 :((value < 0) ? 0 : value);
	}
	
	private BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
        if ( dstCM == null )
            dstCM = src.getColorModel();
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }
	
	/**
	 * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
	 * penalty of BufferedImage.setRGB unmanaging the image.
     * @param image   a BufferedImage object
     * @param x       the left edge of the pixel block
     * @param y       the right edge of the pixel block
     * @param width   the width of the pixel arry
     * @param height  the height of the pixel arry
     * @param pixels  the array of pixels to set
     * @see #getRGB
	 */
	private void setRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
		int type = image.getType();
		if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
			image.getRaster().setDataElements( x, y, width, height, pixels );
		else
			image.setRGB( x, y, width, height, pixels, 0, width );
    }
	
	
	/**
	 * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
	 * penalty of BufferedImage.getRGB unmanaging the image.
     * @param image   a BufferedImage object
     * @param x       the left edge of the pixel block
     * @param y       the right edge of the pixel block
     * @param width   the width of the pixel arry
     * @param height  the height of the pixel arry
     * @param pixels  the array to hold the returned pixels. May be null.
     * @return the pixels
     * @see #setRGB
     */
	private int[] getRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
		int type = image.getType();
		if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
			return (int [])image.getRaster().getDataElements( x, y, width, height, pixels );
		return image.getRGB( x, y, width, height, pixels, 0, width );
    }
}
