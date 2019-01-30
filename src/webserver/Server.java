package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {
		if(args.length!=4) {
			System.out.println("4 arguments must be given");
			System.exit(0);
		}
		try {
			String rootFolder = args[1];
			int port = Integer.parseInt(args[3]);
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server started.\nListening for connections on port : " 
					+ port + " ...\n");
			
			while (true) {
				// waits for incoming client/TCP connection request and processes it
				HTTPServer myServer = new HTTPServer(rootFolder, serverSocket.accept());
				// create dedicated thread to manage the client connection
				Thread thread = new Thread(myServer);
				thread.start();
			}
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}

}
