package vn.mran.simplenote.rsa;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.FileUtil;

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

    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes. Store the set of keys in Prvate.key and Public.key files.
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws FileNotFoundException
     */

    public void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            FileUtil privateKeyFile = new FileUtil(PRIVATE_KEY_FILE, Constant.DATA_FOLDER);
            FileUtil publicKeyFile = new FileUtil(PUBLIC_KEY_FILE, Constant.DATA_FOLDER);

            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                    new FileOutputStream(publicKeyFile.get()));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(
                    new FileOutputStream(privateKeyFile.get()));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();
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
        byte[] cipherText = null;
        FileInputStream fileInputStream = null;
        ObjectInputStream inputStream = null;
        try {
            // Encrypt the string using the public key

            fileInputStream = new FileInputStream(new FileUtil(PUBLIC_KEY_FILE, Constant.DATA_FOLDER).get());
            inputStream = new ObjectInputStream(fileInputStream);
            final PublicKey publicKey = (PublicKey) inputStream.readObject();
            inputStream.close();
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }

    /**
     * Decrypt text using private key.
     *
     * @param text :encrypted text
     * @return plain text
     * @throws java.lang.Exception
     */
    public String decrypt(String text) {
        byte[] dectyptedText = null;
        FileInputStream fileInputStream = null;
        ObjectInputStream inputStream = null;
        try {
            fileInputStream = new FileInputStream(new FileUtil(PRIVATE_KEY_FILE, Constant.DATA_FOLDER).get());
            inputStream = new ObjectInputStream(fileInputStream);
            final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            inputStream.close();
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            dectyptedText = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
            new String(dectyptedText);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}