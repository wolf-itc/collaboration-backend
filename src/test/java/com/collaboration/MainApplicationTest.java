package com.collaboration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MainApplicationTest {
  @Test
  void contextLoads() {
      // Dieser Test stellt sicher, dass der Anwendungskontext erfolgreich geladen wird
  }
}
