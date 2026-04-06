package com.an.profile_service.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String userId;
    String username;
    String avatar;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    String city;
}
