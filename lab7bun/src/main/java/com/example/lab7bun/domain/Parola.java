package com.example.lab7bun.domain;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Parola {

        public static byte[] generateSalt() {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            return salt;
        }

        public static byte[] generateSaltPentruId(Long id){
            String combined = "parola" + id;
            return combined.getBytes(StandardCharsets.UTF_8);
        }

        public static String hashPassword(String password, byte[] salt) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt);
                byte[] hashedPassword = md.digest(password.getBytes());

                // Convertim rezultatul într-un șir de caractere hexazecimale
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : hashedPassword) {
                    stringBuilder.append(String.format("%02x", b));
                }

                return stringBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
}
