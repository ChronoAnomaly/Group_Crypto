package Scothorn_RSA_TEST1;

import java.math.BigInteger;
import java.io.*;
/**
 * Code originally from  https://github.com/rhgrant10/RSA
 * Author: Rob Grant
 * retrieved on 2015/2/10
 */

/**
 * Class representing a private RSA key.
 * 
 * @author Rob 
 * @version 05/31/2010
 */
public class RSAPrivateKey extends RSAKey
{
    /** The private exponent. */
    private BigInteger d;    

    /** Default constructor. */
    public RSAPrivateKey() {
        super();
        setPriExp(null);
        return;
    }
    
    /** Main constructor. */
    public RSAPrivateKey(BigInteger modulus, BigInteger priExp) {
        super(modulus);
        setPriExp(priExp);
        return;
    }

    /** Performs the classical RSA computation. */
    protected BigInteger decrypt(BigInteger c) {
        return c.modPow(getPriExp(), getModulus());
    }
    
    /** Extracts the data portion of the byte array. */
    protected byte[] extractData(byte[] EB) {
        if (EB.length < 12 || EB[0] != 0x00 || EB[1] != 0x02) {
            return null;
        }
        int index = 2;
        do {} while (EB[index++] != 0x00);
        
        return getSubArray(EB, index, EB.length);
    }
    
    /** Returns the private exponent. */
    public BigInteger getPriExp() {
        return d;
    }
    
    /** Sets the private exponent. */
    public void setPriExp(BigInteger priExp)
    {
        d = weedOut(priExp);
        return;
    }
    
    /** Uses key and returns true if decryption was successful. */
    public boolean use(String source, String destination) {
        byte[] sourceBytes = getBytes(source);
        if (isNull(sourceBytes)) {
            return false;
        }
        /* TODO Read through and find out how it works and see how it interacts with the reshape method. */
        int k = getModulusByteSize();
        BigInteger c, m;
        byte[] EB, M;
        byte[][] C = reshape(sourceBytes, k);
        BufferedOutputStream out = null;
        
        try {
            out = new BufferedOutputStream(new FileOutputStream(destination));
            for (int i = 0; i < C.length; i++) {
                if (C[i].length != k) return false;
                c = new BigInteger(C[i]);
                m = decrypt(c);
                EB = toByteArray(m, k);
                M = extractData(EB);
                out.write(M);
            }
            out.close();
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (isNull(out)) out.close();
            } catch (IOException e) {
                return false;
            }
        }
        
        return true;
    }
}
