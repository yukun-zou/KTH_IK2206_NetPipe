import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Receive_ClientFinish {
    HandshakeMessage clientFinishMessage;
    HandshakeCrypto ClientFinishCrypto;
    HandshakeDigest clientDigest_compare;
    public boolean debug = false;

    public Receive_ClientFinish(Socket socket,HandshakeCertificate clientCertificate,HandshakeMessage clientHelloMessage,HandshakeMessage sessionMessage) throws Exception {
        clientFinishMessage = HandshakeMessage.recv(socket);
        if(clientFinishMessage.getType().getCode() == 4){
            ClientFinishCrypto = new HandshakeCrypto(clientCertificate);
            if(debug) {
                System.out.println("ClientFinish Received");
            }

            byte[] timeBytes =  ClientFinishCrypto.decrypt((Base64.getDecoder().decode(clientFinishMessage.getParameter("TimeStamp"))));
            byte[] clientDigest = ClientFinishCrypto.decrypt((Base64.getDecoder().decode(clientFinishMessage.getParameter("Signature"))));
            clientDigest_compare = new HandshakeDigest();
            clientDigest_compare.update(clientHelloMessage.getBytes());
            clientDigest_compare.update(sessionMessage.getBytes());

            clientDigest_compare.digest();
            if(Arrays.equals(clientDigest_compare.digest,clientDigest)) {
                if(debug) {
                    System.out.println("Digest ok");
                }
            }else{
                throw new Exception();
            }
            String s = new String(timeBytes,"UTF-8");
            int timeStamp_recv = Integer.parseInt(s.substring(s.length()-1));
            long nowtime_origin = System.currentTimeMillis();
            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time_NOW=format.format(nowtime_origin);

            int nowtime_get = Integer.parseInt(time_NOW.substring(time_NOW.length()-1));
            if(debug) {
                System.out.println("nowtime:" + time_NOW);
                System.out.println("timeStampReceived:" + s);
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
            }else{throw new Exception("Timestamp error");}
        }
        else{
            throw new Exception();
        }
    }

    public HandshakeMessage getClientFinishMessage(){
        return clientFinishMessage;
    }
}
