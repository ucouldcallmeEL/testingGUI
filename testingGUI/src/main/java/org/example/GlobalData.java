package org.example;

public class GlobalData {
    public static String currentlyLoggedIN = null;
    public static String currentEditingProductId;
    public static String path = "D:/UNI/Junior Year/Semester 6/Software Testing/Project/GitVersion/testingGUI/src/main/resources/org/example/testinggui/";

    public static String getCurrentlyLoggedIN() {
        return currentlyLoggedIN;
    }

    public static void setCurrentlyLoggedIN(String currentlyLoggedIN) {
        GlobalData.currentlyLoggedIN = currentlyLoggedIN;
    }

    public static String getCurrentEditingProductId() {
        return currentEditingProductId;
    }

    public static void setCurrentEditingProductId(String productId) {
        currentEditingProductId = productId;
    }
}
