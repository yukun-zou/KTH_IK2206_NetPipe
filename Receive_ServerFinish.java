import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Receive_ServerFinish {
    HandshakeMessage serverFinishMessage;
    HandshakeDigest serverDigest_compare;
    public boolean debug = false;

    public Receive_ServerFinish(Socket socket,HandshakeCertificate serverCertifcate,HandshakeMessage serverHelloMessage) throws Exception {
        serverFinishMessage = HandshakeMessage.recv(socket);
        if (serverFinishMessage.getType().getCode() == 5) {
            HandshakeCrypto serverFinish = new HandshakeCrypto(serverCertifcate);
            if (debug) {
                System.out.println("Serverfinish Received");
            }
            byte[] timeBytes = serverFinish.decrypt((Base64.getDecoder().decode(serverFinishMessage.getParameter("TimeStamp"))));
            byte[] serverDigest = serverFinish.decrypt((Base64.getDecoder().decode(serverFinishMessage.getParameter("Signature"))));

            serverDigest_compare = new HandshakeDigest();
            serverDigest_compare.update(serverHelloMessage.getBytes());
            serverDigest_compare.digest();

            if (Arrays.equals(serverDigest_compare.digest, serverDigest)) {
                if (debug) {
                    System.out.println("Digest ok");
                }
            } else {
                throw new Exception();
            }
            long nowtime_origin = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time_NOW = format.format(nowtime_origin);

            String s = new String(timeBytes, "UTF-8");
            int timeStamp_recv = Integer.parseInt(s.substring(s.length() - 1));
            int nowtime_get = Integer.parseInt(time_NOW.substring(time_NOW.length() - 1));
            if (debug) {
                System.out.println("nowtime=" + time_NOW);
                System.out.println("timeStampReceived=" + s);
            }

            if (timeStamp_recv + 2 > nowtime_get) {
                if (timeStamp_recv - 2 < nowtime_get) {
                    if (!s.substring(0, s.length() - 2).equals(time_NOW.substring(0, time_NOW.length() - 2))) {
                        if (debug) {
                            System.out.println("Timestamp error");
                        }
                    } else {
                        if (debug) {
                            System.out.println("Timestamp ok");
                        }
                    }
                }
            } else {
                throw new Exception("Timestamp error");
            }
        } else {
            throw new Exception();
        }
    }

    public HandshakeMessage getServerFinishMessage(){
        return serverFinishMessage;
    }
}
