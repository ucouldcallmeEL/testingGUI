package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest {

    private static final String baseEmail = "testuser@gmail.com";
    private static final String basePassword = "password123";
    private static final String testUserID = "sharedTestUser";
    private static User user;

    @BeforeAll
    static void initOnce() throws Exception {
        user = new User();

        // Register shared user once — skip if already registered
        try {
            user.Register("Test User", testUserID, baseEmail, basePassword, "123 Abdo Basha", "01094749270", true);
        } catch (RegistrationException ignored) {
            // User already exists — it's okay for DB reuse
        }
    }

    @BeforeEach
    void setUpEachTest() {
        user = new User(); // Fresh instance for test safety
    }

    @Test
    @Order(1)
    @DisplayName("Register a client successfully")
    void registerClient_NewUser_ValidInputSuccess() throws Exception {
        String uniqueID = "user" + System.currentTimeMillis();
        assertDoesNotThrow(() -> user.Register("New User", uniqueID, baseEmail, basePassword, "123 Street", "01094749270", true));
    }

    @Test
    @Order(2)
    @DisplayName("Fail Registration due to Duplicate UserID from DB")
    void registerClient_DuplicateIDFromDB_ThrowsException() {
        RegistrationException ex = assertThrows(RegistrationException.class, () -> {
            user.Register("hagar", "hagar", baseEmail, basePassword, "123 Abdo Basha", "01094749270", true);
        });

        assertEquals("Client is already registered.", ex.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Fail Registration, Duplicate UserID (test user)")
    void registerClient_DuplicateTestUserID_ThrowsException() {
        RegistrationException ex = assertThrows(RegistrationException.class, () -> {
            user.Register("Test User", testUserID, baseEmail, basePassword, "123 Abdo Basha", "01094749270", true);
        });

        assertEquals("Client is already registered.", ex.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Registration fails due to empty name")
    void registration_EmptyName_ThrowsException() {
        RegistrationException ex = assertThrows(RegistrationException.class, () -> {
            user.Register("", "new" + System.currentTimeMillis(), baseEmail, basePassword, "123 Abdo Basha", "01094749270", true);
        });

        assertEquals("Please enter a valid name.", ex.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Registration fails due to empty email")
    void registration_EmptyEmail_ThrowsException() {
        RegistrationException ex = assertThrows(RegistrationException.class, () -> {
            user.Register("Test User", "new" + System.currentTimeMillis(), "", basePassword, "123 Abdo Basha", "01094749270", true);
        });

        assertEquals("Invalid email address.", ex.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Registration fails due to short phone number")
    void registration_ShortPhoneNumber_ThrowsException() {
        RegistrationException ex = assertThrows(RegistrationException.class, () -> {
            user.Register("Test User", "new" + System.currentTimeMillis(), baseEmail, basePassword, "123 Abdo Basha", "01234", true);
        });

        assertEquals("Phone number is invalid.", ex.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Registration fails: phone doesn't start with 0")
    void registration_PhoneWrongStart_ThrowsException() {
        RegistrationException ex = assertThrows(RegistrationException.class, () -> {
            user.Register("Test User", "new" + System.currentTimeMillis(), baseEmail, basePassword, "123 Abdo Basha", "11234567890", true);
        });

        assertEquals("Phone number is invalid.", ex.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Registration fails due to empty address (client)")
    void registration_EmptyAddressClient_ThrowsException() {
        RegistrationException ex = assertThrows(RegistrationException.class, () -> {
            user.Register("Test User", "new" + System.currentTimeMillis(), baseEmail, basePassword, "", "01094749270", true);
        });

        assertEquals("Invalid address.", ex.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Registration fails due to incorrect password length")
    void registration_WrongPasswordLength_ThrowsException() {
        RegistrationException ex = assertThrows(RegistrationException.class, () -> {
            user.Register("Test User", "new" + System.currentTimeMillis(), baseEmail, "1234", "123 Abdo Basha", "01094749270", true);
        });

        assertEquals("Password must be at least 8 characters.", ex.getMessage());
    }


    @Test
    @Order(10)
    @DisplayName("Login successful with existing DB user")
    void login_ExistingDBUser_ReturnsUserID() throws Exception {
        String result = user.LogIn("hagar", "hagar2005");
        assertEquals("hagar", result);
    }

    @Test
    @Order(11)
    @DisplayName("Login failed with incorrect username")
    void login_WrongUsername_ThrowsException() {
        LogInException ex = assertThrows(LogInException.class, () -> {
            user.LogIn("wrongusername", basePassword);
        });

        assertEquals("Username is incorrect.", ex.getMessage());
    }

    @Test
    @Order(12)
    @DisplayName("Login failed with incorrect username from DB")
    void login_WrongDBUsername_ThrowsException() {
        LogInException ex = assertThrows(LogInException.class, () -> {
            user.LogIn("gaga", "hagar2005");
        });

        assertEquals("Username is incorrect.", ex.getMessage());
    }

    @Test
    @Order(13)
    @DisplayName("Login failed with incorrect password for test user")
    void login_WrongPasswordForTestUser_ThrowsException() {
        LogInException ex = assertThrows(LogInException.class, () -> {
            user.LogIn(testUserID, "wrongpassword");
        });

        assertEquals("Password is incorrect.", ex.getMessage());
    }

    @Test
    @Order(14)
    @DisplayName("Login failed with incorrect password for DB user")
    void login_WrongPasswordForDBUser_ThrowsException() {
        LogInException ex = assertThrows(LogInException.class, () -> {
            user.LogIn("hagar", "wrongpassword");
        });

        assertEquals("Password is incorrect.", ex.getMessage());
    }


    @Test
    @Order(15)
    @DisplayName("Change password fails with wrong current password")
    void changePassword_WrongCurrentPassword_ThrowsException() {
        PasswordChangeException ex = assertThrows(PasswordChangeException.class, () -> {
            user.ChangePassword(testUserID, "newPass123", "wrongpass");
        });
        assertEquals("The entered Password doesn't match with the user's.", ex.getMessage());
    }

    @Test
    @Order(16)
    @DisplayName("Change password fails with short new password")
    void changePassword_ShortNewPassword_ThrowsException() {
        // Set the correct current password first
        String currentPassword = "password123"; // assuming basePassword is still valid

        PasswordChangeException ex = assertThrows(PasswordChangeException.class, () -> {
            user.ChangePassword(testUserID, "short", currentPassword);
        });

        assertEquals("Password must be at least 8 characters.", ex.getMessage());
    }


    @Test
    @Order(17)
    @DisplayName("Update email successfully for DB user (hagar)")
    void updateEmail_DBUser_Success() {
        assertDoesNotThrow(() -> {
            user.updateEmail("hagar", "updated_email@test.com", "hagar2005");
        });
    }

    @Test
    @Order(18)
    @DisplayName("Update email fails with incorrect password")
    void updateEmail_WrongPassword_ThrowsException() {
        EmailAuthenticationException ex = assertThrows(EmailAuthenticationException.class, () -> {
            user.updateEmail("hagar", "test@email.com", "wrongpass");
        });
        assertEquals("Incorrect current password.", ex.getMessage());
    }

    @Test
    @Order(19)
    @DisplayName("Update email fails with invalid email format")
    void updateEmail_InvalidFormat_ThrowsException() {
        EmailAuthenticationException ex = assertThrows(EmailAuthenticationException.class, () -> {
            user.updateEmail("hagar", "invalidemail", "hagar2005");
        });
        assertEquals("Invalid email format.", ex.getMessage());
    }


    @Test
    @Order(20)
    @DisplayName("Update address successfully for test user")
    void updateAddress_TestUser_Success() {
        String currentPassword = "password123"; // assuming basePassword is still valid
        assertDoesNotThrow(() -> {
            user.updateAddress(testUserID, "456 Updated Street", currentPassword);
        });
    }

    @Test
    @Order(21)
    @DisplayName("Update address fails with wrong password")
    void updateAddress_WrongPassword_ThrowsException() {
        AddressChangeException ex = assertThrows(AddressChangeException.class, () -> {
            user.updateAddress(testUserID, "456 Street", "wrongpass");
        });
        assertEquals("Incorrect current password.", ex.getMessage());
    }

    @Test
    @Order(22)
    @DisplayName("Update address fails with empty address")
    void updateAddress_EmptyAddress_ThrowsException() {
        String currentPassword = "password123"; // assuming basePassword is still valid
        AddressChangeException ex = assertThrows(AddressChangeException.class, () -> {
            user.updateAddress(testUserID, "",currentPassword);
        });
        assertEquals("Invalid address.", ex.getMessage());
    }


    @Test
    @Order(23)
    @DisplayName("Update phone number successfully for DB user")
    void updatePhoneNumber_DBUser_Success() {
        assertDoesNotThrow(() -> {
            user.updatePhoneNumber("hagar", "01234567891", "hagar2005");
        });
    }

    @Test
    @Order(24)
    @DisplayName("Update phone fails with wrong password")
    void updatePhoneNumber_WrongPassword_ThrowsException() {
        AuthException ex = assertThrows(AuthException.class, () -> {
            user.updatePhoneNumber("hagar", "01234567891", "wrongpass");
        });
        assertEquals("The entered Password doesn't match with the user's.", ex.getMessage());
    }

    @Test
    @Order(25)
    @DisplayName("Update phone fails with short number")
    void updatePhoneNumber_ShortNumber_ThrowsException() {
        PhoneNumberException ex = assertThrows(PhoneNumberException.class, () -> {
            user.updatePhoneNumber("hagar", "12345", "hagar2005");
        });
        assertEquals("Phone number must be exactly 11 characters.", ex.getMessage());
    }




    @AfterAll
    static void resetModifiedUsers() {
        try {
            // Reset shared test user's credentials
            user.ChangePassword(testUserID, basePassword, "newPass123");
            user.updateEmail(testUserID, baseEmail, basePassword);
            user.updateAddress(testUserID, "123 Abdo Basha", basePassword);
            user.updatePhoneNumber(testUserID, "01094749270", basePassword);
        } catch (Exception ignored) {
            // Ignore if already reset or if not needed
        }

        try {
            // Reset hagar's password if changed
            user.ChangePassword("hagar", "hagar2005", "hagarNewPass2024");
        } catch (Exception ignored) {
        }
    }



}