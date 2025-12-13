package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Utils {
    
    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedBytes = md.digest(password.getBytes());
            
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);
            
            return saltBase64 + ":" + hashBase64;
            
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
        public static boolean checkPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            String saltBase64 = parts[0];
            String originalHash = parts[1];
            
            byte[] salt = Base64.getDecoder().decode(saltBase64);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedBytes = md.digest(password.getBytes());
            String newHash = Base64.getEncoder().encodeToString(hashedBytes);

            return originalHash.equals(newHash);
            
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}