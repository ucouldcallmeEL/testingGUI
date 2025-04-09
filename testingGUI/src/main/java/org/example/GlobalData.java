package org.example;

public class GlobalData {
    public static String currentlyLoggedIN = null;
    public static String path = "D:/UNI/Junior Year/Semester 6/Software Testing/Project/testingGUI/testingGUI/src/main/resources/org/example/testinggui/";

    public static String getCurrentlyLoggedIN() {
        return currentlyLoggedIN;
    }

    public static void setCurrentlyLoggedIN(String currentlyLoggedIN) {
        GlobalData.currentlyLoggedIN = currentlyLoggedIN;
    }
}
//