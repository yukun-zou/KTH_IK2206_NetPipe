import java.io.FileInputStream;
import java.net.Socket;
import java.util.Base64;

public class Receive_ServerHello {
    HandshakeMessage serverHelloMessage;
    public HandshakeCertificate serverCertifcate;
    HandshakeCertificate caCertificate;
    private String serverCertificateString;
    public boolean debug = false;

    public Receive_ServerHello(Socket socket, String caPath) throws Exception {
        FileInputStream instream = new FileInputStream(caPath);
        caCertificate = new HandshakeCertificate(instream);
        serverHelloMessage = HandshakeMessage.recv(socket);
        if(serverHelloMessage.getType().getCode() ==2){
            if(debug) {
                System.out.println("Serverhello Received");
            }

            serverCertificateString = serverHelloMessage.getParameter("Certificate");
            byte[] certificateByte = Base64.getDecoder().decode(serverCertificateString);
            serverCertifcate = new HandshakeCertificate(certificateByte);

            serverCertifcate.verify(caCertificate);
            caCertificate.verify(caCertificate);
        }else {
            throw new Exception();
        }
    }
    public HandshakeMessage getServerHelloMessage(){
        return serverHelloMessage;
    }
}
