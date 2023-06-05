package com.main.dhbworld.Backup;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class BackupCipher {

    protected static InputStream decrypt(String password, InputStream in) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        reader.lines().forEach(stringBuilder::append);
        reader.close();
        String file = stringBuilder.toString();
        byte[] fileBytes = file.getBytes(StandardCharsets.UTF_8);
        byte[] magicnumber = Arrays.copyOfRange(fileBytes, 0, 4);

        if (Arrays.equals(magicnumber, BackupHandler.ENC_MAGIC_NUMBER)) {
            file = new String(Arrays.copyOfRange(fileBytes, 4, fileBytes.length));
        }

        String[] data = file.split("__:__");
        byte[] encryptedData = Base64.getDecoder().decode(data[0]);
        byte[] iv = Base64.getDecoder().decode(data[1]);
        byte[] salt = Base64.getDecoder().decode(data[2]);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec paramSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);

        ByteArrayInputStream bis = new ByteArrayInputStream(encryptedData);

        return new CipherInputStream(bis, cipher);
    }

    protected static void encrypt(String password, InputStream in, OutputStream out) throws Exception {

        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[8];
        secureRandom.nextBytes(salt);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] iv = cipher.getIV();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        CipherOutputStream cos = new CipherOutputStream(outputStream, cipher);

        IOUtils.copy(in, cos);

        cos.flush();
        cos.close();
        outputStream.flush();

        byte[] data = outputStream.toByteArray();
        String data64 = Base64.getEncoder().encodeToString(data);

        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        concat.write(data64.getBytes(StandardCharsets.UTF_8));
        concat.write("__:__".getBytes(StandardCharsets.UTF_8));
        concat.write(Base64.getEncoder().encode(iv));
        concat.write("__:__".getBytes(StandardCharsets.UTF_8));
        concat.write(Base64.getEncoder().encode(salt));
        concat.flush();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(concat.toByteArray());

        IOUtils.copy(inputStream, out);

        concat.close();
        inputStream.close();
        out.close();

    }
}
