package me.detj.timetravel.coders.string;

import com.google.common.collect.ImmutableList;
import me.detj.timetravel.TimeTravelTestInstances;
import me.detj.timetravel.coders.string.UnknownStringException;
import me.detj.timetravel.coders.string.StringCoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import java.util.List;

public class TestStringCoder {

    @Test
    public void testEncode() {
        StringCoder coder = TimeTravelTestInstances.stringCoder();

        //test empty
        Assertions.assertLinesMatch(ImmutableList.of(), coder.toStrings(new byte[0]));

        //test 0s
        List<String> zeros = coder.toStrings(new byte[]{0});
        Assertions.assertLinesMatch(ImmutableList.of("0"), zeros);

        zeros = coder.toStrings(new byte[]{0, 0});
        Assertions.assertLinesMatch(ImmutableList.of("0", "0"), zeros);

        zeros = coder.toStrings(new byte[]{0, 0, 0});
        Assertions.assertLinesMatch(ImmutableList.of("0", "0"), zeros);

        zeros = coder.toStrings(new byte[]{0, 0, 0, 0});
        Assertions.assertLinesMatch(ImmutableList.of("0", "0", "0"), zeros);

        zeros = coder.toStrings(new byte[]{0, 0, 0, 0, 0});
        Assertions.assertLinesMatch(ImmutableList.of("0", "0", "0", "0"), zeros);

        zeros = coder.toStrings(new byte[]{0, 0, 0, 0, 0, 0});
        Assertions.assertLinesMatch(ImmutableList.of("0", "0", "0", "0"), zeros);


        zeros = coder.toStrings(new byte[]{0, 0, 0, 0, 0, 0, 0});
        Assertions.assertLinesMatch(ImmutableList.of("0", "0", "0", "0", "0"), zeros);

        List<String> max = coder.toStrings(new byte[]{-1, -1, -1, -1, -1, -1});
        Assertions.assertLinesMatch(ImmutableList.of("4095", "4095", "4095", "4095"), max);

        List<String> one0one0etc = coder.toStrings(new byte[]{85, 85, 0});
        Assertions.assertLinesMatch(ImmutableList.of("1365", "1280"), one0one0etc);
    }

    @Test
    public void testDecode() throws UnknownStringException, BadPaddingException {
        StringCoder coder = TimeTravelTestInstances.stringCoder();

        //test empty
        Assertions.assertArrayEquals(new byte[0], coder.toBytes(ImmutableList.of()));

        //test 0s
        Assertions.assertArrayEquals(new byte[]{0}, coder.toBytes(ImmutableList.of("0")));

        Assertions.assertArrayEquals(new byte[]{0, 0, 0}, coder.toBytes(ImmutableList.of("0", "0")));

        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 0}, coder.toBytes(ImmutableList.of("0", "0", "0")));

        Assertions.assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0}, coder.toBytes(ImmutableList.of("0", "0", "0", "0")));

        Assertions.assertArrayEquals(new byte[]{-1, -1, -1, -1, -1, -1}, coder.toBytes(ImmutableList.of("4095", "4095", "4095", "4095")));

        Assertions.assertArrayEquals(new byte[]{85, 85, 0}, coder.toBytes(ImmutableList.of("1365", "1280")));
    }


    @Test
    public void testEncodeDecode() throws UnknownStringException, BadPaddingException {
        StringCoder coder = TimeTravelTestInstances.stringCoder();

        byte[] input = new byte[]{0, 0, 0};
        Assertions.assertArrayEquals(input, coder.toBytes(coder.toStrings(input)));

        input = new byte[]{1, 2, 3};
        Assertions.assertArrayEquals(input, coder.toBytes(coder.toStrings(input)));

        input = new byte[]{1, 2, 3, 4};
        Assertions.assertArrayEquals(input, coder.toBytes(coder.toStrings(input)));

        input = new byte[]{1, 2, 3, 4, 6};
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4, 6, 0}, coder.toBytes(coder.toStrings(input)));

        input = new byte[]{1, 2, 3, 4, 5, 6};
        Assertions.assertArrayEquals(input, coder.toBytes(coder.toStrings(input)));

        input = new byte[]{1, 2, 3, 4, 5, 6, 7};
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7}, coder.toBytes(coder.toStrings(input)));

        input = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0}, coder.toBytes(coder.toStrings(input)));
    }
}
