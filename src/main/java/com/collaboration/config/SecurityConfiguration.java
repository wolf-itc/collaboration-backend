/**
 *  SecurityConfiguration
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.config;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final DataSource dataSource;

    public SecurityConfiguration(final DataSource dataSource) {
      this.dataSource = dataSource;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.jdbcAuthentication().dataSource(dataSource);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(r -> r.requestMatchers(
          "/v1/users", "/favicon.ico", "/v1/users/login", "/v1/users/activate-account/**", "/v1/users/prepare-password-reset", "/v1/users/reset-password/**", "/v1/users/retrieveMainMenu").permitAll()
      .requestMatchers(
        "/v1/users/**", "/v1/nonusers/**", "/v1/roles/**", "/v1/itemtypes/**", "/v1/permissions/**").hasRole("USER")
      .requestMatchers(
        "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").hasAnyRole("DEVELOPER", "ADMIN")
      .requestMatchers(
        "/v1/organizations/**").hasAnyRole("DEVELOPER", "ADMIN") // Only for development! After that only ADMIN
      .requestMatchers(
        "/v1/authorities/**").hasAnyRole("ADMIN")
      .anyRequest().denyAll());
      http.httpBasic(Customizer.withDefaults());
      http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
      return http.build();
    }    
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration corsConfiguration = new CorsConfiguration();
      corsConfiguration.setAllowedOriginPatterns(List.of("*"));
//      corsConfiguration.setAllowedOrigins(List.of("https://pff.ddnss.de:3000", "https://editor.swagger.io"));
      corsConfiguration.setAllowedMethods(List.of("GET", "POST", "OPTION", "DELETE", "PUT"));
      corsConfiguration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
      corsConfiguration.setAllowCredentials(true);
      corsConfiguration.setAllowedHeaders(List.of("*"));
      corsConfiguration.setMaxAge(3600L);
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", corsConfiguration);
      return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }
}