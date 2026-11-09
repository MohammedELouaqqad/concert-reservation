package com.example.reservation_concert.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthentificationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        .requestMatchers(GET,"/api/v1/users").hasAuthority("ADMIN")
                        .requestMatchers(POST,"/api/v1/users/admin").hasAuthority("ADMIN")
                        .requestMatchers(GET,"/api/v1/users/me").authenticated()

                        .requestMatchers(GET,"/api/v1/concerts/**").authenticated()
                        .requestMatchers(POST,"/api/v1/concerts/*/reservations").authenticated()
                        .requestMatchers(POST,"/api/v1/concerts/**").hasAuthority("ADMIN")
                        .requestMatchers(PUT,"/api/v1/concerts/**").hasAuthority("ADMIN")
                        .requestMatchers(DELETE,"/api/v1/concerts/**").hasAuthority("ADMIN")


                        .requestMatchers(GET,"/api/v1/reservations").authenticated()
                        .requestMatchers(PUT,"/api/v1/reservations/**").authenticated()

                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
