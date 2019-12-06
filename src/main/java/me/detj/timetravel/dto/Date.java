package me.detj.timetravel.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;

public class Date {

    private final int year, month, day;

    @JsonCreator
    public Date(@JsonProperty("year") int year,
                @JsonProperty("month") int month,
                @JsonProperty("day") int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static Date fromLocalDate(LocalDate date) {
        return new Date(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(year, month, day);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return year == date.year &&
                month == date.month &&
                day == date.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }

    @Override
    public String toString() {
        return String.format("%d-%d-%d", year, month, day);
    }
}
