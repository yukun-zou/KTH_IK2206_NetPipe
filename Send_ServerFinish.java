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

public class Send_ServerFinish {
    HandshakeMessage serverFinishMessage;
    public boolean debug = false;

    public Send_ServerFinish(Socket socket, String privateKeyFile,HandshakeMessage serverHelloMessage) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {

        serverFinishMessage = new HandshakeMessage(HandshakeMessage.MessageType.SERVERFINISHED);
        HandshakeDigest serverDigest = new HandshakeDigest();
        serverDigest.update(serverHelloMessage.getBytes());
        FileInputStream instream = new FileInputStream(privateKeyFile);

        serverDigest.digest();


        byte[] privateKeyBytes = instream.readAllBytes();
        HandshakeCrypto serverFinish = new HandshakeCrypto(privateKeyBytes);
        String time_NOW = Timestamp_client();
        byte[] digestEncrypted = serverFinish.encrypt(serverDigest.digest);

        byte[] timeBytes = time_NOW.getBytes(StandardCharsets.UTF_8);
        byte[] timeBytesEncrypted = serverFinish.encrypt(timeBytes);
        serverFinishMessage.putParameter("TimeStamp", Base64.getEncoder().encodeToString(timeBytesEncrypted));
        serverFinishMessage.putParameter("Signature", Base64.getEncoder().encodeToString(digestEncrypted));

        serverFinishMessage.send(socket);
        if(debug) {
            System.out.println("Serverfinish has been sent");
        }
    }
    public static String Timestamp_client() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String DatePHase=null;
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            DatePHase = sdf.format(date);
            return DatePHase;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DatePHase;

    }
    public HandshakeMessage getServerFinishMessage(){
        return serverFinishMessage;
    }
}
