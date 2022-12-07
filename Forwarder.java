import java.net.*;
import java.io.*;

/*
 * Class to forward (binary) data between two pairs of Input/Outputstreams
 */

public class Forwarder {
    /*
     * Thread class to switch data between an InputStream and an OutputStream.
     * Switch until end of file on InputStream.
     * If a socket is given as argument, shut down output to the socket after
     * end of file.
     */
    private static class StreamForwarder implements Runnable {
        private InputStream input;
        private OutputStream output;
        private Socket shutdownSocket = null;

        private static int BUFFERSIZE = 1024;

        StreamForwarder(InputStream inputStream, OutputStream outputStream, Socket socket) {
            this.input = inputStream;
            this.output = outputStream;
            this.shutdownSocket = socket;
        }

        StreamForwarder(InputStream inputStream, OutputStream outputStream) {
            this.input = inputStream;
            this.output = outputStream;
        }

        public void run() {
            byte[] buf = new byte[BUFFERSIZE];
            int nread;
            try {
                while (-1 != (nread = input.read(buf, 0, BUFFERSIZE))) {
                    output.write(buf, 0, nread);
                }
            } catch (IOException ex) {
                System.err.println("Forwarder error in " + Thread.currentThread().getName());
            }
            if (this.shutdownSocket != null) {
                try {
                    this.shutdownSocket.shutdownOutput();
                } catch (IOException e) {}
            }
        }
    }

    /*
     * Start two forwarder threads, one in each direction, and wait
     * for them to complete.
     */
    public static void forwardStreams(InputStream sysinput, OutputStream sysoutput, InputStream netinput, OutputStream netoutput, Socket socket) {

        Thread sysreader = new Thread(new StreamForwarder(sysinput, netoutput, socket), "sysreader");
        Thread syswriter = new Thread(new StreamForwarder(netinput, sysoutput), "syswriter");

        sysreader.start();
        syswriter.start();
        try {
            sysreader.join();
            syswriter.join();
        } catch (Exception ex) {}
    }
}

