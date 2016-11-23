package edu.upenn.cis.cis455.webserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ErrorLog {
	File logFile;
	PrintWriter out;
	static ErrorLog logInstance;
	
	private ErrorLog(String filename){
		logFile = new File(filename);
		try {
			out = new PrintWriter(logFile, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ErrorLog getInstance(String filename){
		if (logInstance == null){
			logInstance = new ErrorLog(filename);
		}
		return logInstance;
	}
	
	public void write(String log){
		out.write(log);
		out.write("\n");
	}
	
	public void flush(){
		out.flush();
	}
}
