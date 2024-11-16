package com.actvn.Shopee_BE.dto.request;

import com.actvn.Shopee_BE.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {
    private String useName;
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();

    public UserRequest(String useName, String email, String password) {
        this.useName = useName;
        this.email = email;
        this.password = password;
    }
}
