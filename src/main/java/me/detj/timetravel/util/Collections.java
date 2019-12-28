package me.detj.timetravel.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Collections {

    public static <T> Set<T> duplicates(Collection<T> collection) {
        Set<T> seen = new HashSet<>();
        Set<T> duplicates = new HashSet<>();
        for (T item : collection) {
            if (!seen.add(item)) {
                duplicates.add(item);
            }
        }
        return duplicates;
    }
}
