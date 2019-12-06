package me.detj.timetravel.dto;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.stream.Collectors;

public class Page<T> {

    private final int pageNum;
    private final int size;
    private final List<T> items;

    public Page(int pageNum, List<T> items) {
        Preconditions.checkArgument(pageNum >= 0, "pageNum must be positive");
        Preconditions.checkNotNull(items, "items cannot be null");
        Preconditions.checkArgument(!items.isEmpty(), "items cannot be empty");

        this.pageNum = pageNum;
        this.items = items;
        size = items.size();
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getSize() {
        return size;
    }

    public List<T> getItems() {
        return items;
    }


    @Override
    public String toString() {
        return "Page{" +
                "pageNum=" + pageNum +
                ", size=" + size +
                ", items=[" + System.lineSeparator() +
                items.stream().map(T::toString).collect(Collectors.joining("," + System.lineSeparator())) +
                "]" + System.lineSeparator() +
                '}';
    }
}
