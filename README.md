# Files for Project Assignment "NetPipe"

- `README.md` This file. It is in in Markdown format. You can view it as a text file, or use a Markdown preview tool (there are plenty). 
- `NetPipeClient.java` is a working client for the NetPipe application, without security.
- `NetPipeServer.java` is a working server for the NetPipe application, without security.
- `Arguments.java` is a simple parser for command line arguments. It is used by NetPipeClient and NetPipeServer. 
- `Forwarder.java` is a class with two threads to forward data between streams. It is used by NetPipeClient and NetPipeServer.
- `HandshakeMessage.java` is a class with methods and declarations for the message exchange between client and server during the handshake phase. Use it to implement the handshake protocol. (It is *not* used by any of other classes, since they do not support security.)


