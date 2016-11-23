package edu.upenn.cis.cis455.webserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import junit.framework.TestCase;

public class ErrorLogTest extends TestCase {

	private ErrorLog log = ErrorLog.getInstance("testerror.txt");
	
	public void testWrite() {
		String test = "test";
		log.write(test);
		log.flush();
		File file = new File("testerror.txt");
		Scanner in = null;
		try {
			in = new Scanner(file);
			String actual = in.nextLine();
			assertEquals(actual, test);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			in.close();
		}
		
	}

}
