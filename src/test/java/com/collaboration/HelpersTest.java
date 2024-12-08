/**
 *  HelpersTest
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration;

import com.collaboration.config.CollaborationException;
import com.collaboration.config.CollaborationException.CollaborationExceptionReason;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HelpersTest {

  @Test
  public void testEnsureAlphabetic_ValidInput() {
    // Test mit gültigen alphabetischen Eingaben
    assertDoesNotThrow(() -> Helpers.ensureAlphabetic("abc"));
    assertDoesNotThrow(() -> Helpers.ensureAlphabetic("ABC"));
    assertDoesNotThrow(() -> Helpers.ensureAlphabetic("aBcDeF"));
  }

  @Test
  public void testEnsureAlphabetic_InvalidInput() {
    // Test mit ungültigen Eingaben
    assertThrows(CollaborationException.class, () -> Helpers.ensureAlphabetic("abc123"));
    assertThrows(CollaborationException.class, () -> Helpers.ensureAlphabetic("abc!@#"));
    assertThrows(CollaborationException.class, () -> Helpers.ensureAlphabetic("123"));
    assertThrows(CollaborationException.class, () -> Helpers.ensureAlphabetic(" "));
    assertThrows(CollaborationException.class, () -> Helpers.ensureAlphabetic("abc def"));
  }

  @Test
  public void testEnsureAlphabetic_EmptyString() {
    // Test mit leerem String
    assertThrows(CollaborationException.class, () -> Helpers.ensureAlphabetic(""));
  }

  @Test
  public void testEnsureAlphabetic_NullInput() {
    // Test mit null-Eingabe
    assertThrows(NullPointerException.class, () -> Helpers.ensureAlphabetic(null));
  }

  @Test
  public void testEnsureAlphabetic_ExceptionDetails() {
    // Test auf korrekte Exception-Details
    try {
      Helpers.ensureAlphabetic("abc123");
      fail("Expected CollaborationException was not thrown");
    } catch (CollaborationException e) {
      assertEquals(CollaborationExceptionReason.INVALID_VALUE, e.getExceptionReason());
    }
  }
}
