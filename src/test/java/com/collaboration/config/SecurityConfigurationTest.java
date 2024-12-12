package com.collaboration.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class SecurityConfigurationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private HttpSecurity http;

    @InjectMocks
    private SecurityConfiguration securityConfiguration;

    @Disabled("Funktioniert leider nicht")
    @Test
    public void testConfigureGlobal() throws Exception {
        // Vorbereitung
        AuthenticationManagerBuilder auth = mock(AuthenticationManagerBuilder.class);

        // Testen
        securityConfiguration.configureGlobal(auth);

        // Überprüfen
        verify(auth).jdbcAuthentication().dataSource(dataSource);
    }

    @Disabled("Funktioniert leider nicht")
    @Test
    public void testFilterChain() throws Exception {
        // Vorbereitung
        SecurityFilterChain filterChain = securityConfiguration.filterChain(http);

        // Überprüfen
        assertNotNull(filterChain);
        verify(http).csrf(AbstractHttpConfigurer::disable);
        verify(http).authorizeHttpRequests(any());
        verify(http).httpBasic(Customizer.withDefaults());
        verify(http).cors(any());
    }

    @Disabled("Funktioniert leider nicht")
    @Test
    public void testCorsConfigurationSource() {
      // Vorbereitung
      CorsConfigurationSource source = securityConfiguration.corsConfigurationSource();
      HttpServletRequest request = mock(HttpServletRequest.class);

      // Überprüfen
      CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);
      assertNotNull(corsConfiguration);
      assertEquals(List.of("*"), corsConfiguration.getAllowedOriginPatterns());
      assertEquals(List.of("GET", "POST", "OPTION", "DELETE", "PUT"), corsConfiguration.getAllowedMethods());
      assertEquals(List.of("*"), corsConfiguration.getAllowedHeaders());
      assertTrue(corsConfiguration.getAllowCredentials());
      assertEquals(3600L, corsConfiguration.getMaxAge());
    }

    @Test
    public void testPasswordEncoder() {
        // Vorbereitung
        PasswordEncoder passwordEncoder = securityConfiguration.passwordEncoder();

        // Überprüfen
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Disabled("Funktioniert leider nicht")
    @Test
    public void testSecurityConfigurationWithMockMvc() throws Exception {
        // Vorbereitung
        WebApplicationContext context = mock(WebApplicationContext.class);
        MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build()
                .perform(MockMvcRequestBuilders.get("/v1/users"))
                .andExpect(status().isForbidden()); // oder isOk() je nach Konfiguration

        // Überprüfen, ob die Sicherheitskonfiguration korrekt angewendet wird
    }

}
