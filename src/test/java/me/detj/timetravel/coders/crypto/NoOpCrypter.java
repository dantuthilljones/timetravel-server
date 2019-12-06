package me.detj.timetravel.coders.crypto;

import javax.crypto.spec.SecretKeySpec;

public class NoOpCrypter extends Crypter {

    public NoOpCrypter() {
        super(new SecretKeySpec(new byte[16], Crypter.KEY_ALGORITHM));
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        return bytes;
    }
}
