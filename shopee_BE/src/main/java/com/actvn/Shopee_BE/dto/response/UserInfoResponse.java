package com.actvn.Shopee_BE.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoResponse {
    private String userId;
    private String jwtToken;
    private String userName;

    private List<String> roles;

    public UserInfoResponse(String userId, String userName, List<String> roles) {
        this.userId = userId;
        this.userName = userName;
        this.roles = roles;
    }
}
