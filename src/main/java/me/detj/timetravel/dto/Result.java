package me.detj.timetravel.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Result {

    private final ResultStatus status;

    private final Date date;


    @JsonCreator
    public Result(@JsonProperty("status") ResultStatus status, @JsonProperty("date") Date date) {
        this.status = Preconditions.checkNotNull(status, "status is null");
        this.date = date;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }
}
