package edu.upenn.cis.cis455.webserver;

import java.util.HashMap;
/**
 * This class is a singleton that stores all the sessions in the server
 * @author cis555
 *
 */
public class SessionPool {
	static HashMap<String, MyHttpSession> sessions;
	private static SessionPool instance = null;

	private SessionPool(){
		sessions = new HashMap<String, MyHttpSession>();
	}
	
	/*
	 * Create a new instance if it has never been created
	 */
	public static SessionPool getInstance(){
		if (instance == null){
			instance = new SessionPool();
		}
		return instance;
	}
	
	public static MyHttpSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
	
	public static void addSession(MyHttpSession session){
		sessions.put(session.getId(), session);
	}
}
