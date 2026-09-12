package com.vtrade.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkerSlotRequest {

    @NotBlank
    @JsonProperty("day_of_week")
    private String dayOfWeek;

    @NotBlank
    @JsonProperty("start_time")
    private String startTime;

    @NotBlank
    @JsonProperty("end_time")
    private String endTime;
}
