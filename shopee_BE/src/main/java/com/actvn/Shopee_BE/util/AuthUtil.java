package com.actvn.Shopee_BE.util;

import com.actvn.Shopee_BE.entity.User;
import com.actvn.Shopee_BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    private UserRepository userRepository;

    public String getEmailLogged(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + authentication.getName()));
//        System.out.println(user.getEmail());
        return user.getEmail() ;

    }

    public String getUserIdLogged(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + authentication.getName()));
//        System.out.println(user.getId());
        return user.getUserId() ;
    }

    public User getUserNameLogged(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: "
                        + authentication.getName()));
//        System.out.println(user.getUserName());
        return user;
    }


}
