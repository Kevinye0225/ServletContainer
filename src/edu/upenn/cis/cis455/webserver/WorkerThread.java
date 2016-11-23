package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.Logger;
/**
 * This class keeps running the task when it is available
 * @author kevin
 *
 */

public class WorkerThread extends Thread{
	static Logger log = Logger.getLogger(WorkerThread.class);	

	private TaskBlockingQueue taskQueue;
	private String url = "/";
	private boolean shutDown = false;
	
	public WorkerThread(TaskBlockingQueue queue) {
		this.taskQueue = queue;
	}

	public void run() {
		while(!shutDown) {
			Task task = (Task) taskQueue.dequeue();
			task.retrieveUrl();
			setUrl(task.getUrl());
			task.run();
		}
	}
	
	/*
	 * returns the url of current thread
	 */
	public synchronized String getUrl(){
		return this.url;
	}
	
	public synchronized void setUrl(String url){
		while (url == null){
			try {
				wait();
			} catch (InterruptedException e){
//				log.info(e);
				HttpServer.logs.write(e.toString());
			}
		}
		
		notifyAll();
		this.url = url;
	}
	
	public synchronized void shutDown(){
		this.shutDown = true;
		this.interrupt();
	}
	
}
