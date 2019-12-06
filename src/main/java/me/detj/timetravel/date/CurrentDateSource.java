package me.detj.timetravel.date;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CurrentDateSource implements DateSource {

    public LocalDate get() {
        return LocalDate.now();
    }
}
