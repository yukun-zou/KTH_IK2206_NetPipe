import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Send_ClientFinish {
    HandshakeMessage clientFinishMessage;
    public boolean debug = false;

    public Send_ClientFinish(Socket socket, String privateKeyFile,HandshakeMessage sessionMessage,HandshakeMessage clientHelloMessage) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {

        HandshakeDigest clientDigest = new HandshakeDigest();
        clientDigest.update(clientHelloMessage.getBytes());
        clientDigest.update(sessionMessage.getBytes());
        clientFinishMessage = new HandshakeMessage(HandshakeMessage.MessageType.CLIENTFINISHED);
        clientDigest.digest();

        FileInputStream instream = new FileInputStream(privateKeyFile);
        String time = Timestamp_client();

        byte[] privateKeyBytes = instream.readAllBytes();
        HandshakeCrypto clientFinish = new HandshakeCrypto(privateKeyBytes);
        byte[] digestEncrypted = clientFinish.encrypt(clientDigest.digest);
        byte[] timeBytes = time.getBytes(StandardCharsets.UTF_8);
        byte[] timeBytesEncrypted = clientFinish.encrypt(timeBytes);

        clientFinishMessage.putParameter("TimeStamp", Base64.getEncoder().encodeToString(timeBytesEncrypted));
        clientFinishMessage.putParameter("Signature", Base64.getEncoder().encodeToString(digestEncrypted));
        clientFinishMessage.send(socket);
        if(debug) {
            System.out.println("Clientfinish has been sent");
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
