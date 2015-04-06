package Scothorn_RSA_TEST1;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by brett on 2015-04-06.
 */
public class EncryptionTest {

    public static void main(String[] args) {

        EncryptionUtil sec = new EncryptionUtil();


        try {


            final String originalText = "Text to be encrypted ";
            ObjectInputStream inputStream = null;

            // Encrypt the string using the public key
            inputStream = new ObjectInputStream(new FileInputStream(sec.PUBLIC_KEY_FILE));
            final PublicKey publicKey = (PublicKey) inputStream.readObject();
            final byte[] cipherText = sec.encrypt(originalText, publicKey);

            // Decrypt the cipher text using the private key.
            inputStream = new ObjectInputStream(new FileInputStream(sec.PRIVATE_KEY_FILE));
            final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            final String plainText = sec.decrypt(cipherText, privateKey);

            // Printing the Original, Encrypted and Decrypted Text
            System.out.println("Original: " + originalText);
            System.out.println("Encrypted: " +cipherText.toString());
            System.out.println("Decrypted: " + plainText);

            System.out.println("Testing file encrypting: ");
            File input = new File("tester.txt");
            File output = new File("Encrypted Output.txt");

            sec.encrypt(input, output, publicKey);

            File deOut = new File("Decrypted Output.txt");

            sec.decrypt(output, deOut, privateKey);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
