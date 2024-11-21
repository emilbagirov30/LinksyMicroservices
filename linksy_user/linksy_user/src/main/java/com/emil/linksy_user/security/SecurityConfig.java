package com.emil.linksy_user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers("/api/users/profile_data").authenticated()
                .requestMatchers("/api/users/all_data").authenticated()
                .requestMatchers("/api/users/delete_avatar").authenticated()
                .requestMatchers("/api/posts/create").authenticated()
                .requestMatchers("/api/users/change_password").authenticated()
                .requestMatchers("/api/users//update_birthday").authenticated()
                .requestMatchers("/api/users//update_link").authenticated()
                .requestMatchers("/api/users//update_username").authenticated()
                .requestMatchers("/api/users/upload/avatar").authenticated()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
