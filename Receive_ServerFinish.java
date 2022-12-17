import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Receive_ServerFinish {
    HandshakeMessage serverFinishMessage;
    public boolean debug = false;

    public Receive_ServerFinish(Socket socket,HandshakeCertificate serverCertifcate,HandshakeMessage serverHelloMessage) throws Exception {
        serverFinishMessage = HandshakeMessage.recv(socket);
        if (serverFinishMessage.getType().getCode() !=5 ){
            throw new Exception();
        }
        HandshakeCrypto serverFinish = new HandshakeCrypto(serverCertifcate);
        if(debug) {
            System.out.println("Serverfinish Received");
        }
        byte[] timeBytes =  serverFinish.decrypt((Base64.getDecoder().decode(serverFinishMessage.getParameter("TimeStamp"))));
        byte[] serverDigest = serverFinish.decrypt((Base64.getDecoder().decode(serverFinishMessage.getParameter("Signature"))));

        HandshakeDigest serverDigest_compare = new HandshakeDigest();
        serverDigest_compare.update(serverHelloMessage.getBytes());
        serverDigest_compare.digest();

        if(Arrays.equals(serverDigest_compare.digest,serverDigest)) {
            if(debug) {
                System.out.println("Digest ok");
            }
        }else {throw new Exception();}

        String nowtime = Timestamp_client();
        String s = new String(timeBytes,"UTF-8");
        int timeStamp_last = Integer.parseInt(s.substring(s.length()-1));
        int nowtimelast = Integer.parseInt(nowtime.substring(nowtime.length()-1));
        if(debug) {
            System.out.println("nowtime=" + nowtime);
            System.out.println("timeStampReceived=" + s);
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

    public HandshakeMessage getServerFinishMessage(){
        return serverFinishMessage;
    }
}
