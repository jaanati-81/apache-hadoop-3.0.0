package org.apache.hadoop.util;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OurECLogger {
	
	private static OurECLogger instance;
	private static final String logFile = "/home/hduser/OurECLog.log";
	
	
	// added to avoid all the messages from hadoop internal classes
	// private final boolean enabled = false;
	private String className;
	private FileWriter fileWriter;
	
	public OurECLogger() {
		System.out.println("starting OurECLogger, logging at " + logFile);
			
	}

	
	@SuppressWarnings("rawtypes")
	private OurECLogger(Class c) {
		className = c.getName();
	}
	
	private OurECLogger(String name) {
		className = name;
	}
	
	private String getClassName(){
		return this.className;
	}
	//double check locking implementation (faster with threads...)
	public static synchronized OurECLogger getInstance() {
				
		if (instance == null) {
			synchronized (OurECLogger.class) {
				if (instance == null)
					instance = new OurECLogger();
			}
		}
		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	public static synchronized OurECLogger getLogger(Class c) {
	
		if (instance == null) {
			synchronized (OurECLogger.class) {
			//	if (instance == null){
					instance = new OurECLogger(c);
					instance.className = c.getName();
				//}
			}
		}
		else
			instance.className = c.getName();
		
		return instance;
	}
	
	public static synchronized OurECLogger getLogger(String name) {
		if (instance == null) {
			synchronized (OurECLogger.class) {
				if (instance == null)
					instance = new OurECLogger(name);
			}
		} 
		
		return instance;
	}
	
	/**
	 * Write any message into a file log declared as source
	 * @param msg - input string with the message to be logged
	 */
	public synchronized void write(String msg) {
		
		//if (!enabled)
			//return;
		//check if the file writer is initialized
		try {
			if (fileWriter == null)
				//the true in the end declared the file in appending mode
				fileWriter = new FileWriter(logFile, true);
			//after open, write the message
			StringBuilder builder = new StringBuilder();
			SimpleDateFormat ft = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
			//add date time info
			//builder.append("[" + format.format(date) + "] (" + System.nanoTime() + ") ");
			builder.append("[" + ft.format(new Date()) + "] ");
			className = this.getClassName();
			//if the class name exists then add it
			if (className != null && className.length() > 0)
				builder.append("{" + className + "} ");
			//add the message
			builder.append(msg);
			//Console output
			System.out.println(builder.toString());
			//add the newline character
			builder.append("\n");
			//write it in the file
			fileWriter.write(builder.toString());
		} catch (IOException ioe) {
			System.err.println("Aborting writing... file couldn't be opened!!");
			
		} finally {
			close();
		}
	}
	
	/**
	 * Method executed at the end of the process tested
	 */
	private void close() {
		try {
			if (fileWriter != null)
				fileWriter.close();
			//forcing garbage collection for sanity
			fileWriter = null;
		} catch (IOException ioe) {
			System.err.println("Aborting writing... file couldn't be closed!!");
			//writeFile("E");
		}
	}

}

