package com.delta.mobileplatform.util;

import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
    private static final String ALGORITHM = "AES";
    private static final String MODE = "AES/ECB/PKCS5Padding";
    private static final String ENCODING = "UTF-8";
    private static final String IV = "79C31CDE124B8E0F6CA195306EDD8F94";
    private static final String SALT = "D131900D16924F7CBB4C394AFA45967E";
    private static final String ISSUER = "DIAWorks.AuthServer";

    // 加密
    public static String encrypt(String plainText) throws Exception {
        byte[] keyBytes = generateKey(ISSUER, SALT.getBytes(StandardCharsets.UTF_8), 1000, 128);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(ENCODING));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 生成密鑰
    private static byte[] generateKey(String passphrase, byte[] salt, int iterations, int keySize) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, keySize);
        return factory.generateSecret(spec).getEncoded();
    }
}