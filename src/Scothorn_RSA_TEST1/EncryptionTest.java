package Scothorn_RSA_TEST1;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by brett on 2015-04-06.
 */
public class EncryptionTest {

    public static void main(String[] args) {

        EncryptionUtil sec = null;
        sec = new EncryptionUtil();


        try {

            System.out.println("Testing file encrypting: ");
            File input = new File("To Encrypt.txt");
            File output = new File("Encrypted Output.txt");

            sec.encrypt(input, output);

            File deOut = new File("Decrypted Output.txt");

            sec.decrypt(output, deOut);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
