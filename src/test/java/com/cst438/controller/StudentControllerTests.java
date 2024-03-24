package com.cst438.controller;

import com.cst438.test.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.openqa.selenium.*;


@SpringBootTest
class StudentControllerTests {

    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {

        System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER_FILE_LOCATION.getValue());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);

        driver.get(Constants.URL.getValue());
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
    }

    @AfterEach
    public void terminateDriver() throws Exception {
        if (driver != null) {
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    ///////////////////////// TESTS /////////////////////////////////

    @Test // student enrolls into a section
    void studentEnrollment() {

    }

    @Test // student attempts to enroll in a section but fails because the student is already enrolled
    void studentAlreadyEnrolled() {

    }

    @Test // student attempts to enroll in a section but the section number is invalid
    void studentEnrollmentInvalidSection() {

    }

    @Test // student attempts to enroll in a section, but it is past the add deadline
    void studentEnrollmentPastDeadline() {

    }
}