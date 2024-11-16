package com.actvn.Shopee_BE.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {
    private String email;
    private String username;
    private String password;
    private Set<String> roles;

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    public Set<String> getRoles() {
        return roles;
    }

//    public Set<String> getRole() {
//    }
}
