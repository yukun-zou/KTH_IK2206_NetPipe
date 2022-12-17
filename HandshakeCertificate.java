import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.*;

import java.security.cert.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * HandshakeCertificate class represents X509 certificates exchanged
 * during initial handshake
 */
public class HandshakeCertificate {
    X509Certificate certificate;

    /*
     * Constructor to create a certificate from data read on an input stream.
     * The data is DER-encoded, in binary or Base64 encoding (PEM format).
     */
    HandshakeCertificate(InputStream instream) throws CertificateException {
        CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
            certificate = (X509Certificate) certificatefactory.generateCertificate(instream);
    }

    /*
     * Constructor to create a certificate from its encoded representation
     * given as a byte array
     */
    HandshakeCertificate(byte[] certbytes) throws CertificateException {
        CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
        InputStream bis = new ByteArrayInputStream(certbytes);
            certificate = (X509Certificate) certificatefactory.generateCertificate(bis);
    }

    /*
     * Return the encoded representation of certificate as a byte array
     */
    public byte[] getBytes() throws CertificateEncodingException {return certificate.getEncoded();}

    /*
     * Return the X509 certificate
     */
    public X509Certificate getCertificate() {
        return certificate;
    }

    /*
     * Cryptographically validate a certificate.
     * Throw relevant exception if validation fails.
     */
    public void verify(HandshakeCertificate cacert) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        try {
            this.certificate.verify(cacert.certificate.getPublicKey());
        } catch (CertificateExpiredException e1) {
            throw new CertificateExpiredException();
        } catch (CertificateNotYetValidException e2) {
            throw new CertificateNotYetValidException();
        } catch (CertificateException e3) {
            throw new CertificateException();
        } catch (InvalidKeyException e4) {
            throw new InvalidKeyException();
        } catch (NoSuchAlgorithmException e5) {
            throw new NoSuchAlgorithmException();
        } catch (NoSuchProviderException e6) {
            throw new NoSuchProviderException();
        } catch (SignatureException e7) {
            throw new SignatureException();
        }
    }

    /*
     * Return CN (Common Name) of subject
     */
    public String getCN() {
        X500Principal principal = this.certificate.getSubjectX500Principal();
        try {
            LdapName ldapName = new LdapName(principal.getName());
            for (Rdn rdn : ldapName.getRdns()) {
                if (rdn.getType().equalsIgnoreCase("cn")) {
                    return rdn.getValue().toString();
                }
            }
            return principal.getName();
        } catch (NamingException en) {
            return principal.getName();
        }
    }

    /*
     * return email address of subject
     */
    public String getEmail() {
            X500Principal principal = this.certificate.getSubjectX500Principal();
            try {
                LdapName ldapName = new LdapName(principal.toString());
                for (Rdn rdn : ldapName.getRdns()) {
                    if (rdn.getType().equalsIgnoreCase("emailaddress")) {
                        return rdn.getValue().toString();
                    }
                }
                return principal.toString();
            } catch (NamingException en) {
                return principal.toString();
            }
    }
}
