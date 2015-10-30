package com.concur.swbservice.api.model.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by mtalbot on 11/08/2015.
 */
public class SwbserviceModel {

    private final String name;
    private final DateTime occurs;
    private final Long frequency;
    private final UUID id;

    @JsonCreator
    public SwbserviceModel(@JsonProperty("id") UUID id, @JsonProperty("name") String name, @JsonProperty("occurs") DateTime occurs, @JsonProperty("frequency") Long frequency) {
        this.id = id;
        this.name = name;
        this.occurs = occurs;
        this.frequency = frequency;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotEmpty
    public String getName() {
        return name;
    }

    @NotNull
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    public DateTime getOccurs() {
        return occurs;
    }

    @Min(0)
    @Max(100)
    public Long getFrequency() {
        return frequency;
    }
}
