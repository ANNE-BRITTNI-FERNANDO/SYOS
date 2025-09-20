package syos.test;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Debug utility to test password hashing and verification
 */
public class PasswordDebug {
    
    public static void main(String[] args) {
        try {
            String password = "Customer3*";
            
            // Generate salt like in AuthenticationService
            String salt = generateSalt();
            System.out.println("Generated salt: " + salt);
            
            // Hash password like in AuthenticationService  
            String hashedPassword = hashPassword(password, salt);
            System.out.println("Hashed password: " + hashedPassword);
            
            // Verify password like in AuthenticationService
            boolean isValid = verifyPassword(password, hashedPassword, salt);
            System.out.println("Password verification: " + isValid);
            
            // Test with wrong password
            boolean isInvalid = verifyPassword("WrongPassword", hashedPassword, salt);
            System.out.println("Wrong password verification: " + isInvalid);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    private static String hashPassword(String password, String salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // Simply concatenate password and salt string, then hash
        String combined = password + salt;
        byte[] hashedPassword = md.digest(combined.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    
    private static boolean verifyPassword(String password, String hashedPassword, String salt) throws Exception {
        String newHash = hashPassword(password, salt);
        return newHash.equals(hashedPassword);
    }
}