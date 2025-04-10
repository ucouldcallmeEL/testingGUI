package org.example;

public class PaymentController {
    private String UserID;
    private String CardNumber;
    private String CardExpiry;

    public PaymentController(String UserID, String CardNumber, String CardExpiry) {
        this.UserID = UserID;
        this.CardNumber = CardNumber;
        this.CardExpiry = CardExpiry;
    }
    public PaymentController() {

    }

    public String getUserID() {
        return UserID;
    }
    public void setUserID(String userID) {
        UserID = userID;
    }
    public String getCardNumber() {
        return CardNumber;
    }
    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }
    public String getCardExpiry() {
        return CardExpiry;
    }
    public void setCardExpiry(String cardExpiry) {
        CardExpiry = cardExpiry;
    }

    public boolean isCardValid() throws PaymentException {
        if(CardNumber != null && CardNumber.matches("\\d{16}")){
            return true;
        }else{
            throw new PaymentException("Invalid card credentials.");
        }
    }

    public boolean pay(String amount) throws PaymentException {
        if (!isCardValid()) {
            throw new PaymentException("Card is not valid.");
        }else{
            return true;
        }
    }

}
