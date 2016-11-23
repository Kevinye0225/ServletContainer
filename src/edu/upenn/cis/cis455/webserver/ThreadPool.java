package edu.upenn.cis.cis455.webserver;

import java.util.LinkedList;

public class ThreadPool {
	LinkedList<WorkerThread> threads;
	TaskBlockingQueue queue;
	int size;
	int threadSize;
	
	public ThreadPool(TaskBlockingQueue queue, int size){
		this.threads = new LinkedList<WorkerThread>();
		this.queue = queue;
		this.threadSize = size;
		
		this.initializeThreads();
		
	}
	
	public synchronized void initializeThreads(){
		for (int i = 0; i < threadSize; i++) {
			WorkerThread thread = new WorkerThread(queue);
			synchronized (threads){
				threads.add(thread);
				threads.notifyAll();
			}
			thread.start();
		}
	}
	
	public synchronized void addTask(Runnable r){
		queue.enqueue(r);
	}
	
	public synchronized LinkedList<WorkerThread> getThreads(){
		return this.threads;
	}
	
	public synchronized void terminateThreads(){
		for (WorkerThread thread: this.threads){
			thread.shutDown();
		}
	}
	
}
