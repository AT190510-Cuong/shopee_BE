package com.actvn.Shopee_BE.security;

import com.actvn.Shopee_BE.entity.AppRole;
import com.actvn.Shopee_BE.entity.Role;
import com.actvn.Shopee_BE.repository.RoleRepository;
import com.actvn.Shopee_BE.security.jwt.AuthEntryPointJwt;
import com.actvn.Shopee_BE.security.jwt.AuthTokenFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SecurityConfiguration.class);
//    @Autowired
//    private DataSource mDataSource;


    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthEntryPointJwt unauthEntryPointJwt;
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) ->
                requests.requestMatchers("/api/auth/signin").permitAll()
                        .requestMatchers("/api/auth/signup").permitAll()
                        .anyRequest().authenticated()


//                requests.anyRequest().authenticated()
        );
//        http.authorizeHttpRequests(request -> {
//                request.requestMatchers("/h2-console/**").permitAll()
//                .requestMatchers("/signin").permitAll()
//                        .anyRequest().authenticated();
//    });
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()
        ));
//        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling((exception) -> exception.authenticationEntryPoint(unauthEntryPointJwt));
        return http.build();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter();
    }
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }

    @Bean
    public CommandLineRunner initData(UserDetailsService userDetailsService) {

        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role role = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(role);
                    });
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role role = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(role);
                    });
            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role role = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(role);
                    });


//            JdbcUserDetailsManager manager = (JdbcUserDetailsManager) userDetailsService;
            UserDetails user1 = User.withUsername("user1").password(passwordEncoder().encode("passwordUser1"))
                    .roles("USER")
                    .build();
            logger.info("user1: {}", user1.getPassword());
//            System.out.println("user1: " + user1);
            UserDetails admin = User.withUsername("admin")
                    .password(passwordEncoder().encode("passwordAdmin"))
                    .roles("ADMIN")
                    .build();
//            JdbcUserDetailsManager manager = (JdbcUserDetailsManager) userDetailsService;
            logger.info("admin: {}", admin.getPassword());

            if(userDetailsService instanceof InMemoryUserDetailsManager){
                InMemoryUserDetailsManager inMemoryUserDetailsManager = (InMemoryUserDetailsManager) userDetailsService;
                inMemoryUserDetailsManager.createUser(user1);
                inMemoryUserDetailsManager.createUser(admin);
            }
//            JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(mDataSource);
//            userDetailsManager.createUser(user1);
//            userDetailsManager.createUser(admin);
        };

    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager(){
//        return new InMemoryUserDetailsManager();
//    }

//    @Bean
//    public UserDetailsService UserDetailsService (){
//        UserDetails user1 = User.withUsername("user1").password("passwordUser1")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.withUsername("admin")
//                .password("passwordAdmin")
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user1, admin);
//    }

    @Bean
    public InMemoryUserDetailsManager userDetailsManager(){
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}
