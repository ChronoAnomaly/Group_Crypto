package group_crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by brett on 2015-04-06.
 */
public class EncryptionTest {

    public static void main(String[] args) throws IOException {


        // TODO possibly work on making this test run the EncryptionUtil program and pass args to it
        String command, file, destination;
        Scanner in = new Scanner(System.in);
        System.out.print("Enter: command file destination\n");
        command = in.next();
        file = in.next();
        destination = in.next();

        Process process = new ProcessBuilder("EncryptionUtil", command, file, destination).start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        System.out.printf("Output of running %s is:", Arrays.toString(args));

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
//        EncryptionUtil sec = null;
//        sec = new EncryptionUtil();
//
//
//        try {
//
//            System.out.println("Testing file encrypting: ");
//            File input = new File("To Encrypt.txt");
//            File output = new File("Encrypted Output.txt");
//
//            sec.encrypt(input, output);
//
//            File deOut = new File("Decrypted Output.txt");
//
//            sec.decrypt(output, deOut);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
