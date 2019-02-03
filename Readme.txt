Name: Deanne Chan
SCU ID: W1393868

Assignment: Programming Assignment 1 - Web Server
Date: Feb 2, 2019

~ High-level description of the assignment and what the program(s) does
server is ran and listening on a given port
forever loop that listens for connection:
   create a new thread to accept incoming connection
   parse HTTP request
   GET method else return HTTP Status Code 501 Not Implemented
   ensure well-formed request else return HTTP Status Code 400 Bad request
   if '/' is input, append index.html
   ensure file exists else return HTTP Status Code 404 Not Found
   ensure file permissions are set properly else return HTTP Status Code 403 Forbidden
   transmit file contents by reading the file and write out to socket
   close input and output streams
   close connection

~ A list of submitted files
- src/webserver folder
   - HTTPServer.java
   - Server.java

- files folder
   - index.html
   - 404.html
   - 403.html
   - 400.html
   - 501.html
   - CAMP-0119-Press-release-photo-white.jpg
   - EnginMove.png
   - image42.jpg
   - learningglass-760x480.jpg
   - love-simon.jpg
   - PresidentialSearchBadge.png

- Readme.txt
- Makefile.txt
- Scriptfile.docx

~ Instructions for running program
cd src
javac webserver/*.java
java webserver.Server -document_root "../files" -port 8080
# open the browser -- localhost:8080
# open terminal
telnet localhost 8080
get / HTTP/1.1
