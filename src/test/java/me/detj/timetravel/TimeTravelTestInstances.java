package me.detj.timetravel;

import me.detj.timetravel.coders.crypto.Crypter;
import me.detj.timetravel.coders.crypto.NoOpCrypter;
import me.detj.timetravel.coders.date.DateCoder;
import me.detj.timetravel.date.DateSource;
import me.detj.timetravel.coders.string.StringCoder;

import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeTravelTestInstances {

    private static final StringCoder stringCoder = new StringCoder(IntStream.range(0, 4096).mapToObj(String::valueOf).collect(Collectors.toList()));
    private static final Crypter crypter = new Crypter(new SecretKeySpec(new byte[16], Crypter.KEY_ALGORITHM));
    private static final Crypter noOpCrypter = new NoOpCrypter();
    private static final DateSource dateSource = () -> LocalDate.of(2000, 1, 1);
    private static final TimeTravelLogic timeTravelLogic =  new TimeTravelLogic(dateSource, DateCoder.getInstance(), stringCoder, crypter, 16);

    public static Crypter crypter() {
        return crypter;
    }

    public static Crypter noOpCrypter() {
        return noOpCrypter;
    }

    public static DateSource dateSource() {
        return dateSource;
    }

    public static StringCoder stringCoder() {
        return stringCoder;
    }

    public static TimeTravelLogic timeTravelLogic() {
        return timeTravelLogic;
    }
}
