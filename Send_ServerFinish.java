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
    HandshakeDigest serverDigest;
    HandshakeCrypto serverFinish;
    public boolean debug = false;

    public Send_ServerFinish(Socket socket, String privateKeyFile,HandshakeMessage serverHelloMessage) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {
        FileInputStream instream = new FileInputStream(privateKeyFile);

        serverDigest = new HandshakeDigest();
        serverDigest.update(serverHelloMessage.getBytes());
        serverDigest.digest();
        serverFinishMessage = new HandshakeMessage(HandshakeMessage.MessageType.SERVERFINISHED);

        byte[] privateKeyBytes = instream.readAllBytes();
        serverFinish = new HandshakeCrypto(privateKeyBytes);

        long nowtime_origin = System.currentTimeMillis();
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_NOW=format.format(nowtime_origin);

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

    public HandshakeMessage getServerFinishMessage(){
        return serverFinishMessage;
    }
}
