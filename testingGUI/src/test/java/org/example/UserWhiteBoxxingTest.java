package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.mockito.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;


class UserWhiteBoxxingTest {
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
        String name = "John Doe";
        String userID = "john123";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "01234567890";
        doReturn(null).when(spyFireBaseManager).getClient(userID);
        assertDoesNotThrow(() -> user.Register(name, userID, email, password, address, phone, true));
        verify(spyFireBaseManager, times(1)).addClient(any(Client.class));
        verify(spyFireBaseManager, times(1)).getClient(userID);
    }

    @Test
    @Order(2)
    @DisplayName("Fail to register user with duplicate userID")
    void registerUser_DuplicateUserID_Failure() {
        String name = "John Doe2";
        String userID = "john123";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "01234567890";
        doReturn(new Client()).when(spyFireBaseManager).getClient(userID);
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Client is already registered.", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Fail to register user with invalid password")
    void registerUser_InvalidPassword_Failure() {
        String name = "John Doe3";
        String userID = "john1234";
        String email = "john.doe@example.com";
        String password = "short";
        String address = "123 Main Street";
        String phone = "01234567890";
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
        String phone = "12345";
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

        Client mockClient = new Client("Client Name", userID, "client@example.com", password, "123 Street",
                "01234567890");
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", password, "456 Market",
                "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);
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

        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);
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

        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass",
                "123 Street", "01234567890");
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", "vendorPass",
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);
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

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
        assertDoesNotThrow(() -> user.ChangePassword(userID, newPassword, currentPassword));
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
    @Order(12)
    @DisplayName("Fail to change password with incorrect current password for a client")
    void changePassword_Client_WrongCurrentPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "wrongPass";
        String newPassword = "newClientPass";

        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass",
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
                () -> user.ChangePassword(userID, newPassword, currentPassword));
        assertEquals("The entered Password doesn't match with the user's.", exception.getMessage());
    }

    @Test
    @Order(13)
    @DisplayName("Fail to change password with too short new password for a client")
    void changePassword_Client_TooShortNewPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newPassword = "short";

        // Mock FireBaseManager to return a valid client
        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
                () -> user.ChangePassword(userID, newPassword, currentPassword));
        assertEquals("Password must be at least 8 characters.", exception.getMessage());
    }

    @Test
    @Order(14)
    @DisplayName("Fail to change password for a non-existent user")
    void changePassword_UserNotFound_Failure() {
        // Arrange
        String userID = "unknownUser";
        String currentPassword = "somePassword";
        String newPassword = "newPassword";

        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);
        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
                () -> user.ChangePassword(userID, newPassword, currentPassword));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    @Order(15)
    @DisplayName("Update email successfully for a client")
    void updateEmail_Client_Success() throws EmailAuthenticationException {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newEmail = "newclient@example.com";

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
        assertDoesNotThrow(() -> user.updateEmail(userID, newEmail, currentPassword));
        verify(spyFireBaseManager, times(1)).changeClientEmail(userID, newEmail);
    }

    @Test
    @Order(16)
    @DisplayName("Fail to update email with incorrect password for a client")
    void updateEmail_Client_IncorrectPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "wrongPass";
        String newEmail = "newclient@example.com";

        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass",
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
        EmailAuthenticationException exception = assertThrows(EmailAuthenticationException.class,
                () -> user.updateEmail(userID, newEmail, currentPassword));
        assertEquals("Incorrect current password.", exception.getMessage());
    }

    @Test
    @Order(17)
    @DisplayName("Fail to update email with invalid format")
    void updateEmail_InvalidFormat_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newEmail = "invalidemail";

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
        EmailAuthenticationException exception = assertThrows(EmailAuthenticationException.class,
                () -> user.updateEmail(userID, newEmail, currentPassword));
        assertEquals("Invalid email format.", exception.getMessage());
    }

    @Test
    @Order(18)
    @DisplayName("Update address successfully for a client")
    void updateAddress_Client_Success() throws AddressChangeException {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newAddress = "456 Updated St";

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
        assertDoesNotThrow(() -> user.updateAddress(userID, newAddress, currentPassword));
        verify(spyFireBaseManager, times(1)).changeClientAddress(userID, newAddress);
    }

    @Test
    @Order(19)
    @DisplayName("Fail to update address with incorrect password for a client")
    void updateAddress_Client_IncorrectPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "wrongPass";
        String newAddress = "456 Updated St";

        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass",
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
        AddressChangeException exception = assertThrows(AddressChangeException.class,
                () -> user.updateAddress(userID, newAddress, currentPassword));
        assertEquals("Incorrect current password.", exception.getMessage());
    }

    @Test
    @Order(20)
    @DisplayName("Fail to update address with empty address")
    void updateAddress_EmptyAddress_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newAddress = "";

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
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

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
        assertDoesNotThrow(() -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));
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

        Client mockClient = new Client("Client Name", userID, "client@example.com", "clientPass",
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
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

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);
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

        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);
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

        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);
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

        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);
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
        boolean type = false;
        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        assertDoesNotThrow(() -> user.Register(name, userID, email, password, address, phone, type));
        verify(spyFireBaseManager, times(1)).addVendor(any(Vendor.class));
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
        boolean type = false;
        doReturn(new Vendor()).when(spyFireBaseManager).getVendor(userID);
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
        boolean type = false;
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
        String phone = "12345";
        boolean type = false;
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
        boolean type = false;
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
        boolean type = false;
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
        boolean type = false;
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword,
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
        assertDoesNotThrow(() -> user.ChangePassword(userID, newPassword, currentPassword));
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", "vendorPass",
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword,
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
        assertDoesNotThrow(() -> user.updateEmail(userID, newEmail, currentPassword));
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword,
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword,
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
        assertDoesNotThrow(() -> user.updateAddress(userID, newAddress, currentPassword));
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", "vendorPass",
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword,
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
        assertDoesNotThrow(() -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));
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

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", currentPassword,
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);
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
    @Test
    @Order(51)
    @DisplayName("Test Register with invalid email for client")
    void registerUser_InvalidEmail_Failure() {
        // Arrange
        String name = "John Doe";
        String userID = "john1238";
        String email = ""; // Invalid email
        String password = "password123";
        String address = "123 Main Street";
        String phone = "01234567890";

        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Invalid email address.", exception.getMessage());
    }

    @Test
    @Order(52)
    @DisplayName("Test Register with invalid address for client")
    void registerUser_InvalidAddress_Failure() {
        // Arrange
        String name = "John Doe";
        String userID = "john1239";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = ""; // Invalid address
        String phone = "01234567890";

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Invalid address.", exception.getMessage());
    }

    @Test
    @Order(53)
    @DisplayName("Test LogIn with empty password for vendor")
    void login_EmptyPassword_Failure() {
        // Arrange
        String userID = "vendor1";
        String password = ""; // Empty password

        Vendor mockVendor = new Vendor("Vendor Name", userID, "vendor@example.com", "vendorPass",
                "456 Market", "01234567890");
        doReturn(mockVendor).when(spyFireBaseManager).getVendor(userID);

        // Act & Assert
        LogInException exception = assertThrows(LogInException.class, () -> user.LogIn(userID, password));
        assertEquals("Password is incorrect.", exception.getMessage());
    }

    @Test
    @Order(54)
    @DisplayName("Test LogIn with empty username")
    void login_EmptyUsername_Failure() {
        // Arrange
        String userID = ""; // Empty username
        String password = "somePassword";

        doReturn(null).when(spyFireBaseManager).getVendor(userID);
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        LogInException exception = assertThrows(LogInException.class, () -> user.LogIn(userID, password));
        assertEquals("Username is incorrect.", exception.getMessage());
    }

    @Test
    @Order(55)
    @DisplayName("Test ChangePassword with empty new password")
    void changePassword_EmptyNewPassword_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newPassword = ""; // Empty new password

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
                () -> user.ChangePassword(userID, newPassword, currentPassword));
        assertEquals("Password must be at least 8 characters.", exception.getMessage());
    }

    @Test
    @Order(56)
    @DisplayName("Test UpdateEmail for null email")
    void updateEmail_NullEmail_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newEmail = null; // Null email

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        EmailAuthenticationException exception = assertThrows(EmailAuthenticationException.class,
                () -> user.updateEmail(userID, newEmail, currentPassword));
        assertEquals("Invalid email format.", exception.getMessage());
    }

    @Test
    @Order(57)
    @DisplayName("Test UpdatePhoneNumber for null phone number")
    void updatePhoneNumber_NullPhoneNumber_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newPhoneNumber = null; // Null phone number

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        PhoneNumberException exception = assertThrows(PhoneNumberException.class,
                () -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));
        assertEquals("Phone number must be exactly 11 characters.", exception.getMessage());
    }

    @Test
    @Order(58)
    @DisplayName("Test UpdateAddress for null address")
    void updateAddress_NullAddress_Failure() {
        // Arrange
        String userID = "client1";
        String currentPassword = "clientPass";
        String newAddress = null; // Null address

        Client mockClient = new Client("Client Name", userID, "client@example.com", currentPassword,
                "123 Street", "01234567890");
        doReturn(mockClient).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        AddressChangeException exception = assertThrows(AddressChangeException.class,
                () -> user.updateAddress(userID, newAddress, currentPassword));
        assertEquals("Invalid address.", exception.getMessage());
    }

    @Test
    @Order(59)
    @DisplayName("Test SignOut functionality")
    void testSignOut() {
        // Arrange
        GlobalData.setCurrentlyLoggedIN("client1");

        // Act
        User.SignOut();

        // Assert
        assertNull(GlobalData.getCurrentlyLoggedIN(), "User should be signed out.");
    }

    @Test
    @Order(60)
    @DisplayName("Test SignOut sets currently logged in user to null")
    void testSignOut_userNull() {
        // Arrange
        GlobalData.setCurrentlyLoggedIN("user123"); // Simulate a logged-in user
        User.SignOut();
        assertNull(GlobalData.getCurrentlyLoggedIN(), "The currently logged-in user should be null after SignOut.");
    }

    @Test
    @Order(61)
    @DisplayName("Test getUserByID returns the correct username")
    void testGetUserByID() {
        // Arrange
        String userID = "user123";
        String expectedUsername = "John Doe";

        User mockUser = mock(User.class);
        when(spyFireBaseManager.getUser(userID)).thenReturn(mockUser);
        when(mockUser.getName()).thenReturn(expectedUsername);

        // Act
        String actualUsername = user.getUserByID(userID);
        assertEquals(expectedUsername, actualUsername, "The username returned should match the expected username.");
    }

    @Test
    @Order(62)
    @DisplayName("Test getUser returns the correct user object")
    void testGetUser() {
        // Arrange
        String username = "user123";
        User mockUser = mock(User.class);

        when(spyFireBaseManager.getUser(username)).thenReturn(mockUser);

        // Act
        User actualUser = user.getUser(username);

        // Assert
        assertEquals(mockUser, actualUser, "The user object returned should match the mocked user object.");
    }

    @Test
    @Order(63)
    @DisplayName("Test GetUserByID returns the correct user object")
    void testGetUserByID_Object() {
        // Arrange
        String userID = "user123";
        User mockUser = mock(User.class);

        when(spyFireBaseManager.getUser(userID)).thenReturn(mockUser);

        // Act
        User actualUser = user.GetUserByID(userID);

        // Assert
        assertEquals(mockUser, actualUser, "The user object returned should match the mocked user object.");
    }

    @Test
    @Order(64)
    @DisplayName("Test search returns the correct list of items")
    void testSearch() {
        // Arrange
        String query = "sample query";
        List<Item> mockItems = new ArrayList<>();
        mockItems.add(mock(Item.class));
        mockItems.add(mock(Item.class));

        when(spyFireBaseManager.searchBar(query)).thenReturn(mockItems);

        // Act
        List<Item> actualItems = user.search(query);

        // Assert
        assertEquals(mockItems, actualItems, "The list of items returned should match the mocked list.");
    }

    @Test
    @Order(65)
    @DisplayName("Test Register with invalid phone number length")
    void registerUser_InvalidPhoneNumberLength_Failure() {
        // Arrange
        String name = "John Doe";
        String userID = "user1235";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "12345"; // Invalid length

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Phone number is invalid.", exception.getMessage());
    }

    @Test
    @Order(66)
    @DisplayName("Test Register with valid length but invalid starting digit")
    void registerUser_InvalidPhoneNumberStartingDigit_Failure() {
        // Arrange
        String name = "John Doe";
        String userID = "user12333";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "12345678901"; // Starts with 1 instead of 0

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, true));
        assertEquals("Phone number is invalid.", exception.getMessage());
    }

    @Test
    @Order(67)
    @DisplayName("Test Register with valid phone number")
    void registerUser_ValidPhoneNumber_Success() throws RegistrationException {
        // Arrange
        String name = "John Doe";
        String userID = "user123";
        String email = "john.doe@example.com";
        String password = "password123";
        String address = "123 Main Street";
        String phone = "01234567890"; // Valid phone number

        // Mocking FireBaseManager behavior
        doReturn(null).when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        assertDoesNotThrow(() -> user.Register(name, userID, email, password, address, phone, true));
        verify(spyFireBaseManager, times(1)).addClient(any(Client.class));
    }

    @Test
    @Order(68)
    @DisplayName("Test Register with valid length but invalid starting digit for vendor")
    void registerVendor_InvalidStartingDigitPhoneNumber_Failure() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor12333";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phone = "12345678901"; // Starts with 1 instead of 0
        boolean type = false; // Indicates vendor

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, type));
        assertEquals("Phone number is invalid.", exception.getMessage());
    }


    @Test
    @Order(69)
    @DisplayName("Test Register with null phone for vendor")
    void registerVendor_NullPhone_Failure() {
        // Arrange
        String name = "Vendor Name";
        String userID = "vendor12355";
        String email = "vendor@example.com";
        String password = "password123";
        String address = "456 Vendor Street";
        String phone = null; // Null phone number
        boolean type = false;

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                user.Register(name, userID, email, password, address, phone, type));
        assertEquals("Phone number is too short.", exception.getMessage());
    }

    @Test
    @Order(70)
    @DisplayName("Test LogIn with null UserID")
    void login_NullUserID_Failure() {
        // Arrange
        String userID = null; // Null UserID
        String password = "password123";

        // Act & Assert
        LogInException exception = assertThrows(LogInException.class, () -> user.LogIn(userID, password));
        assertEquals("Username is incorrect.", exception.getMessage());
    }

    @Test
    @Order(71)
    @DisplayName("Test LogIn with null Password")
    void login_NullPassword_Failure() {
        // Arrange
        String userID = "user123";
        String password = null; // Null password

        doReturn(new Client("Client Name", userID, "client@example.com", "password123", "123 Street", "01234567890"))
                .when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        LogInException exception = assertThrows(LogInException.class, () -> user.LogIn(userID, password));
        assertEquals("Password is incorrect.", exception.getMessage());
    }

//    @Test
//    @Order(71)
//    @DisplayName("Test ChangePassword with null new password")
//    void changePassword_NullNewPassword_Failure() {
//        // Arrange
//        String userID = "user123";
//        String currentPassword = "password123";
//        String newPassword = null; // Null new password
//
//        doReturn(new Client("Client Name", userID, "client@example.com", "password123", "123 Street", "01234567890"))
//                .when(spyFireBaseManager).getClient(userID);
//
//        // Act & Assert
//        PasswordChangeException exception = assertThrows(PasswordChangeException.class,
//                () -> user.ChangePassword(userID, newPassword, currentPassword));
//        assertEquals("Password must be at least 8 characters.", exception.getMessage());
//    }

    @Test
    @Order(72)
    @DisplayName("Test UpdateEmail with null current password")
    void updateEmail_NullCurrentPassword_Failure() {
        // Arrange
        String userID = "user123";
        String currentPassword = null; // Null current password
        String newEmail = "new@example.com";

        doReturn(new Client("Client Name", userID, "client@example.com", "password123", "123 Street", "01234567890"))
                .when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        EmailAuthenticationException exception = assertThrows(EmailAuthenticationException.class,
                () -> user.updateEmail(userID, newEmail, currentPassword));
        assertEquals("Incorrect current password.", exception.getMessage());
    }

    @Test
    @Order(73)
    @DisplayName("Test UpdateAddress with null current password")
    void updateAddress_NullCurrentPassword_Failure() {
        // Arrange
        String userID = "user123";
        String currentPassword = null; // Null current password
        String newAddress = "456 Updated Street";

        doReturn(new Client("Client Name", userID, "client@example.com", "password123", "123 Street", "01234567890"))
                .when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        AddressChangeException exception = assertThrows(AddressChangeException.class,
                () -> user.updateAddress(userID, newAddress, currentPassword));
        assertEquals("Incorrect current password.", exception.getMessage());
    }

    @Test
    @Order(74)
    @DisplayName("Test UpdatePhoneNumber with null current password")
    void updatePhoneNumber_NullCurrentPassword_Failure() {
        // Arrange
        String userID = "user123";
        String currentPassword = null; // Null current password
        String newPhoneNumber = "01234567890";

        doReturn(new Client("Client Name", userID, "client@example.com", "password123", "123 Street", "01234567890"))
                .when(spyFireBaseManager).getClient(userID);

        // Act & Assert
        AuthException exception = assertThrows(AuthException.class,
                () -> user.updatePhoneNumber(userID, newPhoneNumber, currentPassword));
        assertEquals("The entered Password doesn't match with the user's.", exception.getMessage());
    }


}