package me.detj.timetravel.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class PastStringsRequest {

    private final int perPage;
    private final int page;

    @JsonCreator
    public PastStringsRequest(@JsonProperty("perPage") int perPage, @JsonProperty("page") int page) {
        Preconditions.checkArgument(perPage > 0, "perPage must be greater than 0");
        Preconditions.checkArgument(page >= 0, "perPage must positive");

        this.perPage = perPage;
        this.page = page;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "PastWordsRequest{" +
                "perPage=" + perPage +
                ", page=" + page +
                '}';
    }
}
