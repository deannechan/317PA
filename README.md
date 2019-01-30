# Programming Assignment

High-level description of the assignment and what the program(s) does
--------

A list of submitted files
--------
* src/webserver folder
   * HTTPServer.java
   * Server.java

* files folder
   * index.html
   * f.css
   * 404.html
   * CAMP-0119-Press-release-photo-white.jpg
   * EnginMove.png
   * image42.jpg
   * learningglass-760x480.jpg
   * love-simon.jpg
   * PresidentialSearchBadge.png

* Readme.txt
* Makefile.txt
* Scriptfile.docx
* Screenshots

Instructions for running program
--------
1. Go to folder `cd src`  
2. Compile java files `javac webserver/*.java`  
3. Run the server `java webServer.Server -document_root "./files" -port 8080`  
4. Open the browser `localhost:8080`  
5. Open terminal and execute commands
`telnet localhost 8080`  
`get / HTTP/1.1`
