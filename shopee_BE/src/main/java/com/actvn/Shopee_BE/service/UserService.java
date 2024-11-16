package com.actvn.Shopee_BE.service;

import com.actvn.Shopee_BE.dto.request.UserRequest;
import com.actvn.Shopee_BE.dto.response.ApiResponse;

public interface UserService {
    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);


    ApiResponse<Object> creatNewUser(UserRequest userRequest);
}
