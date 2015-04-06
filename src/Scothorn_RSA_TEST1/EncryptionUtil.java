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

/**
 * @author JavaDigest
 *
 */
public class EncryptionUtil {

    /**
     * String to hold name of the encryption algorithm.
     */
    public final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * String to hold the name of the private key file.
     */
    public final String PRIVATE_KEY_FILE = "./keys/private.key";

    /**
     * String to hold name of the public key file.
     */
    public final String PUBLIC_KEY_FILE = "./keys/public.key";

    private final int BLOCK_SIZE = 48;


    public EncryptionUtil() {
        if (!areKeysPresent()) {
            // Method generates a pair of keys using the RSA algorithm and stores it
            // in their respective files
            generateKey();
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
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(PRIVATE_KEY_FILE);
            File publicKeyFile = new File(PUBLIC_KEY_FILE);

            // Create files to store public and private key
            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
            privateKeyFile.createNewFile();

            if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }
            publicKeyFile.createNewFile();

            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                    new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(
                    new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * The method checks if the pair of public and private key has been generated.
     *
     * @return flag indicating if the pair of keys were generated.
     */
    public boolean areKeysPresent() {

        File privateKey = new File(PRIVATE_KEY_FILE);
        File publicKey = new File(PUBLIC_KEY_FILE);

        if (privateKey.exists() && publicKey.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param text
     *          : original plain text
     * @param key
     *          :The public key
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    /**
     * Decrypt text using private key.
     *
     * @param text
     *          :encrypted text
     * @param key
     *          :The private key
     * @return plain text
     * @throws java.lang.Exception
     */
    public String decrypt(byte[] text, PrivateKey key) {
        byte[] decryptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedText = cipher.doFinal(text);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(decryptedText);
    }

    /*
    ===========================================================================
    functions using files
    ===========================================================================
     */

    public void encrypt(File in, File out, PublicKey key) throws IOException, InvalidKeyException {

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            FileInputStream is = new FileInputStream(in);
            CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), cipher);

            copy(is, os);
            int i;
            byte[] b = new byte[BLOCK_SIZE];
            while( (i = is.read(b)) != -1) {
                os.write(b, 0, i);
//                cipher.update(b);
            }
//            cipher.doFinal();
            is.close();
            os.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public void decrypt(File in, File out, PrivateKey key) throws IOException, InvalidKeyException {

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            CipherInputStream is = new CipherInputStream(new FileInputStream(in), cipher);
            FileOutputStream os = new FileOutputStream(out);

            copy(is, os);
            int i;
            byte[] b = new byte[BLOCK_SIZE];
            while( (i = is.read(b)) != -1) {
                os.write(b, 0, i);
//                cipher.update(b);
            }
//            cipher.doFinal();
            is.close();
            os.close();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }

    private void copy(InputStream is, OutputStream os) throws IOException {

        int i;
        byte[] b = new byte[BLOCK_SIZE];
        while( (i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }
}