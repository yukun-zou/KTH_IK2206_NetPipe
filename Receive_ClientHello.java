import java.io.FileInputStream;
import java.net.Socket;
import java.util.Base64;

public class Receive_ClientHello {
    HandshakeMessage clientHelloMessage;
    public HandshakeCertificate clientCertificate;
    HandshakeCertificate caCertificate;
    private String CertificateString;
    public boolean debug = false;

    public Receive_ClientHello(Socket socket, String caPath) throws Exception {
        FileInputStream instream = new FileInputStream(caPath);
        caCertificate = new HandshakeCertificate(instream);

        clientHelloMessage = HandshakeMessage.recv(socket);
        if (clientHelloMessage.getType().getCode() == 1) {
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
