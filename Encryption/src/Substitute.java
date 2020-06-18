/** CS 1501 Summer 2019
 *  @author Conrad Li
 *  SymCipher interface class: key remaps bytes with substiution
 */

import java.util.*;

public class Substitute implements SymCipher {

    private static final int KEY_SIZE = 256;
    
    private byte[] key;         // encryption key
    private byte[] inverseMap;  // reverse encryption key

    public Substitute() {
        this.key = new byte[KEY_SIZE];
        this.inverseMap = new byte[KEY_SIZE];

        ArrayList<Byte> tempList = new ArrayList<>(KEY_SIZE); 
        for (int i = 0; i < KEY_SIZE; i++) {
            tempList.add((byte) i); // get bytes 0-255
        }
        Collections.shuffle(tempList); // permute bytes 

        int temp; // mapped index
        for (int j = 0; j < KEY_SIZE; j++) {
            this.key[j] = tempList.get(j);      // add bytes to key
            temp = this.key[j] & 0xFF;          // get index from unsigned byte
            this.inverseMap[temp] = (byte) j;   // reverse map with index
        }
    }

    public Substitute(byte[] bytes) {
        this.key = bytes; // assume 256 bytes
        this.inverseMap = new byte[KEY_SIZE];

        int temp; // mapped index
        for (int i = 0; i < KEY_SIZE; i++) {
            temp = this.key[i] & 0xFF;          // get index from unsigned byte
            this.inverseMap[temp] = (byte) i;   // reverse map with index
        }
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
        byte[] cipher;  // encrypted message byte array

        message = plaintext.getBytes();
        cipher = new byte[message.length];

        System.out.println("Encoding message: " + plaintext);
        System.out.println("Message read as: " + Arrays.toString(message));

        int temp; // mapped index
        for (int i = 0; i < message.length; i++) {
            temp = message[i] & 0xFF;   // get index from unsigned byte
            cipher[i] = this.key[temp]; // build encrypted message from key
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
        byte[] message;  // decoded cipher byte array

        message = new byte[cipher.length];

        System.out.println("Decoding message: " + Arrays.toString(cipher));
        
        int temp; // mapped index
        for (int i = 0; i < message.length; i++) {
            temp = cipher[i] & 0xFF;            // get index from unsigned byte
            message[i] = this.inverseMap[temp]; // build decoded message from reverse key
        }
        plaintext = new String(message);
        System.out.println("Message read as: " + Arrays.toString(message));
        System.out.println("Decoded message: " + plaintext + "\n");
        return plaintext;
    }
}