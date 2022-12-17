import java.net.*;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

public class NetPipeClient {
    private static String PROGRAMNAME = NetPipeClient.class.getSimpleName();
    private static Arguments arguments;

    Send_ClientHello send_clientHello;
    Receive_ServerHello receive_serverHello;
    Client_Session client_session;
    Send_ClientFinish send_clientFinish;
    Receive_ServerFinish receive_serverFinish;
    public boolean debug;

    public NetPipeClient() {
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
        System.err.println(indent + "--host=<hostname>");
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
        arguments.setArgumentSpec("host", "hostname");
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
     * Parse arguments on command line, connect to server,
     * and call forwarder to forward data between streams.
     */
    public static void main( String[] args) throws Exception {
        Socket socket = null;

        parseArgs(args);
        String host = arguments.get("host");
        int port = Integer.parseInt(arguments.get("port"));
        try {
            socket = new Socket(host, port);
        } catch (IOException ex) {
            System.err.printf("Can't connect to server at %s:%d\n", host, port);
            System.exit(1);
        }

        try {
            NetPipeClient netPipeClient = new NetPipeClient();
            netPipeClient.send_clientHello = new Send_ClientHello(socket,arguments.get("usercert"));
            netPipeClient.receive_serverHello = new Receive_ServerHello(socket, arguments.get("cacert"));
            netPipeClient.client_session = new Client_Session(socket,netPipeClient.receive_serverHello.serverCertifcate);
            netPipeClient.send_clientFinish = new Send_ClientFinish(socket,arguments.get("key"),netPipeClient.client_session.getSessionMessage(),netPipeClient.send_clientHello.getClientHelloMessage());
            netPipeClient.receive_serverFinish = new Receive_ServerFinish(socket,netPipeClient.receive_serverHello.serverCertifcate,netPipeClient.receive_serverHello.getServerHelloMessage());
            OutputStream outputStream = netPipeClient.client_session.sessionEncrypter.openEncryptedOutputStream(socket.getOutputStream());
            InputStream  inputStream = netPipeClient.client_session.sessionDecrypter.openDecryptedInputStream(socket.getInputStream());
            Forwarder.forwardStreams(System.in, System.out,inputStream, outputStream, socket);

        } catch (IOException ex) {
            System.out.println("Stream forwarding error\n");
            System.exit(1);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
