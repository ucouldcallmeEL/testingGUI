package org.example;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All Test Suite")
@SelectClasses({
        ClientTest.class,
        IntegrationTest.class,
        OrderTest.class,
        UserTest.class,
        VendorTest.class
})
public class TestSuite {
    // This class remains empty, it's just a holder for the suite annotations
    // For commit
}