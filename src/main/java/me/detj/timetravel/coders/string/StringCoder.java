package me.detj.timetravel.coders.string;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Bytes;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/*
 * StringCoder converts bits to strings using a dictionary. This implementation uses a dictionary of 4096 (2^12) unique
 * strings. When encoding bytes to Strings, the input bytes are encoded as 12-bit words. These 12-bit words are mapped
 * to unique Strings. When decoding Strings, the inputs Strings are mapped back to the corresponding 12-bit words which
 * are then decoded back to bytes.
 *
 * Minimal padding is used, see #toStrings and #toBytes for details of the padding algorithm.
 * The padding algorithm will cause additional bytes in the output only if the input number of bytes is two more than a
 * multiple of 3. Examples of these input sizes are 2, 5 and 8. A table depicting the number of of input bytes to 12-bit
 * words to output bytes is below
 *
 * | Input Bytes | 12-bit words | Output Bytes |
 * |-------------|--------------|--------------|
 * | 0           | 0            | 0            |
 * | 1           | 1            | 1            |
 * | 2           | 2            | 3            |
 * | 3           | 2            | 3            |
 * | 4           | 3            | 4            |
 * | 5           | 4            | 6            |
 * | 6           | 4            | 6            |
 * | 7           | 5            | 7            |
 * |-------------|--------------|--------------|
 *
 */
@Component
public class StringCoder {

    private static final int DICTIONARY_SIZE = 4096;

    private final List<String> dictionary;
    private final Map<String, Integer> toInt;

    public StringCoder(List<String> dictionary) {
        if (dictionary.size() != DICTIONARY_SIZE) {
            throw new IllegalArgumentException("The dictionary must be of size 4096");
        } else if (ImmutableSet.copyOf(dictionary).size() != dictionary.size()) {
            throw new IllegalArgumentException("The dictionary contains duplicates");
        }

        this.dictionary = dictionary;
        toInt = IntStream.range(0, DICTIONARY_SIZE)
                .mapToObj(i -> i)
                .collect(ImmutableMap.toImmutableMap(dictionary::get, i -> i));
    }

    @Bean
    public static StringCoder defau1t() throws IOException {
        String fileContents = new String(Files.readAllBytes(Paths.get("google-10000-english-no-swears.txt")));
        String[] words = fileContents.split("\\r?\\n");
        List<String> dictionary = Arrays.stream(words).limit(4096).collect(ImmutableList.toImmutableList());
        return new StringCoder(dictionary);
    }

    /*
     * The toWords() method takes an array of bytes and encodes it to an array of 12-bit words. The 12-bit words are then
     * mapped to Strings.
     * To convert the bytes to 12-bit words, we group the bytes in to triples  and concatenate them to form 24 bit words.
     * The 24-bit words are then halfed in to two 12-bit words.
     * The diagram below shows how 3 bytes are encoded in to 2 12-bit words.
     *
     * | byte 0   | byte 1  | byte 2   |
     * | 0000 0000 0000 0000 0000 0000 |
     * | word 0        | word 1        |
     *
     *
     * If the number of input bytes is one more than a multiple of 3, such as 1, 4 or 7, then the last byte is padded to a
     * 12 bit word. The diagram below displays how this is done with an input of 3 bytes
     *
     * | byte 0   | byte 1  | byte 2   | byte 3   | padding |
     * | 0000 0000 0000 0000 0000 0000 | 0000 0000 0000     |
     * | word 0        | word 1        | word 2             |
     *
     *
     * If the number if input bytes is two more than a multiple of 3, such as 2, 5, 8, then one padded byte is appended to
     * the input to make it a multiple of 3. The diagram below shows how a 4 byte input is padded.
     *
     * | byte 0   | byte 1  | byte 2   | byte 3   | byte 4  | padding  |
     * | 0000 0000 0000 0000 0000 0000 | 0000 0000 0000 0000 0000 0000 |
     * | word 0        | word 1        | word 2        | word 3        |
     */
    public List<String> toStrings(byte[] bytes) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (int i = 0; i < bytes.length; i += 3) {
            int byte0 = bytes[i] & 0xFF;
            int byte1 = i + 1 < bytes.length ? bytes[i + 1] & 0xFF : 0;

            //add the first 12-bit word
            int word0 = (byte0 << 4) + (byte1 >> 4);
            builder.add(dictionary.get(word0));

            //check there are enough bytes left for the last 12-bit word
            if (i + 1 < bytes.length) {
                int byte2 = i + 2 < bytes.length ? bytes[i + 2] & 0xFF : 0;
                int word1 = ((byte1 & 0x0F) << 8) + byte2;

                builder.add(dictionary.get(word1));
            }
        }
        return builder.build();
    }

    /*
     * The toBytes() method takes a list of Strings and converts them via a 1->1 mapping to a list of 12-bit words. To
     * decode the 12-bit words to bytes, we append pairs of 12-bit words to create 24-bit words. We then divide these by
     *  3 to create 3 bytes. A diagram of this is below.
     *
     * | byte 0   | byte 1  | byte 2   |
     * | 0000 0000 0000 0000 0000 0000 |
     * | word 0        | word 1        |
     *
     *
     * If the number of input 12-bit words is an odd number, then we assume that the last 4 bits of the last 12-bit word
     * were added via padding and we ignore them. In the example below, 3 12-bit words are decoded to 4 bytes.
     *
     * | byte 0   | byte 1  | byte 2   | byte 3   | ignored |
     * | 0000 0000 0000 0000 0000 0000 | 0000 0000 0000     |
     * | word 0        | word 1        | word 2             |
     */
    public byte[] toBytes(List<String> strings) throws UnknownStringException, BadPaddingException {
        int maxBytes = (strings.size() + 1) / 2 * 3;
        List<Byte> bytes = new ArrayList<>(maxBytes);
        for (int i = 0; i < strings.size(); i += 2) {
            int word0 = getInt(strings.get(i));
            int word1 = i + 1 < strings.size() ? getInt(strings.get(i + 1)) : 0;

            byte byte0 = (byte) (word0 >> 4);
            byte byte1 = (byte) (((word0 & 0x00F) << 4) + (word1 >> 8));
            byte byte2 = (byte) (word1 & 0x0FF);

            bytes.add(byte0);

            if (i + 1 == strings.size()) {
                if (byte1 != 0) {
                    throw new BadPaddingException("If the input size is an odd number, then the last word must not have a value larger than a byte");
                }
                break;
            }

            bytes.add(byte1);
            bytes.add(byte2);


        }
        return Bytes.toArray(bytes);
    }


    private int getInt(String string) throws UnknownStringException {
        Integer result = toInt.get(string);
        if (result == null) {
            throw new UnknownStringException(string);
        }
        return result;
    }
}
