package me.detj.timetravel.dto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PastStringsEntry {

    private final List<String> Strings;
    private final Date date;

    public PastStringsEntry(Date date, List<String> Strings) {
        this.date = date;
        this.Strings = Strings;
    }

    @Override
    public String toString() {
        return "PastWordsEntry{" +
                "words=[" + Strings.stream().collect(Collectors.joining(",")) +
                "], date=" + date +
                '}';
    }

    public List<String> getStrings() {
        return Strings;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PastStringsEntry that = (PastStringsEntry) o;
        return Strings.equals(that.Strings) &&
                date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Strings, date);
    }
}
