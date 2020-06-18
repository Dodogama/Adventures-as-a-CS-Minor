/** CS 1501 Summer 2019
 *  @author Conrad Li
 *  SymCipher interface class: key adds to byte values
 */

import java.util.*;
import java.security.*;

public class Add128 implements SymCipher {
    
    private static final int KEY_SIZE = 128;

    private byte[] key; // encryption key

    public Add128() {
        this.key = new byte[KEY_SIZE];
        try { SecureRandom.getInstanceStrong().nextBytes(this.key); }  // random size 128-bit
        catch (NoSuchAlgorithmException e) { System.out.println(e); }
    }

    public Add128(byte[] bytes) {
        this.key = bytes;
    }

    public byte[] getKey() {
        return this.key;
    }

    /** Takes plaintext message and encrypts by substituting byte values
     *  @param plaintext String containing unencrypted message
     *  @return cipher byte[] containing encrypted message
     */
    public byte[] encode(String plaintext) {
        byte[] message; // plaintext as byte array
        byte[] cipher; // encrypted message byte array

        message = plaintext.getBytes();
        cipher = new byte[message.length];

        System.out.println("Encoding message: " + plaintext);
        System.out.println("Message read as: " + Arrays.toString(message));

        for (int i = 0, j = 0; i < message.length; i++, j++) {  // i: message index, j: key index      
            if (j == this.key.length) { j = 0; } // reset larger key indexes
            cipher[i] = (byte) (message[i] + this.key[j]); 
        }
        System.out.println("Encoded message: " + Arrays.toString(cipher) + "\n");
        return cipher;
    }

    /** Takes encrypted message and decrypts it with reverseMap key
     *  @param cipher byte[] containing encrypted message
     *  @return plaintext String containing decrypted message
     */
    public String decode(byte[] cipher) {
        String plaintext; // decoded cipher String
        byte[] message; // decoded cipher byte array

        message = new byte[cipher.length];

        System.out.println("Decoding message: " + Arrays.toString(cipher));

        for (int i = 0, j = 0; i < cipher.length; i++, j++) { // i: message index, j: key index
            if (j == this.key.length) { j = 0; } // reset larger key indexes
            message[i] = (byte) (cipher[i] - this.key[j]);
        }
        plaintext = new String(message);
        System.out.println("Message read as: " + Arrays.toString(message));
        System.out.println("Decoded message: " + plaintext + "\n");
        return plaintext;
    }
}