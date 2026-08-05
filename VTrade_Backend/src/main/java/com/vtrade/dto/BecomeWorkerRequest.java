package com.vtrade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BecomeWorkerRequest {

    @NotBlank
    @JsonProperty("worker_type")
    private String workerType;

    @NotBlank
    @JsonProperty("student_id")
    private String studentId;

    private String bio;
}
