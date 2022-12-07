/*
 * Handshake message encoding/decoding and transmission
 * for IK2206 project.
 *
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.net.Socket;
import java.util.Properties;

/*
 * A Handshake message is represented as a set of parameters -- <key, value> pairs.
 * Extends Properties class.
 */

public class HandshakeMessage extends Properties {
    /*
     * Constants to represent message type
     */
    public enum MessageType {
        CLIENTHELLO    (1),
        SERVERHELLO    (2),
        SESSION        (3),
        CLIENTFINISHED (4),
        SERVERFINISHED (5);

        private final int code; // Integer code representing message type
        MessageType(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
    static final int LENGTHBYTES = 2; //Width of length field in bytes
    private MessageType messageType;

    public HandshakeMessage(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getType() {
        return this.messageType;
    }

    /*
     * Get the value of a parameter
     */
    public String getParameter(String param) {
        return this.getProperty(param);
    }

    /*
     * Assign a parameter
     */
    public void putParameter(String param, String value) {
        this.put(param, value);
    }

    /*
     * Q&D encoding and decoding of message as Java object.
     *
     * Encode a message into a byte array, by writing the object to an ObjectOutputStream
     * connected to ByteArrayOutputStream.
     */
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
        objectOutputStream.writeObject(this);
        byte[] bytes = byteOutputStream.toByteArray();
        return bytes;
    }

    /*
     * Decode a byte array into a message, by feeding the byte array into a ByteArrayInputStream
     * and connecting an ObjectInputStream to it.
     */
    public static HandshakeMessage fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
        HandshakeMessage message = (HandshakeMessage) objectInputStream.readObject();
        return message;
    }

    /*
     * Send a handshake message on a socket.
     * Encode message as a byte array.
     * Prepend the byte array with an integer (big endian) with the length of the string.
     */

    public void send(Socket socket) throws IOException {
        byte[] bytes = this.getBytes();
        byte[] lengthBytes = new byte[LENGTHBYTES];
        ByteBuffer lengthBuffer = ByteBuffer.wrap(lengthBytes); // Big endian byte buffer with length (a short)
        lengthBuffer.putShort(0, (short) bytes.length);
        OutputStream output = socket.getOutputStream();
        output.write(lengthBytes);
        output.write(bytes);
        output.flush();
    }

    /*
     * Receive a handshake message on a socket
     *
     * Read an integer (big endian), which gives the size of the message in bytes.
     * Then read the byte array and convert it to a message.
     */

    public static HandshakeMessage recv(Socket socket) throws IOException, ClassNotFoundException {
        InputStream input = socket.getInputStream();
        byte[] lengthBytes = new byte[LENGTHBYTES];
        int nread;

        if (LENGTHBYTES != (nread = input.read(lengthBytes, 0, LENGTHBYTES))) {
            throw new IOException("Error receiving message length");
        }
        ByteBuffer lengthBuffer = ByteBuffer.wrap(lengthBytes); // Big endian byte buffer with length (a short)
        int length = lengthBuffer.getShort();

        byte[] buffer = new byte[length];
        nread = 0;
        while (nread < length) {
            int n = input.read(buffer, nread, length-nread);
            if (n < 0)
                throw new IOException("Error receiving message");
            nread += n;
        }
        HandshakeMessage message = HandshakeMessage.fromBytes(buffer);
        return message;
    }
};
