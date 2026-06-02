package com.swp391.final_project.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // public pages
                        .requestMatchers(
                                "/",
                                "/home",
                                "/login",
                                "/forgot-password",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/image/**"
                        ).permitAll()
                        // ADMIN
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        // STUDENT & INSTRUCTOR
                        .requestMatchers("/student/**")
                        .hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers("/instructor/**")
                        .hasAnyRole("INSTRUCTOR", "ADMIN")
                        // tất cả request khác cần login
                        .anyRequest()
                        .authenticated()
                )
                // login bằng form
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/do-login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // access denied
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}