package com.sighthunt.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.sighthunt.data.Metadata;
import org.apache.geronimo.mail.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;


public class EncryptUtils {

    private static final String KEY = "F696E6D52DFE6DCF8B100A0D80FD7D23";
    // token is valid for 100 days
    private static final long VALID_TIME = 100 * 24 * 60 * 60 * 1000;
    private static final String PREFIX = "SightHunt";

    private Cipher mEncryptor;
    private Cipher mDecrytor;
    private static EncryptUtils mInstance;

    public static EncryptUtils getInstance() {
        if (mInstance == null)
            mInstance = new EncryptUtils();

        return mInstance;
    }

    private EncryptUtils() {
        try {

            Key key = new SecretKeySpec(KEY.getBytes(), "AES");
            mEncryptor = Cipher.getInstance("AES");
            mDecrytor = Cipher.getInstance("AES");
            mEncryptor.init(Cipher.ENCRYPT_MODE, key);
            mDecrytor.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String enc(String text) {
        try {
            return new String(Base64.encode(mEncryptor.doFinal(text.getBytes("UTF8"))), "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String dec(String text) {
        try {
            return new String(mDecrytor.doFinal(Base64.decode(text.getBytes("UTF8"))), "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String generateTokenPlainString(String username) {
        final Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        long expiry = new Date().getTime() + VALID_TIME;
        return PREFIX + ":" + username + ":" + expiry + ":" + salt;
    }

    public String getToken(String username) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity result = DBHelper.getUserByUsername(username, datastore);
        if (result != null) {
            Object token = result.getProperty(Metadata.User.TOKEN);
            if (token != null) {
                return token.toString();
            }
        }
        return "";
    }

    public String generateAndStoreToken(String username) {
        String plainString = generateTokenPlainString(username);
        String token = enc(plainString);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity result = DBHelper.getUserByUsername(username, datastore);
        if (result != null) {
            result.setProperty(Metadata.User.TOKEN, token);
            datastore.put(result);
        }
        return token;
    }

    private boolean isTokenPlainStringValid(String string) {
        String[] strings = string.split(":");
        if (strings.length != 3) return false;
        if (!PREFIX.equals(strings[0])) return false;

        long expiry = Long.parseLong(strings[2]);
        long now = new Date().getTime();
        if (expiry > now) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTokenValid(String username, String token) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity result = DBHelper.getUserByUsername(username, datastore);
        if (result != null) {
            if (token.equals(result.getProperty(Metadata.User.TOKEN))) {
                return true;
            }
        }
        // TODO: check if token expired

        return false;
    }
}
