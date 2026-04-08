package com.breakdown.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.breakdown.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
            .cors(cors -> cors.configurationSource(
                corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers("/ws/**").permitAll()
            	    .requestMatchers(
            	            "/",
            	            "/index.html",
            	            "/favicon.ico",
            	            "/manifest.json",
            	            "/static/**",
            	            "/css/**",
            	            "/js/**",
            	            "/assets/**",
            	            "/api/auth/**"
            	    ).permitAll()
            	    .requestMatchers("/api/user/**").hasAnyAuthority("USER", "ADMIN")
            	    .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
            	    .requestMatchers("/api/mechanic/**").hasAnyAuthority("MECHANIC", "ADMIN")

            	    .anyRequest().authenticated()
            	)
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
       // config.setAllowedOrigins(List.of("http://localhost:3000"));
        //config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3002"));
        config.setAllowedOrigins(List.of(
        	    "http://localhost:3000",
        	    "http://localhost:3001",
        	    "http://localhost:3002",
        	    "http://localhost:3003",
        	    "http://localhost:3004",
        	    "http://localhost:3005",
        	    "http://localhost:3006",
        	    "http://localhost:3007",
        	    "http://localhost:3008",
        	    "http://localhost:61409"
        	));
        config.setAllowedMethods(List.of(
            "GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}