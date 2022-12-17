import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HandshakeDigest {
    public MessageDigest messagedigest;
    public byte[] digest;
    /*
     * Constructor -- initialise a digest for SHA-256
     */

    public HandshakeDigest() throws NoSuchAlgorithmException {
        messagedigest = MessageDigest.getInstance("SHA-256");
    }

    /*
     * Update digest with input data
     */
    public void update(byte[] input) {
        messagedigest.update(input);
    }

    /*
     * Compute final digest
     */
    public byte[] digest() {
        this.digest = this.messagedigest.digest();
        return messagedigest.digest();
    }
};
