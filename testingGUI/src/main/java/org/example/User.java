package org.example;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import java.util.ArrayList;
import java.util.List;


public class User {
    private String name;
    private String UserID;
    private String email;
    private String Password;
    private String address;
    private String PhoneNumber;

    public User() {
    }

    public User(String name, String UserID, String email, String password, String address, String PhoneNumber) {
        this.name = name;
        this.UserID = UserID;
        this.email = email;
        this.Password = password;
        this.address = address;
        this.PhoneNumber = PhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.PhoneNumber = phoneNumber;
    }

    static FireBaseManager fm = FireBaseManager.getInstance();

    public void Register(String name, String userID, String email, String password, String address, String PhoneNumber, boolean type) throws RegistrationException {
        boolean UserNameAccepted = false;
        boolean acceptedPassword = false;
        boolean acceptedPhoneNum = false;
        boolean acceptedEmailAddress = false;
        boolean acceptedAddress = false;


        while (type) {
            while (!UserNameAccepted) {
                if (fm.getClient(userID) != null) {
                    throw new RegistrationException("Client is already registered.");
                }
                if(name.length() == 0){
                    throw new RegistrationException("Please enter a valid name.");
                }
                UserNameAccepted = true;
            }

            while (!acceptedPassword) {
                if (password.length() < 8) {
                    throw new RegistrationException("Password must be at least 8 characters.");
                }
                acceptedPassword = true;
            }

            while (!acceptedPhoneNum) {
                if (PhoneNumber.length() != 11) {
                    throw new RegistrationException("Phone number is invalid.");
                } else if (PhoneNumber.length() == 11 && PhoneNumber.charAt(0) != '0') {
                    throw new RegistrationException("Phone number is invalid.");
                }
                acceptedPhoneNum = true;
            }
            while(!acceptedEmailAddress){
                if(email.length() == 0){
                    throw new RegistrationException("Invalid email address.");
                }
                acceptedEmailAddress = true;
            }

            while(!acceptedAddress){
                if(address.length() == 0){
                    throw new RegistrationException("Invalid address.");
                }
                acceptedAddress = true;
            }

            org.example.Client newClient = new Client(name, userID, email, password, address, PhoneNumber);
            fm.addClient(newClient);
            return;
        }

        while (!type) {
            while (!UserNameAccepted) {
                if (fm.getVendor(userID) != null) {
                    throw new RegistrationException("Vendor is already registered.");
                }
                if(name.length() == 0){
                    throw new RegistrationException("Please enter a valid name.");
                }
                UserNameAccepted = true;
            }

            while (!acceptedPassword) {
                if (password.length() < 8) {
                    throw new RegistrationException("Password must be at least 8 characters.");
                }
                acceptedPassword = true;
            }

            while (!acceptedPhoneNum) {
                if (PhoneNumber.length() < 11) {
                    throw new RegistrationException("Phone number is too short.");
                } else if (PhoneNumber.length() == 11 && PhoneNumber.charAt(0) != '0') {
                    throw new RegistrationException("Phone number is invalid.");
                }
                acceptedPhoneNum = true;
            }

            while(!acceptedEmailAddress){
                if(email.length() == 0){
                    throw new RegistrationException("Invalid email address.");
                }
                acceptedEmailAddress = true;
            }

            while(!acceptedAddress){
                if(address.length() == 0){
                    throw new RegistrationException("Please fill out your address you dumdummmmmmm");
                }
                acceptedAddress = true;
            }

            org.example.Vendor newVendor = new Vendor(name, userID, email, password, address, PhoneNumber);
            fm.addVendor(newVendor);
            return;
        }
    }

    public void LogIn(String UserID, String Password) throws LogInException {
        boolean ValidUserID = false;
        boolean ValidPassword = false;
        User user = fm.getVendor(UserID);

        if (user == null) {
            user = fm.getClient(UserID);
            if (user != null) {
                if (user.getUserID().equals(UserID) && user.getPassword().equals(Password)) {
                    ValidUserID = true;
                    ValidPassword = true;

                } else {
                    throw new LogInException("Password is incorrect.");
                }
            } else {
                throw new LogInException("Username is incorrect.");
            }
        }else {
            if (user.getUserID().equals(UserID) && user.getPassword().equals(Password)) {
                ValidUserID = true;
                ValidPassword = true;
                LogInController.isVendor = true;
            }else {
                throw new LogInException("Password is incorrect.");
            }
        }
        GlobalData.setCurrentlyLoggedIN(UserID);
    }
//
    public static void SignOut(){
        GlobalData.setCurrentlyLoggedIN(null);
    }
    public String getUserByID(String UserID){
        String Username= fm.getUser(UserID).getName();
        return Username;
    }
    public User GetUserByID(String UserID){
        User user = fm.getUser(UserID);
        return user;
    }
    public List<Item> search(String Query){
        List<Item> Items=fm.searchBar(Query);
        return Items;
    }

    public void ChangePassword(String UserID, String Password, String CurrentPassword) throws PasswordChangeException, AuthException {
        boolean ValidUserID = false;
        boolean ValidPassword = false;
        User user = fm.getVendor(UserID);

        if (user == null) {
            user = fm.getClient(UserID);
            if (user != null) {
                ValidUserID = true;
                if (!user.getPassword().equals(CurrentPassword)) {
                    throw new PasswordChangeException("The entered Password doesn't match with the user's.");
                }
                if (Password.length() < 8) {
                    throw new PasswordChangeException("Password must be at least 8 characters.");
                }
                fm.changeClientPassword(UserID, Password);
            }
        } else {
            ValidUserID = true;
            if (!user.getPassword().equals(CurrentPassword)) {
                throw new PasswordChangeException("The entered Password doesn't match with the user's.");
            }
            if (Password.length() < 8) {
                throw new PasswordChangeException("Password must be at least 8 characters.");
            }
            fm.changeVendorPassword(UserID, Password);
        }
    }

    public void updateEmail(String UserID, String newEmail, String currentPassword) throws EmailAuthenticationException {
        boolean ValidUserID = false;
        User user = fm.getVendor(UserID);

        if (user == null) {
            user = fm.getClient(UserID);
            if (user != null) {
                ValidUserID = true;
                if (!user.getPassword().equals(currentPassword)) {
                    throw new EmailAuthenticationException("Incorrect current password.");
                }
                if (!newEmail.contains("@") || !newEmail.contains(".")) {
                    throw new EmailAuthenticationException("Invalid email format.");
                }
                this.email = newEmail;
                fm.changeClientEmail(UserID, newEmail);
                System.out.println("Client email updated successfully.");
            }
        } else {
            ValidUserID = true;
            if (!user.getPassword().equals(currentPassword)) {
                throw new EmailAuthenticationException("Incorrect current password.");
            }
            if (!newEmail.contains("@") || !newEmail.contains(".")) {
                throw new EmailAuthenticationException("Invalid email format.");
            }
            this.email = newEmail;
            fm.changeVendorEmail(UserID, newEmail);
            System.out.println("Vendor email updated successfully.");
        }

        if (!ValidUserID) {
            throw new EmailAuthenticationException("User not found.");
        }
    }

    public void updateAddress(String UserID, String newAddress, String currentPassword) throws AddressChangeException {
        boolean ValidUserID = false;
        User user = fm.getVendor(UserID);

        if (user == null) {
            user = fm.getClient(UserID);
            if (user != null) {
                ValidUserID = true;
                if (!user.getPassword().equals(currentPassword)) {
                    throw new AddressChangeException("Incorrect current password.");
                }
                if (newAddress == null || newAddress.trim().isEmpty()) {
                    throw new AddressChangeException("Invalid address.");
                }
                this.address = newAddress;
                fm.changeClientAddress(UserID, newAddress);
                System.out.println("Client address updated successfully.");
            }
        } else {
            ValidUserID = true;
            if (!user.getPassword().equals(currentPassword)) {
                throw new AddressChangeException("Incorrect current password.");
            }
            if (newAddress == null || newAddress.trim().isEmpty()) {
                throw new AddressChangeException("Invalid address.");
            }
            this.address = newAddress;
            fm.changeVendorAddress(UserID, newAddress);
            System.out.println("Vendor address updated successfully.");
        }

        if (!ValidUserID) {
            throw new AddressChangeException("User not found.");
        }
    }

    public void updatePhoneNumber(String userID, String newPhoneNumber, String currentPassword) throws PhoneNumberException, AuthException {
        boolean ValidUserID = false;
        boolean ValidPhoneNumber = false;
        User user = fm.getVendor(userID);

        if (user == null) {
            user = fm.getClient(userID);
            if (user != null) {
                ValidUserID = true;
                if (!user.getPassword().equals(currentPassword)) {
                    throw new AuthException("The entered Password doesn't match with the user's.");
                }
                if (newPhoneNumber.length() != 11) {
                    throw new PhoneNumberException("Phone number must be exactly 11 characters.");
                }
                ValidPhoneNumber = true;
                fm.changeClientPhoneNumber(userID, newPhoneNumber);
            }
        } else {
            ValidUserID = true;
            if (!user.getPassword().equals(currentPassword)) {
                throw new AuthException("The entered Password doesn't match with the user's.");
            }
            if (newPhoneNumber.length() != 11) {
                throw new PhoneNumberException("Phone number must be exactly 11 characters.");
            }
            ValidPhoneNumber = true;
            fm.changeVendorPhoneNumber(userID, newPhoneNumber);
        }
    }
}

class AddressChangeException extends Exception {
    public AddressChangeException(String message) {
        super(message);
    }
}

class PhoneNumberException extends Exception {
    public PhoneNumberException(String message) {
        super(message);
    }
}

class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
