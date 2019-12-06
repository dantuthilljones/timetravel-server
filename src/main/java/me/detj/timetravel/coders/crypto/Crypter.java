package me.detj.timetravel.coders.crypto;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class Crypter {

    public static final String CRYPTO_ALGORITHM = "AES/ECB/NoPadding";
    public static final String KEY_ALGORITHM = "AES";
    private final SecretKeySpec keySpec;

    public Crypter(SecretKeySpec keySpec) {
        this.keySpec = Preconditions.checkNotNull(keySpec, "keySpec is null");
        Preconditions.checkArgument(
                keySpec.getAlgorithm().equals(KEY_ALGORITHM),
                String.format("keySpec's algorithm is '%s' but should be '%s'", keySpec.getAlgorithm(), KEY_ALGORITHM));
    }

    public byte[] encrypt(byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(bytes);
    }

    public byte[] decrypt(byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(bytes);
    }
}
