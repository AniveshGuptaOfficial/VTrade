package com.vtrade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("hostel_block")
    private String hostelBlock;

    @JsonProperty("room_number")
    private String roomNumber;

    @JsonProperty("avatar_url")
    private String avatarUrl;
}
