This is Netpipe project code for KTH IK2206. Below is inrtoduction of this project. This project tells you how to build NetPipe application in Java. If you like this project, any stars will help, thanks!

Overview

In this assignment, you will implement NetPipe, a network application that provides a basic communication service: it sets up a TCP connection between two hosts, and forwards data between system input/output and the TCP connection. This application is very similar to "netcat", or "nc", a popular application for testing and evaluation. Here, you will implement it in Java, and make it secure.

You can think of NetPipe as an application that sets up a secure tunnel between two computers, so that you can exchange data between them in a safe way. In this way NetPipe can serve as a general-purpose VPN (Virtual Private Network) application that allows you to connect computers across the network in a secure way. For example, in the terminal, system input and output are by default associated to the keyboard and screen, respectively. So if you run NetPipe from the command line in a terminal, you can use it to send data between two terminal windows on different hosts. If you redirect system input and output to files, NetPipe can be used as a simple file transfer program. See the assignment introduction slides for examples of NetPipe usage.

# Files for Project Assignment "NetPipe"
I have created a file to store every function and action of client and server.
The following are the core files of the project.
- `README.md` This file. It is in in Markdown format. You can view it as a text file, or use a Markdown preview tool (there are plenty). 
- `NetPipeClient.java` is a working client for the NetPipe application.
- `NetPipeServer.java` is a working server for the NetPipe application.
- `Arguments.java` is a simple parser for command line arguments. It is used by NetPipeClient and NetPipeServer. 
- `Forwarder.java` is a class with two threads to forward data between streams. It is used by NetPipeClient and NetPipeServer.
- `HandshakeMessage.java` is a class with methods and declarations for the message exchange between client and server during the handshake phase. Use it to implement the handshake protocol. (It is *not* used by any of other classes, since they do not support security.)


