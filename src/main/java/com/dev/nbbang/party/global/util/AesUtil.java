package com.dev.nbbang.party.global.util;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Component
@RequiredArgsConstructor
public class AesUtil {
    @Value("${aes.key}")
    private String key;

    public String encrypt(String text) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(key.substring(0, 16).getBytes("UTF-8")));
            return new String(Base64.encodeBase64(c.doFinal(text.getBytes("UTF-8"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String cipherText) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(key.substring(0, 16).getBytes("UTF-8")));
            return new String(c.doFinal(Base64.decodeBase64(cipherText.getBytes("UTF-8"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
