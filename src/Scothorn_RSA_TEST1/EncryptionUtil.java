package Scothorn_RSA_TEST1;

/**
 * Code originally from  https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
 * Author: Unknown
 * retrieved on 2015/3/26
 */

/**
 * Created by brett on 2015-03-26.
 */
import java.io.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author JavaDigest
 *
 */
public class EncryptionUtil {

    /**
     * String to hold name of the encryption algorithm.
     */
    public final String ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * Location of the private key for the AES algorithm.
     */
    public final String KEY_FILE =  "./keys/secure.key";

    private final int BLOCK_SIZE = 1024;
    private final int AES_KEY_SIZE = 128;

    private byte[] aesKey;

    private Cipher aesCipher;
    private SecretKeySpec aeskeySpec;


    public EncryptionUtil() throws GeneralSecurityException, IOException {

        if (!areKeysPresent()) {
            // Method generates a pair of keys using the RSA algorithm and stores it
            // in their respective files
            generateKey();
        } else {
            loadKey();
        }
    }

    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes. Store the set of keys in Private.key and Public.key files.
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void generateKey() {

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
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    public void saveKey() throws IOException, GeneralSecurityException {


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

    public void loadKey() throws GeneralSecurityException, IOException {


        File privateKeyFile = new File(KEY_FILE);


        ObjectInputStream in = new ObjectInputStream(new FileInputStream(privateKeyFile));

        aesKey = new byte[AES_KEY_SIZE/8];
        FileInputStream is = new FileInputStream(privateKeyFile);
        BufferedInputStream bin = new BufferedInputStream(is);
        in.read(aesKey);
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
        aesCipher = Cipher.getInstance(ALGORITHM);

    }


    /**
     * The method checks if the pair of public and private key has been generated.
     *
     * @return flag indicating if the pair of keys were generated.
     */
    public boolean areKeysPresent() {

        File secureKey = new File(KEY_FILE);

        if (secureKey.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /*
    ===========================================================================
    functions using files
    ===========================================================================
     */

    public void encrypt(File in, File out) throws IOException, InvalidKeyException {

        //            final Cipher cipher = Cipher.getInstance(ALGORITHM);
        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

        copy(is, os);

        is.close();
        os.close();
    }

    public void decrypt(File in, File out) throws IOException, InvalidKeyException {

        //            final Cipher cipher = Cipher.getInstance(ALGORITHM);
        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);

        copy(is, os);

        is.close();
        os.close();

    }

    private void copy(InputStream is, OutputStream os) throws IOException {

        int i;
        byte[] b = new byte[BLOCK_SIZE];
        while( (i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }
}