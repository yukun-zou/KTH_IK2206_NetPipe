import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class Send_ClientHello {
    HandshakeMessage clientHelloMessage;
    X509Certificate clientCertificate;
    public boolean debug = false;

    public Send_ClientHello(Socket socket, String certificatepath) throws IOException, CertificateException {
            FileInputStream instream = new FileInputStream(certificatepath);
            clientCertificate = new HandshakeCertificate(instream).getCertificate();
            clientHelloMessage = new HandshakeMessage(HandshakeMessage.MessageType.CLIENTHELLO);
            String clientCertificateString = Base64.getEncoder().encodeToString(clientCertificate.getEncoded());
            clientHelloMessage.putParameter("Certificate", clientCertificateString);
            clientHelloMessage.send(socket);
            if(debug) {
                System.out.println("Clienthello has been sent");
            }

    }
    public HandshakeMessage getClientHelloMessage(){
        return clientHelloMessage;
    }
}
