import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class Send_ServerHello {
    HandshakeMessage serverHelloMessage;
    public boolean debug = false;

    public Send_ServerHello(Socket socket, String certificatepath) throws CertificateException, IOException {
        FileInputStream instream = new FileInputStream(certificatepath);
        X509Certificate serverCertificate = new HandshakeCertificate(instream).getCertificate();

        serverHelloMessage = new HandshakeMessage(HandshakeMessage.MessageType.SERVERHELLO);
        String serverCertificateString = Base64.getEncoder().encodeToString(serverCertificate.getEncoded());

        serverHelloMessage.putParameter("Certificate", serverCertificateString);
        serverHelloMessage.send(socket);
        if(debug) {
            System.out.println("Severhello has been sent");
        }
    }
        public HandshakeMessage getServerHelloMessage(){
            return serverHelloMessage;
        }
}
