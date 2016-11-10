package com.jmaker.securecredential.file;

import android.os.Environment;
import android.util.Base64;

import com.jmaker.securecredential.exception.CSVException;
import com.jmaker.securecredential.exception.EncryptionException;
import com.jmaker.securecredential.vo.Credential;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Store credentials in and retrieve Credential from storage.
 * Created by wchan on 8/30/2016.
 */
abstract public class CredentialsFile {

    private static SecretKeySpec secretKeySpec = null;    //call the getSecretKeySpec() method to access the secret key.
    private final static String TRANSFORMATION = "AES";   //the encryption algorithm name used in this application.
    private final static String KEY_FILENAME = "key1";
    private final static String SECRET_KEY_ALIAS = "SecureCredentialSecretKeyAlias";



    /**
     * Read all credentials from storage.
     * @return List of credential objects.
     * @throws CSVException
     */
    public abstract List<Credential> getCredentials() throws CSVException;

    /**
     * Read credential by Id.
     * @param id the credential Id.
     * @return the credential object.
     */
    public abstract Credential getCredential(int id) throws CSVException;

    /**
     * Add a new credential to the credential file.
     * @param credential the new credential.
     * @throws CSVException
     */
    public abstract void addCredential(Credential credential) throws CSVException;

    /**
     * Update the credential in the credential file.
     * @param credential credential with the updated attributes.
     * @throws CSVException
     */
    public abstract void updateCredential(Credential credential) throws CSVException;

    /**
     * Delete credential by id from the credential file.
     * @param id the credential id
     * @throws CSVException
     */
    public abstract void deleteCredential(int id) throws CSVException;

    /**
     * Get the new credential id
     * @return the new credential id
     * @throws CSVException
     */
    protected abstract int getNewId() throws CSVException;

    /**
     * Encode the base 64 bytes array
     * @param plainBytes the plain bytes array to be encoded in base 64 form.
     * @return the base 64 encoded string.
     */
    protected String encode(byte[] plainBytes) {
        return Base64.encodeToString(plainBytes, Base64.NO_WRAP);
    }

    /**
     * Decode the base 64 string.
     * @param encodedText the encoded base 64 string.
     * @return the decoded base 64 bytes array.
     */
    private byte[] decode(String encodedText) {
        return Base64.decode(encodedText, Base64.NO_WRAP);
    }

    /**
     * Get the CSV file
     * @return the CSV file
     */
    private File getKeyFile() {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);
        return new File(path, "/" + KEY_FILENAME);
    }

    /**
     * Get the secret key from the key store.
     * @return the secret key
     * @throws EncryptionException
     */
    private SecretKey getSecretKey() throws EncryptionException {
        SecretKey secretKey = null;

        FileInputStream fis = null;
        try {
            //load the secret key from the keystore.
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = "PLEASE_USE_KEYSTORE_PASSWORD".toCharArray(); //TODO remove this hardcoded keystore password
            fis = new java.io.FileInputStream(getKeyFile());
            ks.load(fis, password);
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) ks.getEntry(SECRET_KEY_ALIAS, protParam);
            secretKey = secretKeyEntry.getSecretKey();
        } catch (Exception e) {
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
            }
        }

        if (secretKey == null) {
            return generateSecretKey();
        } else {
            return secretKey;
        }
    }

    /**
     * Generate a secret key and put it to the key store.
     * @return the generated secret key.
     * @throws EncryptionException
     */
    private SecretKey generateSecretKey() throws EncryptionException {
        FileOutputStream fos = null;

        try {
            //generate the secret key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();

            //save the new secret key to the key store
            char[] password = "PLEASE_USE_KEYSTORE_PASSWORD".toCharArray(); //TODO remove this hardcoded keystore password
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null);
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
            KeyStore.SecretKeyEntry newSecretKeyEntry =
                    new KeyStore.SecretKeyEntry(secretKey);
            ks.setEntry(SECRET_KEY_ALIAS, newSecretKeyEntry, protParam);
            fos = new java.io.FileOutputStream(getKeyFile());
            ks.store(fos, password);

            return secretKey;
        } catch (Exception e) {
            throw new EncryptionException("Failed to generate the key.", e);
        }
    }

    /**
     * Get the secret key.
     * @return the secret key.
     * @throws EncryptionException
     */
    private SecretKeySpec getSecretKeySpec() throws EncryptionException {
        if (secretKeySpec == null) {
            try {
                secretKeySpec = new SecretKeySpec(getSecretKey().getEncoded(), "AES");
            } catch (Exception e) {
                throw new EncryptionException("Failed to get the key.", e);
            }
        }

        return secretKeySpec;
    }

    /**
     * Encrypt a string.
     * @param plainText the string to encrypt.
     * @return cipher text in Base 64 format.
     * @throws EncryptionException
     */
    protected String encrypt(String plainText) throws EncryptionException {
        String base64CipherText = null;

        try {
            byte[] plainBytes = plainText.getBytes();
            String base64Text = encode(plainBytes);

            Cipher c = Cipher.getInstance(TRANSFORMATION);
            c.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
            byte[] base64Bytes = base64Text.getBytes();
            byte[] cipherBytes = c.doFinal(base64Bytes);
            base64CipherText = encode(cipherBytes);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt data.", e);
        }

        return base64CipherText;
    }

    /**
     * Decrypt Base 64 format cipher text.
     * @param base64CipherText the Base 64 format cipher text.
     * @return the plain text.
     * @throws EncryptionException
     */
    protected String decrypt(String base64CipherText) throws EncryptionException {
        String plainText = null;

        try {
            Cipher c = Cipher.getInstance(TRANSFORMATION);
            c.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
            byte[] cipherBytes = decode(base64CipherText);
            byte[] base64PlainBytes = c.doFinal(cipherBytes);
            String base64PlainText = new String(base64PlainBytes);
            byte[] plainBytes = decode(base64PlainText);
            plainText = new String(plainBytes);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt data.", e);
        }

        return plainText;
    }
}