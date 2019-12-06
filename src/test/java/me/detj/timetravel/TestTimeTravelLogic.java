package me.detj.timetravel;

import com.google.common.collect.ImmutableList;
import me.detj.timetravel.coders.crypto.Crypter;
import me.detj.timetravel.dto.*;
import me.detj.timetravel.coders.date.DateCoder;
import me.detj.timetravel.date.DateSource;
import me.detj.timetravel.coders.string.UnknownStringException;
import me.detj.timetravel.coders.string.StringCoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

public class TestTimeTravelLogic {

    @Test
    public void testGetPresentDate() throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, UnknownStringException {
        LocalDate date = LocalDate.of(2000, 1, 1);

        DateSource dateSource = () -> date;
        TimeTravelLogic timeTravelLogic = new TimeTravelLogic(dateSource,
                DateCoder.getInstance(),
                TimeTravelTestInstances.stringCoder(),
                TimeTravelTestInstances.crypter(),
                16);

        List<String> words = timeTravelLogic.todaysWords();

        Assertions.assertEquals(date, timeTravelLogic.getDate(words));
    }

    @Test
    public void testCheckPresentDate() throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, UnknownStringException {
        DateCoder dateCoder = DateCoder.getInstance();
        StringCoder stringCoder = TimeTravelTestInstances.stringCoder();
        Crypter crypter = TimeTravelTestInstances.crypter();

        LocalDate presentDate = LocalDate.of(2000, 1, 1);
        TimeTravelLogic timeTravelLogic = new TimeTravelLogic(() -> presentDate, dateCoder, stringCoder, crypter, 16);

        //words for 2000-1-1
        List<String> words = timeTravelLogic.todaysWords();
        Result result = timeTravelLogic.checkWords(words);

        Assertions.assertEquals(Date.fromLocalDate(presentDate), result.getDate());
        Assertions.assertEquals(ResultStatus.PRESENT, result.getStatus());
    }

    @Test
    public void testCheckPastDate() throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, UnknownStringException {
        DateCoder dateCoder = DateCoder.getInstance();
        StringCoder stringCoder = TimeTravelTestInstances.stringCoder();
        Crypter crypter = TimeTravelTestInstances.crypter();

        LocalDate presentDate = LocalDate.of(2000, 1, 1);
        TimeTravelLogic timeTravelLogic = new TimeTravelLogic(() -> presentDate, dateCoder, stringCoder, crypter, 16);

        //words for 2000-1-1
        List<String> words = timeTravelLogic.todaysWords();

        //create new timeTravelLogic with the date set to 2000-1-2
        LocalDate futureDate = LocalDate.of(2000, 1, 2);
        timeTravelLogic = new TimeTravelLogic(() -> futureDate, dateCoder, stringCoder, crypter, 16);

        Result result = timeTravelLogic.checkWords(words);

        Assertions.assertEquals(Date.fromLocalDate(presentDate), result.getDate());
        Assertions.assertEquals(ResultStatus.PAST, result.getStatus());
    }

    @Test
    public void testCheckFutureDate() throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, UnknownStringException {
        DateCoder dateCoder = DateCoder.getInstance();
        StringCoder stringCoder = TimeTravelTestInstances.stringCoder();
        Crypter crypter = TimeTravelTestInstances.crypter();

        LocalDate presentDate = LocalDate.of(2000, 1, 2);
        TimeTravelLogic timeTravelLogic = new TimeTravelLogic(() -> presentDate, dateCoder, stringCoder, crypter, 16);

        //words for 2000-1-2
        List<String> words = timeTravelLogic.todaysWords();

        //create new timeTravelLogic with the date set to 2000-1-1
        LocalDate pastDate = LocalDate.of(2000, 1, 1);
        timeTravelLogic = new TimeTravelLogic(() -> pastDate, dateCoder, stringCoder, crypter, 16);

        Result result = timeTravelLogic.checkWords(words);

        Assertions.assertEquals(Date.fromLocalDate(presentDate), result.getDate());
        Assertions.assertEquals(ResultStatus.FUTURE, result.getStatus());
    }

    @Test
    public void testCheckInvalidWords() throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, UnknownStringException {
        TimeTravelLogic timeTravelLogic = TimeTravelTestInstances.timeTravelLogic();

        //Check invalid words
        List<String> wordsNotInDictionary = ImmutableList.of("word1", "word2", "word3");

        Result result = timeTravelLogic.checkWords(wordsNotInDictionary);

        Assertions.assertNull(result.getDate());
        Assertions.assertEquals(ResultStatus.ERROR, result.getStatus());


        //Check valid words but not a valid order
        List<String> wordsInvalidOrder = ImmutableList.of("1","2","3","4","5","6","7","8","9","10","11","12");

        result = timeTravelLogic.checkWords(wordsInvalidOrder);

        Assertions.assertNull(result.getDate());
        Assertions.assertEquals(ResultStatus.ERROR, result.getStatus());
    }

    @Test
    void testPastWords() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        TimeTravelLogic timeTravelLogic = TimeTravelTestInstances.timeTravelLogic();

        //page 1 with 10 per page
        Page<PastStringsEntry> page = timeTravelLogic.getPastWords(new PastStringsRequest(10, 0));

        Assertions.assertEquals(0, page.getPageNum());
        Assertions.assertEquals(10, page.getSize());

        List<PastStringsEntry> entries = page.getItems();
        Assertions.assertEquals(10, entries.size());

        validateEntry(entries.get(0), new Date(1999, 12, 31), timeTravelLogic);
        validateEntry(entries.get(1), new Date(1999, 12, 30), timeTravelLogic);
        validateEntry(entries.get(2), new Date(1999, 12, 29), timeTravelLogic);
        validateEntry(entries.get(3), new Date(1999, 12, 28), timeTravelLogic);
        validateEntry(entries.get(4), new Date(1999, 12, 27), timeTravelLogic);
        validateEntry(entries.get(5), new Date(1999, 12, 26), timeTravelLogic);
        validateEntry(entries.get(6), new Date(1999, 12, 25), timeTravelLogic);
        validateEntry(entries.get(7), new Date(1999, 12, 24), timeTravelLogic);
        validateEntry(entries.get(8), new Date(1999, 12, 23), timeTravelLogic);
        validateEntry(entries.get(9), new Date(1999, 12, 22), timeTravelLogic);


        //page 2 with 15 per page
        page = timeTravelLogic.getPastWords(new PastStringsRequest(15, 1));

        Assertions.assertEquals(1, page.getPageNum());
        Assertions.assertEquals(15, page.getSize());

        entries = page.getItems();
        Assertions.assertEquals(15, entries.size());

        validateEntry(entries.get(0), new Date(1999, 12, 16), timeTravelLogic);
        validateEntry(entries.get(1), new Date(1999, 12, 15), timeTravelLogic);
        validateEntry(entries.get(2), new Date(1999, 12, 14), timeTravelLogic);
        validateEntry(entries.get(3), new Date(1999, 12, 13), timeTravelLogic);
        validateEntry(entries.get(4), new Date(1999, 12, 12), timeTravelLogic);
        validateEntry(entries.get(5), new Date(1999, 12, 11), timeTravelLogic);
        validateEntry(entries.get(6), new Date(1999, 12, 10), timeTravelLogic);
        validateEntry(entries.get(7), new Date(1999, 12, 9), timeTravelLogic);
        validateEntry(entries.get(8), new Date(1999, 12, 8), timeTravelLogic);
        validateEntry(entries.get(9), new Date(1999, 12, 7), timeTravelLogic);
        validateEntry(entries.get(10), new Date(1999, 12, 6), timeTravelLogic);
        validateEntry(entries.get(11), new Date(1999, 12, 5), timeTravelLogic);
        validateEntry(entries.get(12), new Date(1999, 12, 4), timeTravelLogic);
        validateEntry(entries.get(13), new Date(1999, 12, 3), timeTravelLogic);
        validateEntry(entries.get(14), new Date(1999, 12, 2), timeTravelLogic);
    }

    private static void validateEntry(PastStringsEntry entry, Date expectedDate, TimeTravelLogic timeTravelLogic) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        Assertions.assertEquals(expectedDate, entry.getDate());
        Assertions.assertLinesMatch(timeTravelLogic.getWords(expectedDate.toLocalDate()), entry.getStrings());
    }
}
