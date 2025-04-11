package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class EditProfileController {
    
    @FXML
    private Label UsernameLabel;
    @FXML
    private Label NameLabel;
    @FXML
    private TextField EmailFeild;
    @FXML
    private TextField PhoneNumberField;
    @FXML
    private TextField AddressField;
    @FXML
    private TextField PasswordField;
    @FXML
    private Label EditProfileError;
    
    private String userID;
    private User user;

    FireBaseManager fm = FireBaseManager.getInstance();

    @FXML
    public void initialize() {
        this.userID = GlobalData.getCurrentlyLoggedIN();
        UsernameLabel.setText(this.userID);
        this.user = fm.getUser(GlobalData.getCurrentlyLoggedIN());
        NameLabel.setText(this.user.getName());
        loadUserData();

//        this.userID = GlobalData.getCurrentEditingProductId();
//
//        if (this.userID != null) {
//            user userFetcher = new user();
//            this.user = userFetcher.getuserbyID(this.userID);            loaduserData();
//        } else {
//            System.out.println("userID is null in EditProductController");
//        }
    }


    // New method to load user data into the form
    private void loadUserData() {
        if (this.user != null) {
            // Fetch existing data from the user
            String existingEmailFeild = this.user.getEmail();
            String existingPhoneNumberField = this.user.getPhoneNumber();
            String existingAddressField = this.user.getAddress();
            String existingPasswordField = this.user.getPassword();

            // Pre-fill the TextFields with the existing data
            EmailFeild.setText(existingEmailFeild);
            PhoneNumberField.setText(existingPhoneNumberField);
            AddressField.setText(existingAddressField);
            PasswordField.setText(existingPasswordField);
        }
    }

    @FXML
    private void handleSaveChangesButton(ActionEvent event) throws IOException {
        String email = EmailFeild.getText();
        String phone = PhoneNumberField.getText();
        String address = AddressField.getText();
        String password = PasswordField.getText();

        try {
            this.user.updateEmail(this.userID, email, this.user.getPassword());
            this.user.updatePhoneNumber(this.userID, phone, this.user.getPassword());
            this.user.updateAddress(this.userID, address, this.user.getPassword());
            this.user.ChangePassword(this.userID, password, this.user.getPassword());

            EditProfileError.setStyle("-fx-text-fill: green;");
            EditProfileError.setText("Update Successful!");

            User type = fm.getVendor(this.userID);
            if (type != null) {
                SceneController.switchScene(event, "MainVendorPage.fxml", "Homepage");
            } else {
                SceneController.switchScene(event, "MainPageClient.fxml", "Homepage");
            }
        } catch (PhoneNumberException e) {
            EditProfileError.setText(e.getMessage());
        } catch (AddressChangeException e) {
            EditProfileError.setText(e.getMessage());
        } catch (EmailAuthenticationException e) {
            EditProfileError.setText(e.getMessage());
        } catch (PasswordChangeException e) {
            EditProfileError.setText(e.getMessage());
        } catch (IOException ex) {
            EditProfileError.setText(ex.getMessage());
        } catch (Exception ex) {
            EditProfileError.setText(ex.getMessage());
        }
    }
}
