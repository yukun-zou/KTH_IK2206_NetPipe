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
    HandshakeDigest clientDigest;
    HandshakeCrypto clientFinish;
    public boolean debug = false;

    public Send_ClientFinish(Socket socket, String privateKeyFile,HandshakeMessage sessionMessage,HandshakeMessage clientHelloMessage) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {
        FileInputStream instream = new FileInputStream(privateKeyFile);

        clientDigest = new HandshakeDigest();
        clientDigest.update(clientHelloMessage.getBytes());
        clientDigest.update(sessionMessage.getBytes());
        clientFinishMessage = new HandshakeMessage(HandshakeMessage.MessageType.CLIENTFINISHED);
        clientDigest.digest();


        long nowtime_origin = System.currentTimeMillis();
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_NOW=format.format(nowtime_origin);

        byte[] privateKeyBytes = instream.readAllBytes();
        clientFinish = new HandshakeCrypto(privateKeyBytes);
        byte[] digestEncrypted = clientFinish.encrypt(clientDigest.digest);

        byte[] timeBytes = time_NOW.getBytes(StandardCharsets.UTF_8);
        byte[] timeBytesEncrypted = clientFinish.encrypt(timeBytes);

        clientFinishMessage.putParameter("TimeStamp", Base64.getEncoder().encodeToString(timeBytesEncrypted));
        clientFinishMessage.putParameter("Signature", Base64.getEncoder().encodeToString(digestEncrypted));
        clientFinishMessage.send(socket);
        if(debug) {
            System.out.println("Clientfinish has been sent");
        }
    }

    public HandshakeMessage getClientFinishMessage(){
        return clientFinishMessage;
    }
}
