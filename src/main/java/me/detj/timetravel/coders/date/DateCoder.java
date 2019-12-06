package me.detj.timetravel.coders.date;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;

@Component
public class DateCoder {

    private static final DateCoder instance = new DateCoder();

    private DateCoder() {}

    @Bean
    public static DateCoder getInstance() {
        return instance;
    }

    public byte[] fromDate(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        return new byte[]{(byte) (year - 2000), (byte) month, (byte) day};
    }

    public LocalDate toDate(byte[] bytes) {
        for (int i = 3; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                throw new DateTimeException("Padding bytes are not 0");
            }
        }

        int year = bytes[0] + 2000;
        int month = bytes[1];
        int day = bytes[2];

        return LocalDate.of(year, month, day);
    }
}
