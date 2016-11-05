package com.rostreamer.indexer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.process.blur.study.Filter;
import com.process.blur.study.VignetteFilter;
import com.rostreamer.json.JSONArray;
import com.rostreamer.json.JSONObject;
import com.rostreamer.logger.ConsoleLogger;
import com.rostreamer.util.HashUtils;
import com.rostreamer.util.ImageUtils;
import com.rostreamer.util.NaturalOrderComparator;

public class Indexer
{
	private final static String CRLF = "\r\n";
	
	private final static int HD_POSTER_WIDTH = 304;
	private final static int HD_POSTER_HEIGHT = 237;
	private final static int HD_POSTER_RADIUS = 40;
	private final static int HD_POSTER_MARGIN = 20;
	private final static Color HD_POSTER_FILL = Color.BLACK;
	
	private final static int SD_POSTER_WIDTH = 224;
	private final static int SD_POSTER_HEIGHT = 158;
	private final static int SD_POSTER_RADIUS = 20;
	private final static int SD_POSTER_MARGIN = 10;
	private final static Color SD_POSTER_FILL = Color.BLACK;
	
	private final static String JSON_FILENAME = "contentMetaData.json";
	private final static String HTML_FILENAME = "index.html";
	
	private final static Logger logger = Logger.getLogger(Indexer.class.getName());
	
	private final static IndexerFileFilter filter = new IndexerFileFilter();
	private final static NaturalOrderComparator sorter = new NaturalOrderComparator();
	private final static Filter vignetteFilter = new VignetteFilter();

	private BufferedImage defaultFileImage = null;
	private BufferedImage defaultFolderImage = null;
	
	private int hdPosterWidth = HD_POSTER_WIDTH;
	private int hdPosterHeight = HD_POSTER_HEIGHT;
	
	private int sdPosterWidth = SD_POSTER_WIDTH;
	private int sdPosterHeight = SD_POSTER_WIDTH;
	
	private boolean recurse = false;	
	private boolean rebuildImages = false;
	
	private long processedFileCount = 0;	
	
	public static void main(String [] args)
	throws IOException
	{
		long t0 = System.currentTimeMillis();
		
		if (args == null)
		{
			return;
		}
		
		if (args.length > 0)
		{
			ConsoleLogger.setConsoleLevel(Level.INFO);
			
			Indexer indexer = new Indexer();
			
			if (args.length == 2)
			{
				indexer.setRebuildImages(true);
			}
			
			indexer.setRecurse(true);
			indexer.setHDPosterSize(HD_POSTER_WIDTH, HD_POSTER_HEIGHT);
			indexer.setSDPosterSize(SD_POSTER_WIDTH, SD_POSTER_HEIGHT);
			indexer.index(new File(args[0]), JSON_FILENAME, HTML_FILENAME);
			
			long t1 = System.currentTimeMillis();
			
			logger.log(Level.INFO, "Total files : " + indexer.getProcessedFileCount());
			logger.log(Level.INFO, "Total time : " + (t1-t0) + " ms");
		}
	}
	
	public Indexer()
	throws IOException
	{
		this.defaultFileImage = ImageIO.read(new BufferedInputStream(Indexer.class.getResourceAsStream("/com/rostreamer/indexer/file.jpg")));
		this.defaultFolderImage = ImageIO.read(new BufferedInputStream(Indexer.class.getResourceAsStream("/com/rostreamer/indexer/folder.jpg")));
	}
	
	public void setHDPosterSize(int width, int height)
	{
		this.hdPosterWidth = width;
		this.hdPosterHeight = height;
	}
	
	public void setSDPosterSize(int width, int height)
	{
		this.sdPosterWidth = width;
		this.sdPosterHeight = height;
	}
	
	public void setRecurse(boolean recurse)
	{
		this.recurse = recurse;
	}
	
	public void setRebuildImages(boolean rebuildImages)
	{
		this.rebuildImages = rebuildImages;
	}
	
	public long getProcessedFileCount()
	{
		return processedFileCount;
	}
	
	public void index(File directory, String jsonFilename, String htmlFilename)
	throws IOException
	{
		if (directory.isDirectory() && (directory.getName().startsWith("_") == false))
		{
			Writer writer = null;
			JSONArray jsonArray = null;
			String json = null;
			
			try
			{	
				jsonArray = index(directory);
				
				json = jsonArray.toJSON();
				json = "[\r\n" + json.substring(1, json.length() - 1) + "\r\n]";
				json = json.replace("},{", "},\r\n{");
				
				writer = new BufferedWriter(new FileWriter(directory.getAbsolutePath() + "/" + jsonFilename));
				writer.write(json);
				writer.flush();
				writer.close();
				
				StringBuilder builder = new StringBuilder();
				
				builder.append("<html>").append(CRLF);
				builder.append("<head>").append(CRLF);
				builder.append("<style>body { background-color: #000000; color: #FFFFFF }</style>").append(CRLF);
				builder.append("<title>").append(directory.getName()).append("</title>").append(CRLF);
				builder.append("</head>").append(CRLF);
				builder.append("<body><center>").append(CRLF);
				
				for (int i=0; i<jsonArray.size(); i++)
				{
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				
					if (jsonObject.get("JSONFeedUrl") != null)
					{					
						builder.append("<a href=\"").append(jsonObject.get("Title")).append("\">");
					}
					else
					{
						builder.append("<a href=\"").append(((JSONArray) jsonObject.get("StreamUrls")).get(0)).append("\">");
					}
					builder.append("<img src=\"").append(jsonObject.get("HDPosterUrl")).append("\"/></a><br/>");
					builder.append(URLDecoder.decode(jsonObject.get("Title").toString(), "UTF-8")).append("<br/><br/>");
					builder.append("\r\n");
				}
				
				builder.append("</center></body>").append(CRLF);
				builder.append("</html>");
				
				writer = new BufferedWriter(new FileWriter(directory.getAbsolutePath() + "/" + htmlFilename));
				writer.write(builder.toString());
				writer.flush();
				writer.close();
			}
			finally
			{
				if (writer != null)
				{
					try
					{
						writer.close();
					}
					catch (Exception e)
					{
						// DO NOTHING
					}
				}
			}
			
			if (recurse)
			{
				File [] files = directory.listFiles();
				
				if (files != null)
				{
					for (File child : files)
					{
						index(child, jsonFilename, htmlFilename);
					}
				}
			}
		}
	}
	
	public JSONArray index(File directory)
	throws IOException
	{
		JSONArray jsonArray = new JSONArray();
		
		if (directory.exists() && directory.isDirectory() && directory.canRead() && (directory.getName().startsWith("_") == false))
		{	
			File listingFile = new File(directory.getAbsolutePath() + "/index.txt");
			
			//logger.log(Level.INFO, "Index = [" + listingFile.getAbsolutePath() + "]");
			
			List<File> files = null;			
			
			if (listingFile.exists() && listingFile.isFile() && listingFile.canRead())
			{
				logger.log(Level.INFO, "Indexing directory (I) " + directory.getCanonicalPath() + " ...");
				
				files = new ArrayList<File>();
				BufferedReader reader = null;
				
				try
				{
					reader = new BufferedReader(new FileReader(listingFile));
					
					while (true)
					{
						String line = reader.readLine();
						
						if (line == null)
						{
							break;
						}
						
						line = line.trim();
						
						if (line.length() > 0)
						{	
							File file = new File(directory.getAbsolutePath() + "/" + line);
							
							if (file.exists() && file.canRead())
							{							
								files.add(file);
							}
							else
							{
								logger.log(Level.WARNING, "Index file entry [" + file.getName() + "] not found");
							}
						}
					}
				}
				finally
				{
					if (reader != null)
					{
						try
						{
							reader.close();
						}
						catch (Exception e)
						{
							// DO NOTHING
						}
					}
				}
			}
			else
			{
				logger.log(Level.INFO, "Indexing directory " + directory.getCanonicalPath() + " ...");
				
				files = Arrays.asList(directory.listFiles(filter));
				
				if (files != null)
				{
					Collections.sort(files, sorter);
				}
			}
			
			if (files != null)
			{	
				for (File child : files)
				{
					processedFileCount++;
										
					String filename = child.getName();
					String filenameWithoutExtension = child.getName();
					
					if (child.isFile())
					{
						logger.log(Level.INFO, " [F] " + filename + " ...");
						
						if (filenameWithoutExtension.lastIndexOf(".") >= 0)
						{
							filenameWithoutExtension = filenameWithoutExtension.substring(0, filenameWithoutExtension.lastIndexOf("."));
						}
					}
					else
					{
						logger.log(Level.INFO, " [D] " + filename + " ...");
					}
					
					String filenameFirstPart = filenameWithoutExtension;
					String filenameSecondPart = "";
					
					if (filenameWithoutExtension.indexOf(" - ") >= 0)
					{
						filenameFirstPart = filenameWithoutExtension.substring(0, filenameWithoutExtension.indexOf(" - ")).trim();;
						filenameSecondPart = filenameWithoutExtension.substring(filenameWithoutExtension.indexOf(" - ") + 3).trim();
					}
					
					String filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
					String filenameWithoutExtensionEncoded = URLEncoder.encode(filenameWithoutExtension, "UTF-8").replace("+", "%20");
					String filenameFirstPartEncoded = URLEncoder.encode(filenameFirstPart, "UTF-8").replace("+", "%20");
					String filenameSecondPartEncoded = URLEncoder.encode(filenameSecondPart, "UTF-8").replace("+", "%20");
					
					String filenamePoster = filenameWithoutExtension + ".jpg";
					String filenameHDPoster = filenameWithoutExtension + ".hd.jpg";
					String filenameSDPoster = filenameWithoutExtension + ".sd.jpg";
					
					String hdPosterUrl = null;
					String sdPosterUrl = null;
					
					File parentFile = child.getParentFile();
					String parentFilePath = parentFile.getAbsolutePath();
					
					File filePoster = new File(parentFilePath + File.separator + filenamePoster);

					logger.log(Level.FINER, "filePoster = [" + filePoster.getCanonicalPath() + "]");
//					System.out.println("filePoster = [" + filePoster.getCanonicalPath() + "]");
					
					if (filePoster.exists() == false) {
						File parentFolderPoster = new File(parentFile.getParentFile().getCanonicalPath() + File.separator + parentFile.getName() + ".jpg");

						if (parentFolderPoster.exists()) {
							filePoster = parentFolderPoster;
						}
					}

					if (filePoster.exists() == false) {
						if (child.isDirectory())
						{						
							ImageIO.write(defaultFolderImage, "jpg", filePoster);
						}
						else
						{
							ImageIO.write(defaultFileImage, "jpg", filePoster);
						}
					}
					
					File fileHDPoster = new File(parentFilePath + File.separator + filenameHDPoster);
					
					if (rebuildImages || (fileHDPoster.exists() == false) || (fileHDPoster.exists() && (filePoster.lastModified() > fileHDPoster.lastModified())))
					{
						BufferedImage image = ImageUtils.readImage(filePoster);						
						image = vignetteFilter.filter(image);
						image = ImageUtils.makeRoundedCorner(image, HD_POSTER_RADIUS);
						image = ImageUtils.resizeImage(image, hdPosterWidth, hdPosterHeight, ImageUtils.ScaleMode.SCALE_TO_FIT, HD_POSTER_MARGIN, HD_POSTER_FILL);						
						ImageUtils.writeImage(image, fileHDPoster);
					}
					
					hdPosterUrl = URLEncoder.encode(fileHDPoster.getName(), "UTF-8").replace("+", "%20");
					
					File fileSDPoster = new File(parentFilePath + File.separator + filenameSDPoster);

					if (rebuildImages || (fileSDPoster.exists() == false) || (fileSDPoster.exists() && (filePoster.lastModified() > fileSDPoster.lastModified())))
					{
						BufferedImage image = ImageUtils.readImage(filePoster);
						image = vignetteFilter.filter(image);
						image = ImageUtils.makeRoundedCorner(image, SD_POSTER_RADIUS);
						image = ImageUtils.resizeImage(image, sdPosterWidth, sdPosterHeight, ImageUtils.ScaleMode.SCALE_TO_FIT, SD_POSTER_MARGIN, SD_POSTER_FILL);						
						ImageUtils.writeImage(image, fileSDPoster); 
					}
					
					sdPosterUrl = URLEncoder.encode(fileSDPoster.getName(), "UTF-8").replace("+", "%20");
					
					JSONObject jsonObject = new JSONObject();
					
					jsonObject.put("Title", filenameWithoutExtensionEncoded);
					jsonObject.put("ShortDescriptionLine1", filenameFirstPartEncoded);
					jsonObject.put("ShortDescriptionLine2", filenameSecondPartEncoded);
					jsonObject.put("HDPosterUrl", hdPosterUrl + "?nocache=" + HashUtils.getSHA1(fileHDPoster));
					jsonObject.put("SDPosterUrl", sdPosterUrl + "?nocache=" + HashUtils.getSHA1(fileSDPoster));
					
					if (child.isDirectory())
					{
						jsonObject.put("JSONFeedUrl", filenameEncoded + "/");
					}
					else
					{
						jsonObject.put("StreamFormat", "mp4");
						
						JSONArray subArray = new JSONArray();
						subArray.add(filenameEncoded);
						subArray.add(filenameEncoded);
						
						jsonObject.put("StreamUrls", subArray);
						
						JSONArray subArray2 = new JSONArray();
						subArray2.add("HD");
						subArray2.add("SD");
						
						jsonObject.put("StreamQualities", subArray2);
						
						JSONArray subArray3 = new JSONArray();
						subArray3.add(0);
						subArray3.add(0);
						
						jsonObject.put("StreamBitrates", subArray3);
					}
					
					jsonArray.add(jsonObject);
				}
			}
		}
		
		return jsonArray;
	}
}