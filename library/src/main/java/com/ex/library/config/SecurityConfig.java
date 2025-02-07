package com.ex.library.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    private final String[] PUBLIC_ENDPOINT =
            {"/auth/**",
                    "/library/**",
                    "/api/user/register",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api/book",
                    "/api/book/search",
                    "/api/book/search/*",
                    "/api/user/verify/**",
                    "/maintenance",
                    "/maintenance/**"};

    @Autowired
    private CustomJwtDecoder customJwtDecoder;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(
                registry ->
                        registry
                                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                                .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINT).permitAll()
                                .requestMatchers(HttpMethod.PUT, PUBLIC_ENDPOINT).permitAll()
                                .anyRequest().authenticated()
        );

        httpSecurity.oauth2ResourceServer(resourceServerConfigurer ->
                resourceServerConfigurer.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder))
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }


    @Bean
    JwtAuthenticationConverter converter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
