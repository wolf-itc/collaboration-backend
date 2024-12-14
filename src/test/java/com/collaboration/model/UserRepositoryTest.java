/**
 *  UserRepositoryTest
 *  
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private String testUsername;
    private String testEmail;
    private String testActivationKey;

    @BeforeEach
    public void setUp() {
        // Set up test data
        testUsername = "testUser";
        testEmail = "testuser@example.com";
        testActivationKey = "activationKey123";

        User user = new User();
        user.setUsername(testUsername);
        user.setEmail(testEmail);
        user.setActivationkey(testActivationKey);
        user.setEnabled(true);
        user.setLastname("Doe");
        user.setFirstname("John");
        user.setFailloginCount(0);
        user.setShowNotifications("yes");
        user.setShowIntro(true);
        user.setShowHelp(true);
        user.setShowClearname(true);
        user.setShowContact(true);
        user.setLanguage("en");
        user.setMailSubject("Welcome");
        user.setMailBody("Hello, welcome to our service!");

        userRepository.save(user);
    }

    @Test
    public void testFindByUsername() {
        // Find user by username
        Optional<User> result = userRepository.findByUsername(testUsername);

        // Check if the user is found
        assertTrue(result.isPresent());
        assertEquals(testUsername, result.get().getUsername());
    }

    @Test
    public void testFindByEmail() {
        // Find user by email
        Optional<User> result = userRepository.findByEmail(testEmail);

        // Check if the user is found
        assertTrue(result.isPresent());
        assertEquals(testEmail, result.get().getEmail());
    }

    @Test
    public void testFindByActivationkey() {
        // Find user by activation key
        Optional<User> result = userRepository.findByActivationkey(testActivationKey);

        // Check if the user is found
        assertTrue(result.isPresent());
        assertEquals(testActivationKey, result.get().getActivationkey());
    }

    @Test
    public void testFindByUsernameNotFound() {
        // Try to find a user with a non-existing username
        Optional<User> result = userRepository.findByUsername("nonExistentUser");

        // Assert that no user is found
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByEmailNotFound() {
        // Try to find a user with a non-existing email
        Optional<User> result = userRepository.findByEmail("nonexistentuser@example.com");

        // Assert that no user is found
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByActivationkeyNotFound() {
        // Try to find a user with a non-existing activation key
        Optional<User> result = userRepository.findByActivationkey("nonexistentActivationKey");

        // Assert that no user is found
        assertFalse(result.isPresent());
    }
}
