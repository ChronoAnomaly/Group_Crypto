package group_crypto;

/**
 * Code originally from  https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
 * Author: Unknown
 * retrieved on 2015/3/26
 */

/**
 * Created by brett on 2015-03-26.
 */

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author JavaDigest
 *
 */
public class EncryptionUtil {

    // String to hold name of the encryption algorithm.
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    // Folder location for where the AES key is stored.
    private static final String FOLDER = "./keys/";

    // Location of the private key for the AES algorithm.
    private static final String KEY_FILE =   FOLDER + "secure.key";

    // The block sizes that are used in the encryption and decryption process.
    private static final int BLOCK_SIZE = 1024;
    private static final int AES_KEY_SIZE = 128;

    // Holds the base AES key.
    private static byte[] aesKey;
    // Cipher used to encrypt and decrypt with the AES algorithm.
    private static Cipher aesCipher;
    // Modified key, based on the aesKey.
    private static SecretKeySpec aeskeySpec;


    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes. Store the set of keys in Private.key and Public.key files.
     */
    private static void generateKey() {

        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(AES_KEY_SIZE);
            SecretKey key = kgen.generateKey();
            aesKey = key.getEncoded();
            aeskeySpec = new SecretKeySpec(aesKey, "AES");
            aesCipher = Cipher.getInstance(ALGORITHM);

            saveKey();


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates a file that is used to store the AES key for further uses. It makes the file inside of a sub folder
     * based on where KEY_FILE points to.
     *
     * @throws IOException
     */
    private static void saveKey() throws IOException {


            File privateKeyFile = new File(KEY_FILE);

            // Create file to store private key
            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
            privateKeyFile.createNewFile();

        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
        os.write(aesKey);
        os.close();

    }

    /**
     * Loads the AES key that was saved based on a previous run of the program. It loads into aesKey and then
     * initializes the modified key into aesKeySpec.  The AES cipher is then generated for the current run.
     *
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static void loadKey() throws GeneralSecurityException, IOException {

        File privateKeyFile = new File(KEY_FILE);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(privateKeyFile));

        aesKey = new byte[AES_KEY_SIZE/8];

        ois.read(aesKey);
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
        aesCipher = Cipher.getInstance(ALGORITHM);

    }


    /**
     * The method checks if the pair of public and private key has been generated.
     *
     * @return flag indicating if the pair of keys were generated.
     */
    private static boolean areKeysPresent() {

        File secureKey = new File(KEY_FILE);

        return secureKey.exists();
    }

    /*
    ===========================================================================
    functions using files
    ===========================================================================
     */

    /**
     * Encrypts a file. The file to be encrypted must be placed inside of the project's directory.
     *
     * @param in - The file to encrypt
     * @param out - The output file where the encrypt file is placed
     * @throws IOException
     * @throws InvalidKeyException
     */
    private static void encrypt(File in, File out) throws IOException, InvalidKeyException {

        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

        copy(is, os);

        is.close();
        os.close();
    }

    /**
     * Decrypts a file. The file to be decrypted must be placed inside of the project's directory.
     *
     * @param in - The encrypted file that is to be decrypted
     * @param out - The file where decrypted output is placed
     * @throws IOException
     * @throws InvalidKeyException
     */
    private static void decrypt(File in, File out) throws IOException, InvalidKeyException {

        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);

        copy(is, os);

        is.close();
        os.close();

    }

    /**
     * Reads and writes out to the buffers that are being used for the encryption/decryption methods.
     *
     * @param is - The input stream that is being read from
     * @param os - The output stream that is being wrote to
     * @throws IOException
     */
    private static void copy(InputStream is, OutputStream os) throws IOException {

        int i;
        byte[] b = new byte[BLOCK_SIZE];
        while( (i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }


    /**
     * Main method of the program. Runs from the command line to call the encrypt or decrypt methods.
     *
     * @param args - Must contain 3 arguments for the program to run : command fileName destination
     *             (commands are [encrypt] and [decrypt]
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static void main(String[] args) throws GeneralSecurityException, IOException {

        /* Checks if the program has had a previous run created a AES key, if not, then it will create and store a
         AES key */
        if (!areKeysPresent()) {
            generateKey();
        } else {
            loadKey();
        }

        // ensures the correct number of arguments have been given to the program
        if (args.length < 3) {
            System.out.println("You must provide the the function type (encrypt/decrypt), the file name" +
                    ", and the location to store the key and file.");
        } else {

            File destinationFolder = new File(args[2]);

            if (!destinationFolder.exists() && !destinationFolder.mkdirs()) {
                    System.out.println("Unable to access the destination location.");
                    System.exit(1);
            }

            if (args[0].equalsIgnoreCase("encrypt")) {
                File input = new File(args[1]);
                File encrypted = new File(destinationFolder, args[1]);
                encrypt(input, encrypted);
            } else if ( args[0].equalsIgnoreCase("decrypt")) {
                File input = new File(args[1]);
                File decrypted = new File(destinationFolder, args[1]);
                decrypt(input, decrypted);
            } else {
                System.out.println("Invalid command");
                System.exit(1);
            }
        }
    }
}