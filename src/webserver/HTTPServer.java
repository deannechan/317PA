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
	
	File WEB_ROOT = new File("./files");
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";
	static final String PERM_ERROR = "403.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";
	static final String OTHER_ERROR = "400.html";

	// Client connection via Socket Class
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
			// we read characters from the client via input stream on the socket
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			// we get character output stream to client (for headers)
			out = new PrintWriter(connection.getOutputStream());
			// get binary output stream to client (for requested data)
			dataOut = new BufferedOutputStream(connection.getOutputStream());
			
			// get first line of the request from the client
			String input = in.readLine();
			// we parse the request with a string tokenizer
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase();
			
			if (method.equals("GET") && parse.hasMoreElements())
		         fileRequested = parse.nextToken().toLowerCase();
			else
		         throw new FileNotFoundException();
			
			// we support only GET methods
			if (!method.equals("GET")  &&  !method.equals("HEAD")) {
				// we return the not supported file to the client
				File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
				int fileLength = (int) file.length();
				String contentMimeType = "text/html";
				//read content to return to client
				byte[] fileData = readFileData(file, fileLength);
					
				// we send HTTP Headers with data to client
				out.println("HTTP/1.1 501 Not Implemented");
				out.println("Server: Java HTTP Server from SSaurel : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: " + contentMimeType);
				out.println("Content-length: " + fileLength);
				out.println(); // blank line between headers and content, very important !
				out.flush(); // flush character output stream buffer
				// file
				dataOut.write(fileData, 0, fileLength);
				dataOut.flush();
				
			} else {
				// GET or HEAD method
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_FILE;
				}
				
				while (fileRequested.indexOf("/")==0)// Remove leading / from filename
					fileRequested=fileRequested.substring(1);
				
				File file = new File(WEB_ROOT, fileRequested);
				if(file.exists() && !(file.canRead())) {
					out.print("HTTP/1.0 403 Forbidden\r\n"+ "Location: /" + fileRequested + "/\r\n\r\n");
			        out.close();
			        return;
				}
				int fileLength = (int) file.length();
				String content = getContentType(fileRequested);
				
				if (method.equals("GET")) { // GET method so we return content
					byte[] fileData = readFileData(file, fileLength);
					
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Date: " + new Date());
					out.println("Content-type: " + content);
					out.println("Content-length: " + fileLength);
					out.println(); // blank line between headers and content, very important !
					out.flush(); // flush character output stream buffer
					
					dataOut.write(fileData, 0, fileLength);
					dataOut.flush();
				}	
			}
			
		} catch (FileNotFoundException fnfe) {
			try {
				fileNotFound(out, dataOut, fileRequested);	
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
	
	private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, FILE_NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();

	}
	
}
