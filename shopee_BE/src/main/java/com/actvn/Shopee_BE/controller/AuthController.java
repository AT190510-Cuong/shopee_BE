package com.actvn.Shopee_BE.controller;

import com.actvn.Shopee_BE.dto.request.LoginRequest;
import com.actvn.Shopee_BE.dto.request.SignupRequest;
import com.actvn.Shopee_BE.dto.request.UserRequest;
import com.actvn.Shopee_BE.dto.response.MessageResponse;
import com.actvn.Shopee_BE.dto.response.UserInfoResponse;
import com.actvn.Shopee_BE.entity.AppRole;
import com.actvn.Shopee_BE.entity.Role;
import com.actvn.Shopee_BE.entity.User;
import com.actvn.Shopee_BE.security.jwt.JwtUtils;
import com.actvn.Shopee_BE.security.service.UserDetailsImpl;
import com.actvn.Shopee_BE.service.RoleService;
import com.actvn.Shopee_BE.service.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleService roleService;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication ;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", "Bad credentials");
            map.put("status", 401);
//
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);
        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                cookie.toString(),
                userDetails.getUsername(),
                roles
        );
        return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userService.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // 2. set roles for user

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));


        UserRequest newUser = new UserRequest(signUpRequest.getUsername(),signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword())) ;
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();


        if(strRoles == null){
            Role role = roleService.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(role);

        }
        else {
            for(String role : strRoles){
                switch (role) {
                    case "ADMIN":
                        Role adminRole = roleService.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "SELLER":
                        Role sellerRole = roleService.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(sellerRole);

                        break;
                    default:
                        Role userRole = roleService.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);

                        break;
                }
            }
        }

        newUser.setRoles(roles);

        // 3. save user
        // 4. return response

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.creatNewUser(newUser));
    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> signoutUser(){
        ResponseCookie cleanJwtCookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString())
                .body(new MessageResponse("You've been sihned out"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetail(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).toList();
        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);


    }

    public String currentUser(Authentication authentication) {
        if(authentication != null){
            return authentication.getName();
        }
          return "";
    }
}

