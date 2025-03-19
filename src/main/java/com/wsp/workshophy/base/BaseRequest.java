package com.wsp.workshophy.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wsp.workshophy.utilities.serializer.DateToTimestamp;
import com.wsp.workshophy.utilities.serializer.TimestampToDate;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public Boolean active = true;
    @JsonIgnore
    public Long version = 0L;
    @JsonIgnore
    public String createdBy;
    @JsonIgnore
    public String updatedBy;

    @JsonIgnore
    @JsonSerialize(using = DateToTimestamp.class)
    @JsonDeserialize(using = TimestampToDate.class)
    public LocalDateTime createdDate;

    @JsonIgnore
    @JsonSerialize(using = DateToTimestamp.class)
    @JsonDeserialize(using = TimestampToDate.class)
    public LocalDateTime updatedDate;
}
