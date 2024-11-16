package com.actvn.Shopee_BE.controller;

import com.actvn.Shopee_BE.dto.request.LoginRequest;
import com.actvn.Shopee_BE.dto.response.LoginResponse;
import com.actvn.Shopee_BE.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SpringSecurityDemoController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SpringSecurityDemoController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;


    @GetMapping("/hello")
   public String sayHello() {
      return "Hello Spring security";
   }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String sayHelloAdmin() {
        return "Hello Admin";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/user")
    public String sayHelloUser() {
        return "Hello User";
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Signin application");
        Authentication authentication ;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", "Bad credentials");
            map.put("status", 401);
//
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateJwtTokenFromUsername(userDetails);
        List<String> roles = new ArrayList<>();
//        userDetails.getAuthorities().forEach(authority -> {
//            roles.add(authority.getAuthority());
//        });

        for(GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
//            roles.add(authority.getAuthority());
            String authority = grantedAuthority.getAuthority();
            roles.add(authority);
        }

        LoginResponse response = new LoginResponse(userDetails.getUsername(), jwtToken, roles);

        return ResponseEntity.status(HttpStatus.OK).body(response);


    }
}
