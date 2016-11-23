package edu.upenn.cis.cis455.webserver;

import junit.framework.TestCase;

public class SessionPoolTest extends TestCase {

	private SessionPool sessionPool = SessionPool.getInstance();
	
	public void testSingleTon() {
		SessionPool newPool = SessionPool.getInstance();
		assertEquals(newPool, sessionPool);
	}
	
	public void testMap() {
		MyHttpSession session = new MyHttpSession();
		SessionPool.addSession(session);
		
		assertEquals(session, SessionPool.getSession(session.getId()));
	}

}
