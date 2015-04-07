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
     * String to hold the name of the private key file.
     */
    public final String PRIVATE_KEY_FILE = "./keys/private.key";

    /**
     * String to hold name of the public key file.
     */
    public final String PUBLIC_KEY_FILE = "./keys/public.key";

    public final String KEY_FILE =  "./keys/secure.keystore";
    private String passTest = "TeStInG";

    private final int BLOCK_SIZE = 1024;
    private final int AES_KEY_SIZE = 128;

    private byte[] aesKey;

    private Cipher aesCipher;
    private SecretKeySpec aeskeySpec;


    public EncryptionUtil() {

        try {




            KeyStore keyStore = createKeyStore(KEY_FILE, passTest);

            if (keyStore.size() == 0) {
                generateKey();

                // store the secret key
                KeyStore.SecretKeyEntry keyStoreEntry = new KeyStore.SecretKeyEntry(aeskeySpec);
                KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection("pw-secret".toCharArray());
                keyStore.setEntry("aesSecretKey", keyStoreEntry, keyPassword);
                keyStore.store(new FileOutputStream(KEY_FILE), passTest.toCharArray());
            } else {

                KeyStore.Entry entry = keyStore.getEntry("mySecretKey", null);
                aeskeySpec = (SecretKeySpec) entry;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (!areKeysPresent()) {
//            // Method generates a pair of keys using the RSA algorithm and stores it
//            // in their respective files
            generateKey();
//        }
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

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

//        try {
//            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//            keyGen.initialize(1024);
//            final KeyPair key = keyGen.generateKeyPair();
//
//            File privateKeyFile = new File(PRIVATE_KEY_FILE);
//            File publicKeyFile = new File(PUBLIC_KEY_FILE);
//
//            // Create files to store public and private key
//            if (privateKeyFile.getParentFile() != null) {
//                privateKeyFile.getParentFile().mkdirs();
//            }
//            privateKeyFile.createNewFile();
//
//            if (publicKeyFile.getParentFile() != null) {
//                publicKeyFile.getParentFile().mkdirs();
//            }
//            publicKeyFile.createNewFile();
//
//            // Saving the Public key in a file
//            ObjectOutputStream publicKeyOS = new ObjectOutputStream(
//                    new FileOutputStream(publicKeyFile));
//            publicKeyOS.writeObject(key.getPublic());
//            publicKeyOS.close();
//
//            // Saving the Private key in a file
//            ObjectOutputStream privateKeyOS = new ObjectOutputStream(
//                    new FileOutputStream(privateKeyFile));
//            privateKeyOS.writeObject(key.getPrivate());
//            privateKeyOS.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
    private static KeyStore createKeyStore(String fileName, String pw) throws Exception {
        File file = new File(fileName);

        final KeyStore keyStore = KeyStore.getInstance("JCEKS");
        if (file.exists()) {
            // .keystore file already exists => load it
            keyStore.load(new FileInputStream(file), pw.toCharArray());
        } else {
            // .keystore file not created yet => create it
            keyStore.load(null, null);
//            keyStore.store(new FileOutputStream(fileName), pw.toCharArray());
            // store away the keystore
            java.io.FileOutputStream fos = null;
            try {
                fos = new java.io.FileOutputStream("newKeyStoreName");
                keyStore.store(fos, pw.toCharArray());
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }

        return keyStore;
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

//    private void saveKey() throws IOException {
//
//        ObjectOutputStream oout = new ObjectOutputStream(aeskeySpec);
//        try {
//            oout.writeObject(KEY_FILE);
//        } finally {
//            oout.close();
//        }
//
//    }

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