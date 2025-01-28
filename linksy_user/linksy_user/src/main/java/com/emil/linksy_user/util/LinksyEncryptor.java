package com.emil.linksy_user.util;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LinksyEncryptor {

    private final BasicTextEncryptor encryptor;


    public LinksyEncryptor(@Value("${jasypt.encryptor.password}") String key) {
        encryptor = new BasicTextEncryptor();
        encryptor.setPassword(key);
    }

    public String encrypt(String value) {
        if (value==null) return null;
        try {
            return encryptor.encrypt(value);
        } catch (Exception e) {
            throw new RuntimeException("Error when encrypting text", e);
        }
    }

    public String decrypt(String value) {
        if (value==null) return null;
        try {
            return encryptor.decrypt(value);
        } catch (Exception e) {
            System.out.println("исключение:" + value);
            throw new RuntimeException("Error in decoding the text", e);
        }
    }
}