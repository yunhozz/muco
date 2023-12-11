package com.muco.configserver;

import com.muco.configserver.config.JasyptConfig;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class JasyptConfigTest {

    @Test
    void privateKey_encrypt() throws IOException {
        ClassPathResource resource = new ClassPathResource("private-key.txt");
        String privateKey = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setAlgorithm(JasyptConfig.ALGORITHM);
        standardPBEStringEncryptor.setPassword(JasyptConfig.KEY);

        String enc = standardPBEStringEncryptor.encrypt(privateKey);
        System.out.printf("enc :\nENC(%s)\n", enc);
        System.out.printf("dec :\n%s\n", standardPBEStringEncryptor.decrypt(enc));
    }

    @Test
    void string_encrypt() {
        String privateKey = "myKey";
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setAlgorithm(JasyptConfig.ALGORITHM);
        standardPBEStringEncryptor.setPassword(JasyptConfig.KEY);

        String enc = standardPBEStringEncryptor.encrypt(privateKey);
        System.out.printf("enc :\nENC(%s)\n", enc);
        System.out.printf("dec :\n%s\n", standardPBEStringEncryptor.decrypt(enc));
    }
}