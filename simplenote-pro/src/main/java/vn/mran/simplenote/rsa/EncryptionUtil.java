package vn.mran.simplenote.rsa;

import android.util.Base64;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import vn.mran.simplenote.util.DataUtil;

/**
 * @author JavaDigest
 */
public class EncryptionUtil {

    /**
     * String to hold name of the encryption algorithm.
     */
    public static final String ALGORITHM = "RSA";

    /**
     * String to hold the name of the private key file.
     */
    public static final String PRIVATE_KEY_FILE = "private.key";

    /**
     * String to hold name of the public key file.
     */
    public static final String PUBLIC_KEY_FILE = "public.key";
    private RSAPublicKey pubKey;
    private RSAPrivateKey privKey;

    public EncryptionUtil() {
        generateKey();
    }

    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes. Store the set of keys in Prvate.key and Public.key files.
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws FileNotFoundException
     */


    public void generateKey() {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA", "BC");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
                    new BigInteger("d46f473a2d746537de2056ae3092c451", 16),
                    new BigInteger("11", 16));
            RSAPrivateKeySpec privKeySpec = new RSAPrivateKeySpec(
                    new BigInteger("d46f473a2d746537de2056ae3092c451", 16),
                    new BigInteger("57791d5430d593164082036ad8b29fb1", 16));

            pubKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
            privKey = (RSAPrivateKey) keyFactory.generatePrivate(privKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param text : original plain text
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public String encrypt(String text) {
        String encryptData = "";
        try {
            byte[] data = text.getBytes("utf-8");
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encryptData = Base64.encodeToString(cipher.doFinal(data), Base64.DEFAULT);
            Log.d(DataUtil.TAG_BASE, "Encrypt success : " + encryptData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptData;
    }

    /**
     * Decrypt text using private key.
     *
     * @param text :encrypted text
     * @return plain text
     * @throws java.lang.Exception
     */
    public String decrypt(String text) {
        String decryptData = "";
        try {
            byte[] data = Base64.decode(text, Base64.DEFAULT);
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            decryptData = new String(cipher.doFinal(data), "utf-8");
            Log.d(DataUtil.TAG_BASE, "Decrypt success : " + decryptData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return decryptData;
    }
}