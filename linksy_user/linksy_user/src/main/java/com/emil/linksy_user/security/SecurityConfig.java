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
                .requestMatchers("/api/users/message_mode").authenticated()
                .requestMatchers("/api/posts/create").authenticated()
                .requestMatchers("/api/users/change_password").authenticated()
                .requestMatchers("/api/users/update_birthday").authenticated()
                .requestMatchers("/api/users/update_link").authenticated()
                .requestMatchers("/api/users/update_username").authenticated()
                .requestMatchers("/api/upload/avatar").authenticated()
                .requestMatchers("/api/posts/user_posts").authenticated()
                .requestMatchers("/api/posts/delete_post").authenticated()
                .requestMatchers("/api/posts/like/**").authenticated()
                .requestMatchers("/api/posts/comment/delete").authenticated()
                .requestMatchers("/api/posts/add/comment").authenticated()
                .requestMatchers("/api/chats/**").authenticated()
                .requestMatchers("/api/feed/**").authenticated()
                .requestMatchers("/api/messages/**").authenticated()
                .requestMatchers("/api/moments/**").authenticated()
                .requestMatchers("/api/people/find/link").authenticated()
                .requestMatchers("/api/people/find/username").authenticated()
                .requestMatchers("/api/people/{id}").authenticated()
                .requestMatchers("/api/people/user_posts/{id}").authenticated()
                .requestMatchers("/api/people/user_moments/{id}").authenticated()
                .requestMatchers("/api/people/subscribe/{id}").authenticated()
                .requestMatchers("/api/people/unsubscribe/{id}").authenticated()
                .requestMatchers("/api/people/user_subscribers").authenticated()
                .requestMatchers("/api/people/user_subscriptions").authenticated()
                .requestMatchers("/api/people/blacklist/add/{id}").authenticated()
                .requestMatchers("/api/people/blacklist/remove/{id}").authenticated()
                .requestMatchers("/api/people/blacklist/all").authenticated()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
