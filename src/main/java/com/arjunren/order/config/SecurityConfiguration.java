package com.arjunren.order.config;

import com.arjunren.order.security.BearerTokenFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration @EnableMethodSecurity
public class SecurityConfiguration {
    @Bean BCryptPasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
    @Bean SecurityFilterChain security(HttpSecurity http,BearerTokenFilter filter,CorsConfigurationSource cors)throws Exception{return http.csrf(c->c.disable()).cors(c->c.configurationSource(cors)).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/api/auth/register","/actuator/health").permitAll().requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().anyRequest().authenticated()).addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).build();}
    @Bean CorsConfigurationSource cors(@Value("${app.security.allowed-origins}") String origins){var c=new CorsConfiguration();c.setAllowedOrigins(List.of(origins.split(",")));c.setAllowedMethods(List.of("GET","POST","PATCH","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type"));c.setAllowCredentials(false);var source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",c);return source;}
}
