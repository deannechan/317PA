package webserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;


public class HTTPServer implements Runnable{ 
	
	File WEB_ROOT = new File("../files");
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";
	static final String PERM_ERROR = "403.html";
	static final String OTHER_ERROR = "400.html";
	static final String METHOD_NOT_SUPPORTED = "501.html";
	private Socket connection;
	
	public HTTPServer(String root, Socket conn) throws IOException {
		WEB_ROOT = new File(root);
		connection = conn;
	}
	
	@Override
	public void run() {
		try {
			handleRequest();
			System.out.println(connection);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void handleRequest() throws Exception {
		
		BufferedReader in = null; 
		PrintWriter out = null; 
		BufferedOutputStream dataOut = null;
		String fileRequested = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			out = new PrintWriter(connection.getOutputStream());
			dataOut = new BufferedOutputStream(connection.getOutputStream());
			
			String input = in.readLine();
			System.out.println("\n" + input);
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase();
			
			// not implemented method
			if (!method.equals("GET")  &&  !method.equals("HEAD")) {
				error501(out, dataOut, fileRequested);
			} else {
				if (method.equals("GET") && parse.hasMoreElements())
		        	fileRequested = parse.nextToken().toLowerCase();
				else {
					// bad request
					error400(out, dataOut, fileRequested);
				}
	
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_FILE;
				}

				// Remove leading / from filename
				while (fileRequested.indexOf("/")==0)
					fileRequested=fileRequested.substring(1);
				
				File file = new File(WEB_ROOT, fileRequested);
				if(file.exists() && !(file.canRead())) {
					error403(out, dataOut, fileRequested);
					return;
				}

				int fileLength = (int) file.length();
				String content = getContentType(fileRequested);

				if (method.equals("GET")) {
					byte[] fileData = readFileData(file, fileLength);
					
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Date: " + new Date());
					out.println("Content-type: " + content);
					out.println("Content-length: " + fileLength);
					out.println();
					out.flush();
					
					dataOut.write(fileData, 0, fileLength);
					dataOut.flush();
				}	
			}	
		} catch (FileNotFoundException fnfe) {
			try {
				error404(out, dataOut, fileRequested);	
			} catch (IOException ioe) {
				System.err.println("Error with file not found exception : " + ioe.getMessage());
			}		
		} catch (IOException ioe) {
			System.err.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connection.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
		}	
	}
	
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}		
		return fileData;
	}
	
	// return supported MIME Types
	private String getContentType(String fileRequested) {
		String content = "text/plain";
		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
			content =  "text/html";
		else if (fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg"))
	        content="image/jpeg";
	    else if (fileRequested.endsWith(".gif"))
	        content="image/gif";
	    else if (fileRequested.endsWith(".class"))
	        content="application/octet-stream";
		return content;
	}
	
	// Not Found error
	private void error404(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, FILE_NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
	}

	// Forbidden error
	private void error403(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, PERM_ERROR);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 403 Forbidden");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
	}

	// Bad request error
	private void error400(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, OTHER_ERROR);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 400 Bad Request");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
	}
	
	// Not Implemented error
	private void error501(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 501 Not Implemented");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
	}
}
