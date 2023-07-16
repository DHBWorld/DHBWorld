package com.main.dhbworld.Backup;

import androidx.annotation.NonNull;

import com.main.dhbworld.Backup.BackupHandler.BackupHandler;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class BackupCipher {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String CIPHER_ALGORITHM = "AES";
    private static final int KEY_ITEMCOUNT = 65536;
    private static final int KEY_LENGTH = 256;

    public static InputStream decrypt(String password, InputStream in) throws Exception {
        String file = readFile(in);

        String[] data = file.split("__:__");
        byte[] encryptedData = Base64.getDecoder().decode(data[0]);
        byte[] iv = Base64.getDecoder().decode(data[1]);
        byte[] salt = Base64.getDecoder().decode(data[2]);

        Cipher cipher = initializeDecryptCipher(password, iv, salt);
        ByteArrayInputStream bis = new ByteArrayInputStream(encryptedData);
        return new CipherInputStream(bis, cipher);
    }

    private static String readFile(InputStream in) throws IOException {
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
        return file;
    }

    @NonNull
    private static Cipher initializeDecryptCipher(String password, byte[] iv, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, KEY_ITEMCOUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), CIPHER_ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec paramSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        return cipher;
    }

    public static void encrypt(String password, InputStream in, OutputStream out) throws Exception {
        byte[] salt = generateSalt();

        Cipher cipher = initializeEncryptCipher(password, salt);
        byte[] iv = cipher.getIV();
        String data64 = encryptData(in, cipher);
        formatEncryptedData(out, salt, iv, data64);
    }

    private static byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[8];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private static Cipher initializeEncryptCipher(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, KEY_ITEMCOUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), CIPHER_ALGORITHM);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    private static String encryptData(InputStream in, Cipher cipher) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cos = new CipherOutputStream(outputStream, cipher);
        IOUtils.copy(in, cos);

        cos.flush();
        cos.close();
        outputStream.flush();

        byte[] data = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(data);
    }

    private static void formatEncryptedData(OutputStream out, byte[] salt, byte[] iv, String data64) throws IOException {
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
