package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.mockito.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;


class UserWhiteBoxxing {
    private FireBaseManager realFireBaseManager;
    private FireBaseManager spyFireBaseManager;
    private User user;

    @BeforeEach
    void setUp() {
        // Use the real FireBaseManager and create a spy
        realFireBaseManager = new FireBaseManager();
        spyFireBaseManager = spy(realFireBaseManager);
        User.fm = spyFireBaseManager;

        Mockito.reset(spyFireBaseManager);
        user = new User();
        // Mock the changeVendorPassword and changeClientPassword methods
        doNothing().when(spyFireBaseManager).changeVendorPassword(anyString(), anyString());
        doNothing().when(spyFireBaseManager).changeClientPassword(anyString(), anyString());
        doNothing().when(spyFireBaseManager).changeClientEmail(anyString(), anyString());
        doNothing().when(spyFireBaseManager).changeVendorEmail(anyString(), anyString());
        doNothing().when(spyFireBaseManager).changeClientAddress(anyString(), anyString());
        doNothing().when(spyFireBaseManager).changeVendorAddress(anyString(), anyString());
        doNothing().when(spyFireBaseManager).changeClientPhoneNumber(anyString(), anyString());
        doNothing().when(spyFireBaseManager).changeVendorPhoneNumber(anyString(), anyString());
    }

    @Test
    @Order(1)
    @DisplayName("Register user successfully with valid credentials")
    void registerUser_Success() throws RegistrationException {
        // Arrange
        String name = "John Doe";
        String userID = "john123";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "01234567890";

        // Ensure the getClient method returns null to simulate a new user
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.Register(name, userID, email, password, address, phone, true));

        // Verify that addClient was called exactly once with any Client
        verify(spyFireBaseManager, times(1)).addClient(any(Client.class));

        // Optional: Verify that getClient was called
        verify(spyFireBaseManager, times(1)).getClient(userID);
    }

    @Test
    @Order(2)
    @DisplayName("Fail to register user with duplicate userID")
    void registerUser_DuplicateUserID_Failure() {
        // Arrange
        String name = "John Doe2";
        String userID = "john123";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "01234567890";

        // Mock getClient to simulate that the user already exists
        doReturn(new Client()).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Client is already registered.", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Fail to register user with invalid password")
    void registerUser_InvalidPassword_Failure() {
        // Arrange
        String name = "John Doe3";
        String userID = "john1234";
        String email = "john.doe@example.com";
        String password = "short";
        String address = "123 Main Street";
        String phone = "01234567890";

        // Act & Assert (no mocking necessary here since the error occurs before interacting with FireBaseManager)
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Password must be at least 8 characters.", exception.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Fail to register user with invalid phone number")
    void registerUser_InvalidPhoneNumber_Failure() {
        // Arrange
        String name = "John Doe4";
        String userID = "john1235";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "12345"; // Invalid phone number

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Phone number is invalid.", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Fail to register user with empty name")
    void registerUser_EmptyName_Failure() {
        // Arrange
        String name = "";
        String userID = "john1236";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "01234567890";

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Please enter a valid name.", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Log in successfully with valid client credentials")
    void login_ValidClient_Success() {
        // Arrange
        String userID = "client1";
        String password = "clientPass";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", password, "123 Street", "01234567890");
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.LogIn(userID, password));
        assertEquals(userID, GlobalData.getCurrentlyLoggedIN());
        assertFalse(LogInController.isVendor); // It should be a client
    }

    @Test
    @Order(7)
    @DisplayName("Log in successfully with valid vendor credentials")
    void login_ValidVendor_Success() {
        // Arrange
        String userID = "vendor1";
        String password = "vendorPass";

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", password, "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.LogIn(userID, password));
        assertEquals(userID, GlobalData.getCurrentlyLoggedIN());
        assertTrue(LogInController.isVendor); // It should be a vendor
    }

    @Test
    @Order(8)
    @DisplayName("Fail to log in with incorrect username")
    void login_InvalidUsername_Failure() {
        // Arrange
        String userID = "invalidUser";
        String password = "somePassword";

        // Mock FireBaseManager to return null for both vendor and client
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        LogInException exception = assertThrows(LogInException.class, () -> user.LogIn(userID, password));
        assertEquals("Username is incorrect.", exception.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Fail to log in with incorrect password for a client")
    void login_InvalidPasswordClient_Failure() {
        // Arrange
        String userID = "client1";
        String invalidPassword = "wrongPass";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass", "123 Street", "01234567890");
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        LogInException exception = assertThrows(LogInException.class, () -> user.LogIn(userID, invalidPassword));
        assertEquals("Password is incorrect.", exception.getMessage());
    }

    @Test
    @Order(10)
    @DisplayName("Fail to log in with incorrect password for a vendor")
    void login_InvalidPasswordVendor_Failure() {
        // Arrange
        String userID = "vendor1";
        String invalidPassword = "wrongPass";

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", "vendorPass", "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        LogInException exception = assertThrows(LogInException.class, () -> user.LogIn(userID, invalidPassword));
        assertEquals("Password is incorrect.", exception.getMessage());
    }

    @Test
    @Order(11)
    @DisplayName("Change password successfully for a client")
    void changePassword_Client_Success() throws PasswordChangeException, AuthException {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newPassword = "newClientPass";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword, "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.ChangePassword(userID, newPassword, currentPassword));

        // Verify that changeClientPassword was called with the correct arguments
        verify(spyFireBaseManager, times(1)).changeClientPassword(userID, newPassword);
    }

//    @Test
//    @Order(12)
//    @DisplayName("Change password successfully for a vendor")
//    void changePassword_Vendor_Success() throws PasswordChangeException, AuthException {
//        // Arrange
//        String userID = "vendor1";
//        String currentPassword = "vendorPass";
//        String newPassword = "newVendorPass";
//
//        // Mock FireBaseManager to return a valid vendor
//        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword, "456 Market", "01234567890");
//        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
//
//        // Act & Assert
//        assertDoesNotThrow(() -> user.ChangePassword(userID, newPassword, currentPassword));
//
//        // Verify that changeVendorPassword was called with the correct arguments
//        verify(spyFireBaseManager, times(1)).changeVendorPassword(userID, newPassword);
//    }

    @Test
    @Order(13)
    @DisplayName("Fail to change password with incorrect current password for a client")
    void changePassword_Client_WrongCurrentPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "wrongPass";
        String newPassword = "newClientPass";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass", "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
                () -> user.ChangePassword(userID, newPassword, currentPassword));
        assertEquals("The entered Password doesn't match with the user's.", exception.getMessage());
    }

    @Test
    @Order(14)
    @DisplayName("Fail to change password with too short new password for a client")
    void changePassword_Client_TooShortNewPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newPassword = "short";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword, "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
                () -> user.ChangePassword(userID, newPassword, currentPassword));
        assertEquals("Password must be at least 8 characters.", exception.getMessage());
    }

    @Test
    @Order(15)
    @DisplayName("Fail to change password for a non-existent user")
    void changePassword_UserNotFound_Failure() {
        // Arrange
        String userID = "unknownUser";
        String currentPassword = "somePassword";
        String newPassword = "newPassword";

        // Mock FireBaseManager to return null for both vendor and client
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
                () -> user.ChangePassword(userID, newPassword, currentPassword));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    @Order(16)
    @DisplayName("Update email successfully for a client")
    void updateEmail_Client_Success() throws EmailAuthenticationException {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newEmail = "newclient@example.com";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword, "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.updateEmail(userID, newEmail, currentPassword));

        // Verify that changeClientEmail was called with the correct arguments
        verify(spyFireBaseManager, times(1)).changeClientEmail(userID, newEmail);
    }

    @Test
    @Order(17)
    @DisplayName("Fail to update email with incorrect password for a client")
    void updateEmail_Client_IncorrectPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "wrongPass";
        String newEmail = "newclient@example.com";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass", "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        EmailAuthenticationException exception = assertThrows(EmailAuthenticationException.class,
                () -> user.updateEmail(userID, newEmail, currentPassword));
        assertEquals("Incorrect current password.", exception.getMessage());
    }

    @Test
    @Order(18)
    @DisplayName("Fail to update email with invalid format")
    void updateEmail_InvalidFormat_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newEmail = "invalidemail";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword, "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        EmailAuthenticationException exception = assertThrows(EmailAuthenticationException.class,
                () -> user.updateEmail(userID, newEmail, currentPassword));
        assertEquals("Invalid email format.", exception.getMessage());
    }

    @Test
    @Order(19)
    @DisplayName("Update address successfully for a client")
    void updateAddress_Client_Success() throws AddressChangeException {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newAddress = "456 Updated St";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword, "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.updateAddress(userID, newAddress, currentPassword));

        // Verify that changeClientAddress was called with the correct arguments
        verify(spyFireBaseManager, times(1)).changeClientAddress(userID, newAddress);
    }

    @Test
    @Order(20)
    @DisplayName("Fail to update address with incorrect password for a client")
    void updateAddress_Client_IncorrectPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "wrongPass";
        String newAddress = "456 Updated St";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass", "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        AddressChangeException exception = assertThrows(AddressChangeException.class,
                () -> user.updateAddress(userID, newAddress, currentPassword));
        assertEquals("Incorrect current password.", exception.getMessage());
    }

    @Test
    @Order(21)
    @DisplayName("Fail to update address with empty address")
    void updateAddress_EmptyAddress_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newAddress = "";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword, "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        AddressChangeException exception = assertThrows(AddressChangeException.class,
                () -> user.updateAddress(userID, newAddress, currentPassword));
        assertEquals("Invalid address.", exception.getMessage());
    }

    @Test
    @Order(22)
    @DisplayName("Update phone number successfully for a client")
    void updatePhoneNumber_Client_Success() throws PhoneNumberException, AuthException {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newPhoneNumber = "01234567890";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword, "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));

        // Verify that changeClientPhoneNumber was called with the correct arguments
        verify(spyFireBaseManager, times(1)).changeClientPhoneNumber(userID, newPhoneNumber);
    }

    @Test
    @Order(23)
    @DisplayName("Fail to update phone number with incorrect password for a client")
    void updatePhoneNumber_Client_IncorrectPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "wrongPass";
        String newPhoneNumber = "01234567890";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass", "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        AuthException exception = assertThrows(AuthException.class,
                () -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));
        assertEquals("The entered Password doesn't match with the user's.", exception.getMessage());
    }

    @Test
    @Order(24)
    @DisplayName("Fail to update phone number with invalid length")
    void updatePhoneNumber_InvalidLength_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newPhoneNumber = "12345"; // Invalid length

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword, "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        PhoneNumberException exception = assertThrows(PhoneNumberException.class,
                () -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));
        assertEquals("Phone number must be exactly 11 characters.", exception.getMessage());
    }

    @Test
    @Order(25)
    @DisplayName("Fail to update email for a non-existent user")
    void updateEmail_NonExistentUser_Failure() {
        // Arrange
        String userID = "unknownUser";
        String currentPassword = "somePassword";
        String newEmail = "newclient@example.com";

        // Mock FireBaseManager to return null for both vendor and client
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        EmailAuthenticationException exception = assertThrows(EmailAuthenticationException.class,
                () -> user.updateEmail(userID, newEmail, currentPassword));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    @Order(26)
    @DisplayName("Fail to update address for a non-existent user")
    void updateAddress_NonExistentUser_Failure() {
        // Arrange
        String userID = "unknownUser";
        String currentPassword = "somePassword";
        String newAddress = "456 Updated St";

        // Mock FireBaseManager to return null for both vendor and client
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        AddressChangeException exception = assertThrows(AddressChangeException.class,
                () -> user.updateAddress(userID, newAddress, currentPassword));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    @Order(27)
    @DisplayName("Fail to update phone number for a non-existent user")
    void updatePhoneNumber_NonExistentUser_Failure() {
        // Arrange
        String userID = "unknownUser";
        String currentPassword = "somePassword";
        String newPhoneNumber = "01234567890";

        // Mock FireBaseManager to return null for both vendor and client
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        AuthException exception = assertThrows(AuthException.class,
                () -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    @Order(28)
    @DisplayName("Register vendor successfully with valid credentials")
    void registerVendor_Success() throws RegistrationException {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor123";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phone = "01234567890";
        boolean type = false; // Indicates registration as a vendor

        // Mock FireBaseManager to simulate no existing vendor with the same userID
        doReturn(null).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.Register(name, userID, email, password, address, phone, type));

        // Verify that addVendor was called exactly once with any Vendor
        verify(spyFireBaseManager, times(1)).addVendor(any(Vendor.class));

        // Verify that getVendor was called exactly once
        verify(spyFireBaseManager, times(1)).getVendor(userID);
    }

    @Test
    @Order(29)
    @DisplayName("Fail to register vendor with duplicate userID")
    void registerVendor_DuplicateUserID_Failure() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor123";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phone = "01234567890";
        boolean type = false; // Indicates registration as a vendor

        // Mock FireBaseManager to simulate an existing vendor with the same userID
        doReturn(new Vendor()).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> user.Register(name, userID, email, password, address, phone, type));
        assertEquals("Vendor is already registered.", exception.getMessage());
    }

    @Test
    @Order(30)
    @DisplayName("Fail to register vendor with invalid password")
    void registerVendor_InvalidPassword_Failure() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor1236";
        String email = "vendor@example.com";
        String password = "short"; // Invalid password (too short)
        String address = "456 Vendor Street";
        String phone = "01234567890";
        boolean type = false; // Indicates registration as a vendor

        // Act & Assert (no mocking necessary since validation fails before interacting with FireBaseManager)
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> user.Register(name, userID, email, password, address, phone, type));
        assertEquals("Password must be at least 8 characters.", exception.getMessage());
    }

    @Test
    @Order(31)
    @DisplayName("Fail to register vendor with invalid phone number")
    void registerVendor_InvalidPhoneNumber_Failure() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor1237";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phone = "12345"; // Invalid phone number
        boolean type = false; // Indicates registration as a vendor

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> user.Register(name, userID, email, password, address, phone, type));
        assertEquals("Phone number is too short.", exception.getMessage());
    }

    @Test
    @Order(32)
    @DisplayName("Fail to register vendor with empty name")
    void registerVendor_EmptyName_Failure() {
        // Arrange
        String name = ""; // Empty name
        String userID = "vendor1234";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phone = "01234567890";
        boolean type = false; // Indicates registration as a vendor

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> user.Register(name, userID, email, password, address, phone, type));
        assertEquals("Please enter a valid name.", exception.getMessage());
    }

    @Test
    @Order(33)
    @DisplayName("Fail to register vendor with invalid address")
    void registerVendor_InvalidAddress_Failure() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor1235";
        String email = "vendor@example.com";
        String password = "password123";
        String address = ""; // Invalid address
        String phone = "01234567890";
        boolean type = false; // Indicates registration as a vendor

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> user.Register(name, userID, email, password, address, phone, type));
        assertEquals("Please fill out your address you dumdummmmmmm", exception.getMessage());
    }

    @Test
    @Order(34)
    @DisplayName("Fail to register vendor with invalid email")
    void registerVendor_InvalidEmail_Failure() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor1238";
        String email = ""; // Invalid email
        String password = "password123";
        String address = "456 Vendor Street";
        String phone = "01234567890";
        boolean type = false; // Indicates registration as a vendor

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> user.Register(name, userID, email, password, address, phone, type));
        assertEquals("Invalid email address.", exception.getMessage());
    }

    @Test
    @Order(35)
    @DisplayName("Change password successfully for a vendor")
    void changePassword_Vendor_Success() throws PasswordChangeException, AuthException {
        // Arrange
        String userID = "vendor123";
        String currentPassword = "vendorPass";
        String newPassword = "newVendorPass";

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword, "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.ChangePassword(userID, newPassword, currentPassword));

        // Verify that changeVendorPassword was called with the correct arguments
        verify(spyFireBaseManager, times(1)).changeVendorPassword(userID, newPassword);
    }

    @Test
    @Order(36)
    @DisplayName("Fail to change password for a vendor with incorrect current password")
    void changePassword_Vendor_IncorrectPassword_Failure() {
        // Arrange
        String userID = "vendor123";
        String currentPassword = "wrongPass";
        String newPassword = "newVendorPass";

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", "vendorPass", "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
                () -> user.ChangePassword(userID, newPassword, currentPassword));
        assertEquals("The entered Password doesn't match with the user's.", exception.getMessage());
    }

    @Test
    @Order(37)
    @DisplayName("Update email successfully for a vendor")
    void updateEmail_Vendor_Success() throws EmailAuthenticationException {
        // Arrange
        String userID = "vendor123";
        String currentPassword = "vendorPass";
        String newEmail = "newvendor@example.com";

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword, "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.updateEmail(userID, newEmail, currentPassword));

        // Verify that changeVendorEmail was called with the correct arguments
        verify(spyFireBaseManager, times(1)).changeVendorEmail(userID, newEmail);
    }

    @Test
    @Order(38)
    @DisplayName("Fail to update email for a vendor with invalid email format")
    void updateEmail_Vendor_InvalidEmailFormat_Failure() {
        // Arrange
        String userID = "vendor123";
        String currentPassword = "vendorPass";
        String newEmail = "invalidemail"; // Invalid email format

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword, "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        EmailAuthenticationException exception = assertThrows(EmailAuthenticationException.class,
                () -> user.updateEmail(userID, newEmail, currentPassword));
        assertEquals("Invalid email format.", exception.getMessage());
    }

    @Test
    @Order(39)
    @DisplayName("Update address successfully for a vendor")
    void updateAddress_Vendor_Success() throws AddressChangeException {
        // Arrange
        String userID = "vendor123";
        String currentPassword = "vendorPass";
        String newAddress = "789 Vendor Plaza";

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword, "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.updateAddress(userID, newAddress, currentPassword));

        // Verify that changeVendorAddress was called with the correct arguments
        verify(spyFireBaseManager, times(1)).changeVendorAddress(userID, newAddress);
    }

    @Test
    @Order(40)
    @DisplayName("Fail to update address for a vendor with incorrect current password")
    void updateAddress_Vendor_IncorrectPassword_Failure() {
        // Arrange
        String userID = "vendor123";
        String currentPassword = "wrongPass";
        String newAddress = "789 Vendor Plaza";

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", "vendorPass", "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        AddressChangeException exception = assertThrows(AddressChangeException.class,
                () -> user.updateAddress(userID, newAddress, currentPassword));
        assertEquals("Incorrect current password.", exception.getMessage());
    }

    @Test
    @Order(41)
    @DisplayName("Update phone number successfully for a vendor")
    void updatePhoneNumber_Vendor_Success() throws PhoneNumberException, AuthException {
        // Arrange
        String userID = "vendor123";
        String currentPassword = "vendorPass";
        String newPhoneNumber = "09876543210";

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword, "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));

        // Verify that changeVendorPhoneNumber was called with the correct arguments
        verify(spyFireBaseManager, times(1)).changeVendorPhoneNumber(userID, newPhoneNumber);
    }

    @Test
    @Order(42)
    @DisplayName("Fail to update phone number for a vendor with invalid phone number")
    void updatePhoneNumber_Vendor_InvalidPhoneNumber_Failure() {
        // Arrange
        String userID = "vendor123";
        String currentPassword = "vendorPass";
        String newPhoneNumber = "12345"; // Invalid phone number length

        // Mock FireBaseManager to return a valid vendor
        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword, "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        PhoneNumberException exception = assertThrows(PhoneNumberException.class,
                () -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));
        assertEquals("Phone number must be exactly 11 characters.", exception.getMessage());
    }

    @Test
    @Order(43)
    @DisplayName("Test getter and setter for name")
    void testGetAndSetName() {
        // Arrange
        String name = "John Doe";

        // Act
        user.setName(name);

        // Assert
        assertEquals(name, user.getName(), "The name should be correctly set and retrieved.");
    }

    @Test
    @Order(44)
    @DisplayName("Test getter and setter for UserID")
    void testGetAndSetUserID() {
        // Arrange
        String userID = "john123";

        // Act
        user.setUserID(userID);

        // Assert
        assertEquals(userID, user.getUserID(), "The UserID should be correctly set and retrieved.");
    }

    @Test
    @Order(45)
    @DisplayName("Test getter and setter for email")
    void testGetAndSetEmail() {
        // Arrange
        String email = "john.doe@example.com";

        // Act
        user.setEmail(email);

        // Assert
        assertEquals(email, user.getEmail(), "The email should be correctly set and retrieved.");
    }

    @Test
    @Order(46)
    @DisplayName("Test getter and setter for password")
    void testGetAndSetPassword() {
        // Arrange
        String password = "securePassword123";

        // Act
        user.setPassword(password);

        // Assert
        assertEquals(password, user.getPassword(), "The password should be correctly set and retrieved.");
    }

    @Test
    @Order(47)
    @DisplayName("Test getter and setter for address")
    void testGetAndSetAddress() {
        // Arrange
        String address = "123 Main Street";

        // Act
        user.setAddress(address);

        // Assert
        assertEquals(address, user.getAddress(), "The address should be correctly set and retrieved.");
    }

    @Test
    @Order(48)
    @DisplayName("Test getter and setter for phone number")
    void testGetAndSetPhoneNumber() {
        // Arrange
        String phoneNumber = "01234567890";

        // Act
        user.setPhoneNumber(phoneNumber);

        // Assert
        assertEquals(phoneNumber, user.getPhoneNumber(), "The phone number should be correctly set and retrieved.");
    }

    @Test
    @Order(49)
    @DisplayName("Test parameterized constructor")
    void testParameterizedConstructor() {
        // Arrange
        String name = "Jane Doe";
        String userID = "jane123";
        String email = "jane.doe@example.com";
        String password = "password456";
        String address = "456 Elm Street";
        String phoneNumber = "09876543210";

        // Act
        User testUser = new User(name, userID, email, password, address, phoneNumber);

        // Assert
        assertEquals(name, testUser.getName(), "The name should be correctly set and retrieved through the constructor.");
        assertEquals(userID, testUser.getUserID(), "The UserID should be correctly set and retrieved through the constructor.");
        assertEquals(email, testUser.getEmail(), "The email should be correctly set and retrieved through the constructor.");
        assertEquals(password, testUser.getPassword(), "The password should be correctly set and retrieved through the constructor.");
        assertEquals(address, testUser.getAddress(), "The address should be correctly set and retrieved through the constructor.");
        assertEquals(phoneNumber, testUser.getPhoneNumber(), "The phone number should be correctly set and retrieved through the constructor.");
    }

    @Test
    @Order(50)
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        // Act
        User testUser = new User();

        // Assert
        assertNull(testUser.getName(), "The default constructor should initialize name to null.");
        assertNull(testUser.getUserID(), "The default constructor should initialize UserID to null.");
        assertNull(testUser.getEmail(), "The default constructor should initialize email to null.");
        assertNull(testUser.getPassword(), "The default constructor should initialize password to null.");
        assertNull(testUser.getAddress(), "The default constructor should initialize address to null.");
        assertNull(testUser.getPhoneNumber(), "The default constructor should initialize phone number to null.");
    }
}