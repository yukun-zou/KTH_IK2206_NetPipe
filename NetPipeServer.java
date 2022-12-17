import java.net.*;
import java.io.*;

public class NetPipeServer {
    private static String PROGRAMNAME = NetPipeServer.class.getSimpleName();
    private static Arguments arguments;
    Receive_ClientHello receive_clientHello;
    Send_ServerHello send_serverHello;
    Server_Session server_session;
    Receive_ClientFinish receive_clientFinish;
    Send_ServerFinish send_serverFinish;
    public boolean debug;

    public NetPipeServer() {
        debug = false;

    }

    /*
     * Usage: explain how to use the program, then exit with failure status
     */
    private static void usage() {
        String indent = "";
        System.err.println(indent + "Usage: " + PROGRAMNAME + " options");
        System.err.println(indent + "Where options are:");
        indent += "    ";
        System.err.println(indent + "--port=<portnumber>");
        System.err.println(indent + "--usercert=<filename>");
        System.err.println(indent + "--cacert=<filename>");
        System.err.println(indent + "--key=<filename>");
        System.exit(1);
    }

    /*
     * Parse arguments on command line
     */
    private static void parseArgs(String[] args) {
        arguments = new Arguments();
        arguments.setArgumentSpec("port", "portnumber");
        arguments.setArgumentSpec("usercert", "filename");
        arguments.setArgumentSpec("cacert", "filename");
        arguments.setArgumentSpec("key", "filename");

        try {
        arguments.loadArguments(args);
        } catch (IllegalArgumentException ex) {
            usage();
        }
    }

    /*
     * Main program.
     * Parse arguments on command line, wait for connection from client,
     * and call switcher to switch data between streams.
     */
    public static void main( String[] args) throws Exception {
        parseArgs(args);
        ServerSocket serverSocket = null;

        int port = Integer.parseInt(arguments.get("port"));
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.err.printf("Error listening on port %d\n", port);
            System.exit(1);
        }
        Socket socket = null;
        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            System.out.printf("Error accepting connection on port %d\n", port);
            System.exit(1);
        }

        try {
            NetPipeServer netPipeServer = new NetPipeServer();
            netPipeServer.receive_clientHello = new Receive_ClientHello(socket,arguments.get("cacert"));
            netPipeServer.send_serverHello = new Send_ServerHello(socket,arguments.get("usercert"));
            netPipeServer.server_session = new Server_Session(socket,arguments.get("key"));
            netPipeServer.send_serverFinish = new Send_ServerFinish(socket,arguments.get("key"),netPipeServer.send_serverHello.getServerHelloMessage());
            netPipeServer.receive_clientFinish = new Receive_ClientFinish(socket,netPipeServer.receive_clientHello.clientCertificate,netPipeServer.receive_clientHello.getClientHelloMessage(),netPipeServer.server_session.getSessionMessage());
            InputStream  socketInDecry = netPipeServer.server_session.sessionDecrypter.openDecryptedInputStream(socket.getInputStream());
            OutputStream socketOutEncry = netPipeServer.server_session.sessionEncrypter.openEncryptedOutputStream(socket.getOutputStream());
            Forwarder.forwardStreams(System.in, System.out, socketInDecry, socketOutEncry, socket);
        } catch (IOException ex) {
            System.out.println("Stream forwarding error\n");
            System.exit(1);
        }
    }
}
