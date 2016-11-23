package edu.upenn.cis.cis455.webserver;

import java.util.LinkedList;

import org.apache.log4j.Logger;

public class TaskBlockingQueue {

	/**
	 * Logger for this particular class
	 */
	static Logger log = Logger.getLogger(TaskBlockingQueue.class);

	LinkedList<Runnable> queue;
	int size;

	public TaskBlockingQueue(int size) {
		this.queue = new LinkedList<Runnable>();
		this.size = size;
	}

	public synchronized void enqueue(Runnable r) {
		while (queue.size() == size) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				log.info(e);
				HttpServer.logs.write(e.toString());
				return;
			}
		}

		notifyAll();
		queue.add(r);
	}

	public synchronized Runnable dequeue() {
		while (queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				log.info(e);
				HttpServer.logs.write(e.toString());
				continue;
			}
		}

		notifyAll();

		Runnable r = queue.poll();
		return r;
	}
}
