import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Receive_ClientFinish {
    HandshakeMessage clientFinishMessage;
    public boolean debug = false;

    public Receive_ClientFinish(Socket socket,HandshakeCertificate clientCertificate,HandshakeMessage clientHelloMessage,HandshakeMessage sessionMessage) throws Exception {
        clientFinishMessage = HandshakeMessage.recv(socket);
        if(clientFinishMessage.getType().getCode() == 4){
            HandshakeCrypto ClientFinishCrypto = new HandshakeCrypto(clientCertificate);
            if(debug) {
                System.out.println("ClientFinish Received");
            }

            byte[] timeBytes =  ClientFinishCrypto.decrypt((Base64.getDecoder().decode(clientFinishMessage.getParameter("TimeStamp"))));
            byte[] clientDigest = ClientFinishCrypto.decrypt((Base64.getDecoder().decode(clientFinishMessage.getParameter("Signature"))));
            HandshakeDigest clientDigest_compare = new HandshakeDigest();
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
            int timeStamp_last = Integer.parseInt(s.substring(s.length()-1));
            String nowtime = Timestamp_client();
            int nowtimelast = Integer.parseInt(nowtime.substring(nowtime.length()-1));
            if(debug) {
                System.out.println("nowtime:" + nowtime);
                System.out.println("timeStampReceived:" + s);
            }
            if (timeStamp_last + 2 > nowtimelast) {
                if (timeStamp_last - 2 < nowtimelast) {
                    if (!s.substring(0, s.length() - 2).equals(nowtime.substring(0, nowtime.length() - 2))) {
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

    public static String Timestamp_client() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String DatePhase=null;
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            DatePhase = sdf.format(date);
            return DatePhase;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DatePhase;
    }

    public HandshakeMessage getClientFinishMessage(){
        return clientFinishMessage;
    }
}
