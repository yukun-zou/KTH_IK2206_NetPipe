import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Base64;

public class Server_Session {
    HandshakeMessage sessionMessage;
    public byte[] sessionKeyBytes;
    public byte[] sessionIV;
    public SessionKey sessionKey;
    public SessionCipher sessionEncrypter;
    public SessionCipher sessionDecrypter;
    public boolean debug = false;

    public Server_Session(Socket socket, String privateKeyFile) throws Exception {
        sessionMessage = HandshakeMessage.recv(socket);
        if(sessionMessage.getType().getCode() == 3){
            FileInputStream instream = new FileInputStream(privateKeyFile);
            byte[] privateKeyBytes = instream.readAllBytes();
            if(debug) {
                System.out.println("Session Received");
            }

            HandshakeCrypto serverSession = new HandshakeCrypto(privateKeyBytes);

            sessionKeyBytes = serverSession.decrypt(Base64.getDecoder().decode(sessionMessage.getParameter("SessionKey")));
            sessionIV = serverSession.decrypt(Base64.getDecoder().decode(sessionMessage.getParameter("SessionIV")));
            sessionKey =  new SessionKey(sessionKeyBytes);
            sessionEncrypter = new SessionCipher(sessionKey, sessionIV, 1);
            sessionDecrypter = new SessionCipher(sessionKey, sessionIV, 0);

        }
        else {
            throw new Exception();
        }
    }
    public HandshakeMessage getSessionMessage(){
        return sessionMessage;
    }
}
