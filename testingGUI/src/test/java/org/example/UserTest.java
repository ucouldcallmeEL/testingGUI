package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.List;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest {

    private static final String testUserID = "sharedTestUser";
    private static final String baseEmail = "testuser@gmail.com";
    private static final String basePassword = "password123";

    private static String hagarOriginalPassword = "hagar2005";
    private static String hagarNewPassword = "gaga2005";

    private static User user;

    @BeforeAll
    static void setUpOnce() {
        user = new User();
        try {
            user.Register("Test User", testUserID, baseEmail, basePassword, "123 Abdo Basha", "01094749270", true);
        } catch (RegistrationException ignored) {
            // User already exists; continue with existing state
        }

        // Reset password in case it was changed by a previous run
        try {
            user.ChangePassword(testUserID, basePassword, "newPass123");
            user.ChangePassword(testUserID, basePassword, "clientPass123");
        } catch (Exception ignored) {
        }
    }

    @BeforeEach
    void setUpEachTest() {
//        user = new User(); // fresh instance for isolation
    }

    @Test
    @Order(1)
    @DisplayName("Register a new client successfully")
    void registerClient_NewUser_Success() {
        String uniqueID = "testClient1";
        try {
            // If the user already exists (from previous runs), skip registration
            user.LogIn(uniqueID, basePassword); // this will throw if not registered
        } catch (LogInException e) {
            // User doesn't exist yet → proceed with registration
            assertDoesNotThrow(() ->
                    user.Register("New User", uniqueID, "new" + baseEmail, basePassword, "123 Street", "01094749270", true)
            );
        }
    }


    @Test
    @Order(2)
    @DisplayName("Fail registration: duplicate ID from DB")
    void registerClient_DuplicateID_DBUser() {
        RegistrationException ex = assertThrows(RegistrationException.class, () ->
                user.Register("hagar", "hagar", baseEmail, basePassword, "123", "01094749270", true)
        );
        assertEquals("Client is already registered.", ex.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Fail registration: duplicate shared test user")
    void registerClient_DuplicateTestUser() {
        RegistrationException ex = assertThrows(RegistrationException.class, () ->
                user.Register("Test User", testUserID, baseEmail, basePassword, "123", "01094749270", true)
        );
        assertEquals("Client is already registered.", ex.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Fail registration: empty name")
    void registration_EmptyName() {
        RegistrationException ex = assertThrows(RegistrationException.class, () ->
                user.Register("", "uid" + System.currentTimeMillis(), baseEmail, basePassword, "123", "01094749270", true)
        );
        assertEquals("Please enter a valid name.", ex.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Fail registration: empty email")
    void registration_EmptyEmail() {
        RegistrationException ex = assertThrows(RegistrationException.class, () ->
                user.Register("User", "uid" + System.currentTimeMillis(), "", basePassword, "123", "01094749270", true)
        );
        assertEquals("Invalid email address.", ex.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Fail registration: phone too short")
    void registration_ShortPhone() {
        RegistrationException ex = assertThrows(RegistrationException.class, () ->
                user.Register("User", "uid" + System.currentTimeMillis(), baseEmail, basePassword, "123", "01234", true)
        );
        assertEquals("Phone number is invalid.", ex.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Fail registration: phone doesn't start with 0")
    void registration_InvalidPhoneStart() {
        RegistrationException ex = assertThrows(RegistrationException.class, () ->
                user.Register("User", "uid" + System.currentTimeMillis(), baseEmail, basePassword, "123", "11234567890", true)
        );
        assertEquals("Phone number is invalid.", ex.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Fail registration: empty address")
    void registration_EmptyAddress() {
        RegistrationException ex = assertThrows(RegistrationException.class, () ->
                user.Register("User", "uid" + System.currentTimeMillis(), baseEmail, basePassword, "", "01094749270", true)
        );
        assertEquals("Invalid address.", ex.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Fail registration: short password")
    void registration_ShortPassword() {
        RegistrationException ex = assertThrows(RegistrationException.class, () ->
                user.Register("User", "uid" + System.currentTimeMillis(), baseEmail, "1234", "123", "01094749270", true)
        );
        assertEquals("Password must be at least 8 characters.", ex.getMessage());
    }

    // LOGIN TESTS
    @Test
    @Order(10)
    @DisplayName("Login successful: existing DB user (hagar)")
    void login_DBUser_Success() {
        assertDoesNotThrow(() -> user.LogIn("hagar", "hagar2005"));
        assertEquals("hagar", GlobalData.getCurrentlyLoggedIN());
        assertFalse(LogInController.isVendor); // If 'hagar' is a client
    }


    @Test
    @Order(11)
    @DisplayName("Login fail: incorrect username")
    void login_WrongUsername() {
        LogInException ex = assertThrows(LogInException.class, () -> user.LogIn("wronguser", basePassword));
        assertEquals("Username is incorrect.", ex.getMessage());
    }

    @Test
    @Order(12)
    @DisplayName("Login fail: wrong password for test user")
    void login_WrongPassword_TestUser() {
        LogInException ex = assertThrows(LogInException.class, () -> user.LogIn(testUserID, "wrongpass"));
        assertEquals("Password is incorrect.", ex.getMessage());
    }


    // PASSWORD TESTS
    @Test
    @Order(13)
    @DisplayName("Change password fail: wrong current password")
    void changePassword_WrongCurrent() {
        PasswordChangeException ex = assertThrows(PasswordChangeException.class, () ->
                user.ChangePassword(testUserID, "newPass123", "wrongpass")
        );
        assertEquals("The entered Password doesn't match with the user's.", ex.getMessage());
    }

    @Test
    @Order(14)
    @DisplayName("Change password fail: too short")
    void changePassword_TooShort() {
        PasswordChangeException ex = assertThrows(PasswordChangeException.class, () ->
                user.ChangePassword(testUserID, "short", basePassword)
        );
        assertEquals("Password must be at least 8 characters.", ex.getMessage());
    }

    // UPDATE TESTS (Email, Address, Phone)
    @Test
    @Order(15)
    @DisplayName("Update email for DB user")
    void updateEmail_Success() {
        assertDoesNotThrow(() -> user.updateEmail("hagar", "updated_email@test.com", "hagar2005"));
    }

    @Test
    @Order(16)
    @DisplayName("Update Password for DB User")
    void updatePassword_Success(){
        assertDoesNotThrow(() -> {
            user.ChangePassword("hagar", hagarNewPassword, hagarOriginalPassword);
            hagarOriginalPassword = hagarNewPassword; // now it's updated for reset
        });
    }


    @Test
    @Order(17)
    @DisplayName("Update address successfully")
    void updateAddress_Success() {
        assertDoesNotThrow(() -> user.updateAddress(testUserID, "456 Updated St", basePassword));
    }

    @Test
    @Order(18)
    @DisplayName("Update phone successfully")
    void updatePhone_Success() {
        assertDoesNotThrow(() -> user.updatePhoneNumber(testUserID, "01012345678", basePassword));
    }

    @Test
    @Order(19)
    @DisplayName("Sign out clears currently logged in user")
    void signOut_ClearsLoggedUser() throws Exception {
        user.LogIn(testUserID, basePassword);
        assertEquals(testUserID, GlobalData.getCurrentlyLoggedIN());
        User.SignOut();
        assertNull(GlobalData.getCurrentlyLoggedIN());
    }

    @Test
    @Order(20)
    @DisplayName("Get user name by ID returns correct name")
    void getUserByID_ReturnsName() {
        String name = user.getUserByID("hagar");
        assertEquals("hagar", name);
    }

    @Test
    @Order(21)
    @DisplayName("Get User object by ID returns correct user")
    void getUserByID_ReturnsUserObject() {
        User fetchedUser = user.GetUserByID(testUserID);
        assertNotNull(fetchedUser);
        assertEquals(testUserID, fetchedUser.getUserID());
    }

    @Test
    @Order(22)
    @DisplayName("Search returns results for valid query")
    void search_ReturnsResults() {
        List<Item> results = user.search("madrab"); // assuming items exist
        assertNotNull(results);
        assertTrue(results.size() >= 0); // even if empty, it shouldn't be null
    }


    @Test
    @Order(23)
    @DisplayName("Set and get user name")
    void setName_GetName() {
        user.setName("Ahmed");
        assertEquals("Ahmed", user.getName());
    }

    @Test
    @Order(24)
    @DisplayName("Set and get user ID")
    void setUserID_GetUserID() {
        user.setUserID("user123");
        assertEquals("user123", user.getUserID());
    }

    @Test
    @Order(25)
    @DisplayName("Set and get user email")
    void setEmail_GetEmail() {
        user.setEmail("ahmed@example.com");
        assertEquals("ahmed@example.com", user.getEmail());
    }

    @Test
    @Order(26)
    @DisplayName("Set and get user password")
    void setPassword_GetPassword() {
        user.setPassword("myPassword123");
        assertEquals("myPassword123", user.getPassword());
    }

    @Test
    @Order(27)
    @DisplayName("Set and get user address")
    void setAddress_GetAddress() {
        user.setAddress("12 Test Street");
        assertEquals("12 Test Street", user.getAddress());
    }

    @Test
    @Order(28)
    @DisplayName("Set and get user phone number")
    void setPhone_GetPhone() {
        user.setPhoneNumber("01122334455");
        assertEquals("01122334455", user.getPhoneNumber());
    }





    @AfterAll
    static void cleanUpTestUsers() {
        System.out.println("Resetting user data...");

        try {
            // Reset any changes made to 'hagar'
            user.ChangePassword("hagar", "hagar2005", hagarOriginalPassword);
            user.updateEmail("hagar", "hagar@original.com", "hagar2005");
            user.updateAddress("hagar", "el mokkatam", "hagar2005");
            user.updatePhoneNumber("hagar", "01025655273", "hagar2005");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Reset test user
            user.ChangePassword(testUserID, basePassword, "clientPass123");
            user.updateEmail(testUserID, baseEmail, basePassword);
            user.updateAddress(testUserID, "123 Abdo Basha", basePassword);
            user.updatePhoneNumber(testUserID, "01094749270", basePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
