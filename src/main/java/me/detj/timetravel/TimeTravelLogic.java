package me.detj.timetravel;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import me.detj.timetravel.coders.crypto.Crypter;
import me.detj.timetravel.dto.*;
import me.detj.timetravel.coders.date.DateCoder;
import me.detj.timetravel.date.DateSource;
import me.detj.timetravel.coders.string.UnknownStringException;
import me.detj.timetravel.coders.string.StringCoder;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class TimeTravelLogic {

    private final int keySize;

    private final DateSource currentDateSource;
    private final DateCoder dateCoder;
    private final StringCoder stringCoder;
    private final Crypter crypter;

    public TimeTravelLogic(DateSource currentDateSource, DateCoder dateCoder, StringCoder stringCoder, Crypter crypter, int keySize) {
        this.currentDateSource = Preconditions.checkNotNull(currentDateSource, "currentDateSource is null");
        this.dateCoder = Preconditions.checkNotNull(dateCoder, "dateCoder is null");
        this.stringCoder = Preconditions.checkNotNull(stringCoder, "wordCoder is null");
        this.crypter = Preconditions.checkNotNull(crypter, "crypto is null");

        Preconditions.checkArgument(keySize > 0, "keySize must be positive");
        this.keySize = keySize;
    }

    public List<String> todaysWords() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return getWords(currentDateSource.get());
    }

    public Date today() {
        return Date.fromLocalDate(currentDateSource.get());
    }

    public List<String> getWords(LocalDate date) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] todayBytes = dateCoder.fromDate(date);
        byte[] padded = Arrays.copyOf(todayBytes, keySize);
        byte[] encrypted = crypter.encrypt(padded);
        return stringCoder.toStrings(encrypted);
    }

    public LocalDate getDate(List<String> words) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnknownStringException {
        byte[] bytes = stringCoder.toBytes(words);
        byte[] paddingRemoved = Arrays.copyOf(bytes, keySize);
        byte[] decrypted = crypter.decrypt(paddingRemoved);
        return dateCoder.toDate(decrypted);
    }

    public boolean isPresent(LocalDate date) {
        return date.equals(currentDateSource.get());
    }

    public Result checkWords(List<String> words) {
        try {
            return buildResult(getDate(words));
        } catch (Exception e) {
            return buildFailureResult(e);
        }
    }

    public Page<PastStringsEntry> getPastWords(PastStringsRequest request) {
        int daysStart = request.getPage() * request.getPerPage() + 1;
        int daysEnd = daysStart + request.getPerPage();
        List<PastStringsEntry> entries = IntStream.range(daysStart, daysEnd)
                .mapToObj(day -> {
                    try {
                        LocalDate date = currentDateSource.get().minusDays(day);
                        return new PastStringsEntry(Date.fromLocalDate(date), getWords(date));
                    } catch (Exception e) {
                        throw new RuntimeException("Error building page", e);
                    }
                })
                .collect(ImmutableList.toImmutableList());
        return new Page(request.getPage(), entries);
    }

    private Result buildFailureResult(Exception e) {
        return new Result(ResultStatus.ERROR, null);
    }

    private Result buildResult(LocalDate date) {
        if (isPresent(date)) {
            return new Result(ResultStatus.PRESENT, Date.fromLocalDate(date));
        } else {
            return new Result(
                    date.isAfter(currentDateSource.get()) ? ResultStatus.FUTURE : ResultStatus.PAST,
                    Date.fromLocalDate(date));
        }
    }
}
