package com.rostreamer.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ConsoleLogger
{
	public static void setConsoleLevel(Level level)
	{
		Logger rootLogger = Logger.getLogger("");

		java.util.logging.Handler [] handlers = rootLogger.getHandlers();                 
		for(java.util.logging.Handler handler : handlers)
		{
			rootLogger.removeHandler(handler);
		}

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(level);
		consoleHandler.setFormatter(new LogFormatter());         

		rootLogger.addHandler(consoleHandler);      
		rootLogger.setLevel(level);		
	}
	
	static class LogFormatter extends java.util.logging.Formatter
	{
		private final String CRLF = System.getProperty("line.separator");	
		private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		@Override
		public String format(LogRecord record)
		{
			StringBuilder builder = new StringBuilder();
			
			synchronized (simpleDateFormat)
			{
				builder.append(simpleDateFormat.format(new Date(record.getMillis())));
			}
			
			/*
			
			builder.append(" ");
			builder.append(leftPad(String.valueOf(record.getThreadID()), 3, " "));
			builder.append(" ");
			
			*/
						
			int value = record.getLevel().intValue();
			
			/*
			
			if ((value == Level.FINE.intValue()) ||
				(value == Level.FINER.intValue()) ||
				(value == Level.FINEST.intValue()) ||
				(value == Level.ALL.intValue()))
			{
				builder.append("D");
			}
			else
			{
				builder.append(record.getLevel().getName().substring(0, 1));
			}
			
			*/
			
			if ((value == Level.FINE.intValue()) ||
					(value == Level.FINER.intValue()) ||
					(value == Level.FINEST.intValue()) ||
					(value == Level.ALL.intValue()))
			{
				builder.append(" ");
				builder.append(record.getSourceClassName());
				builder.append(" ");
				builder.append(record.getSourceMethodName());
			}
			
			builder.append(" ");
			builder.append(record.getMessage());
			builder.append(CRLF);
			
			if (record.getThrown() != null)
			{
				StringWriter stringWriter = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(stringWriter));
				
				builder.append(stringWriter.toString());
				builder.append(CRLF);
			}
			
			return builder.toString();
		}
		
//		private static String leftPad(String string, int length, String pad)
//		{
//			while (string.length() < length)
//			{
//				string = pad + string;
//			}
//			
//			return string;
//		}
	};
}