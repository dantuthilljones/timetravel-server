package me.detj.timetravel.coders.crypto;

import me.detj.timetravel.TimeTravelTestInstances;
import me.detj.timetravel.coders.crypto.Crypter;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TestCrypter {

    @Test
    public void testCrypto() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Crypter crypter = TimeTravelTestInstances.crypter();

        //must be 16 bytes long
        String testString = "testString123456";

        byte[] encrypted = crypter.encrypt(testString.getBytes());
        byte[] decrypted = crypter.decrypt(encrypted);

        Assert.assertEquals(testString, new String(decrypted));
    }
}
