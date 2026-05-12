package ufzdev.HealthTrack.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    // LLave maestra para encriptación (En producción esto debería venir de una variable de entorno)
    private static final String MASTER_KEY = "HealthTrackKey2026_Secure_IA_API"; 
    private static final String ALGORITHM = "AES";

    public static String encrypt(String data) throws Exception {
        if (data == null || data.isEmpty()) return "";
        SecretKeySpec secretKey = new SecretKeySpec(fixKey(MASTER_KEY), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData) throws Exception {
        if (encryptedData == null || encryptedData.isEmpty()) return "";
        SecretKeySpec secretKey = new SecretKeySpec(fixKey(MASTER_KEY), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    private static byte[] fixKey(String key) {
        byte[] keyBytes = key.getBytes();
        byte[] fixedKey = new byte[16]; // AES-128
        System.arraycopy(keyBytes, 0, fixedKey, 0, Math.min(keyBytes.length, 16));
        return fixedKey;
    }
}
