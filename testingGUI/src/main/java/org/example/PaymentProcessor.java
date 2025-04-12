package org.example;

public class PaymentProcessor {
    private String UserID;
    private String CardNumber;
    private String CVV;

    public PaymentProcessor(String UserID, String CardNumber, String CardExpiry) {
        this.UserID = UserID;
        this.CardNumber = CardNumber;
        this.CVV = CardExpiry;
    }
    public PaymentProcessor() {

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

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
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
