import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SessionCipher {
    public SessionKey sessionkey;
    public Cipher cipher = null;
    public byte[] IV = null;
    /*
     * Constructor to create a SessionCipher from a SessionKey. The IV is
     * created automatically.
     */
    public SessionCipher(SessionKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        this.sessionkey = key;
        this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
        this.IV = this.cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        cipher.init(Cipher.ENCRYPT_MODE,key.getSecretKey(),new IvParameterSpec(this.IV));
    }

    /*
     * Constructor to create a SessionCipher from a SessionKey and an IV,
     * given as a byte array.
     */

    public SessionCipher(SessionKey key, byte[] ivbytes, int a) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        this.sessionkey = key;
        this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
        this.IV = ivbytes;
        if(a == 0) {
            cipher.init(Cipher.DECRYPT_MODE, key.getSecretKey(), new IvParameterSpec(ivbytes));
        }
        if(a ==1){
            cipher.init(Cipher.ENCRYPT_MODE, key.getSecretKey(), new IvParameterSpec(ivbytes));
        }
    }

    /*
     * Return the SessionKey
     */
    public SessionKey getSessionKey() {
        return sessionkey;
    }

    /*
     * Return the IV as a byte array
     */
    public byte[] getIVBytes() {
        return IV;
    }

    /*
     * Attach OutputStream to which encrypted data will be written.
     * Return result as a CipherOutputStream instance.
     */
    CipherOutputStream openEncryptedOutputStream(OutputStream os) throws InvalidKeyException, InvalidAlgorithmParameterException {
        return new CipherOutputStream(os,this.cipher);
    }

    /*
     * Attach InputStream from which decrypted data will be read.
     * Return result as a CipherInputStream instance.
     */

    CipherInputStream openDecryptedInputStream(InputStream inputstream) throws InvalidKeyException, InvalidAlgorithmParameterException {
        return new CipherInputStream(inputstream,this.cipher);
    }
}
