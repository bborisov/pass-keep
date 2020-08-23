package pass.keep.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CipherUtil {

    public static final String ALGORITHM = "AES";

    private static final String ALGORITHM_WITH_MODE_AND_PADDING = ALGORITHM + "/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 128;

    public static byte[] encrypt(byte[] password, byte[] initVector, SecretKey secretKey) {
        return crypt(password, initVector, secretKey, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(byte[] password, byte[] initVector, SecretKey secretKey) {
        return crypt(password, initVector, secretKey, Cipher.DECRYPT_MODE);
    }

    private static byte[] crypt(byte[] password, byte[] initVector, SecretKey secretKey, int cipherMode) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_WITH_MODE_AND_PADDING);
            cipher.init(cipherMode, secretKey, new IvParameterSpec(initVector));

            return cipher.doFinal(password);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getInitVector() {
        byte[] iv = new byte[KEY_LENGTH / 8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    public static SecretKey getSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_LENGTH);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
