Name: Deanne Chan
SCU ID: W1393868

Assignment: Programming Assignment 1 - Web Server
Date: 

~ High-level description of the assignment and what the program(s) does


~ A list of submitted files

- src/webserver folder
   - HTTPServer.java
   - Server.java

- files folder
   - index.html
   - f.css
   - 404.html
   - CAMP-0119-Press-release-photo-white.jpg
   - EnginMove.png
   - image42.jpg
   - learningglass-760x480.jpg
   - love-simon.jpg
   - PresidentialSearchBadge.png

- Readme.txt
- Makefile.txt
- Scriptfile.docx
- Screenshots

~ Instructions for running program
cd src
javac webserver/*.java
java webServer.Server -document_root "./files" -port 8080
# open the browser -- localhost:8080


# open terminal
telnet localhost 8080
get / HTTP/1.1
