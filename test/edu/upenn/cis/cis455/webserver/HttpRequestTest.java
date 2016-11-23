package edu.upenn.cis.cis455.webserver;

import junit.framework.TestCase;

public class HttpRequestTest extends TestCase {
	private HttpRequest request;

	public void testGetter() {
		request = new HttpRequest();
		assertEquals(request.getVersion(), "1.1");
		assertTrue(request.isValid());
		assertFalse(request.isHome());
		assertFalse(request.isShutDown());
		assertTrue(request.isStatic());
		assertTrue(request.isValidPath());
		assertFalse(request.isServerError);
	}

}
