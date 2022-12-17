import java.io.FileInputStream;
import java.net.Socket;
import java.util.Base64;

public class Receive_ClientHello {
    HandshakeMessage clientHelloMessage;
    public HandshakeCertificate clientCertificate;
    private String CertificateString;
    public boolean debug = false;

    public Receive_ClientHello(Socket socket, String caPath) throws Exception {
        clientHelloMessage = HandshakeMessage.recv(socket);
        if (clientHelloMessage.getType().getCode() == 1) {
            FileInputStream instream = new FileInputStream(caPath);
            HandshakeCertificate caCertificate = new HandshakeCertificate(instream);
            if(debug) {
                System.out.println("Clienthello Received");
            }

            CertificateString = clientHelloMessage.getParameter("Certificate");
            byte[] certificateByte = Base64.getDecoder().decode(CertificateString);
            clientCertificate = new HandshakeCertificate(certificateByte);
            if(debug) {
                System.out.println(CertificateString);
            }
            clientCertificate.verify(caCertificate);
            caCertificate.verify(caCertificate);

        }
    }
        public HandshakeMessage getClientHelloMessage(){
            return clientHelloMessage;
        }
}
