import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

public class Client_Session {
    HandshakeCrypto ClientSession;
    public SessionKey sessionKey;
    public byte[] sessionKeyBytes;
    public byte[] sessionIV;
    public boolean debug = false;

    HandshakeMessage sessionMessage;
    public SessionCipher sessionEncrypter;
    public SessionCipher sessionDecrypter;

    public Client_Session(Socket socket,HandshakeCertificate serverCertifcate) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, IOException, IllegalBlockSizeException, BadPaddingException {
        sessionKey = new SessionKey(128);

        sessionEncrypter = new SessionCipher(sessionKey);
        sessionIV = sessionEncrypter.getIVBytes();

        sessionKeyBytes = sessionKey.getKeyBytes();
        sessionDecrypter = new SessionCipher(sessionKey, sessionIV, 0);

        sessionMessage = new HandshakeMessage(HandshakeMessage.MessageType.SESSION);
        ClientSession = new HandshakeCrypto(serverCertifcate);

        byte[] sessionIVEncrypted = ClientSession.encrypt(sessionIV);
        sessionMessage.putParameter("SessionIV", Base64.getEncoder().encodeToString(sessionIVEncrypted));
        byte[] sessionKeyEncrypted =  ClientSession.encrypt(sessionKeyBytes);
        sessionMessage.putParameter("SessionKey", Base64.getEncoder().encodeToString(sessionKeyEncrypted));
        sessionMessage.send(socket);
        if(debug) {
            System.out.println("Clientsession have been sent");
        }
    }
    public HandshakeMessage getSessionMessage(){
        return sessionMessage;
    }
}
