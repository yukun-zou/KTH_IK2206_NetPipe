import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class Send_ClientHello {
    HandshakeMessage clientHelloMessage;
    public boolean debug = false;

    public Send_ClientHello(Socket socket, String certificatepath) {
        try {
            FileInputStream instream = new FileInputStream(certificatepath);
            X509Certificate clientCertificate = new HandshakeCertificate(instream).getCertificate();
            clientHelloMessage = new HandshakeMessage(HandshakeMessage.MessageType.CLIENTHELLO);
            String clientCertificateString = Base64.getEncoder().encodeToString(clientCertificate.getEncoded());
            clientHelloMessage.putParameter("Certificate", clientCertificateString);
            clientHelloMessage.send(socket);
            if(debug) {
                System.out.println("Clienthello has been sent");
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }
    public HandshakeMessage getClientHelloMessage(){
        return clientHelloMessage;
    }
}
