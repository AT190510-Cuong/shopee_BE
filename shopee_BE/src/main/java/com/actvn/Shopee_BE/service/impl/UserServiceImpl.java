package com.actvn.Shopee_BE.service.impl;

import com.actvn.Shopee_BE.dto.request.UserRequest;
import com.actvn.Shopee_BE.dto.response.ApiResponse;
import com.actvn.Shopee_BE.entity.User;
import com.actvn.Shopee_BE.repository.UserRepository;
import com.actvn.Shopee_BE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public ApiResponse<Object> creatNewUser(UserRequest newUser) {

        User user = new User(newUser.getUseName(),
                 newUser.getEmail(),
                 newUser.getPassword()
               );

        user.setRoles(newUser.getRoles());

        User created = userRepository.save(user);

//        user.setUserName(userRequest.getUseName());
//        user.setEmail(userRequest.getEmail());
//        user.setPassword(userRequest.getPassword());
//        user.setRoles(userRequest.getRoles());
//        userRepository.save(user);

        return ApiResponse.builder().status(
                HttpStatus.CREATED).message("User created successfully!").body(created).build();
    }


}
