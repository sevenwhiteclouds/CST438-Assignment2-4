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
class StudentControllerSystemTests {

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
    void studentEnrollment() throws Exception {
        // TODO: Add predictable Student in DB as a test user. Mock is better but impossible to implement(?)

        driver.findElement(By.id("addCourse")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        driver.findElement(By.id("enrollButton-0")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        driver.findElement(By.id("confirmOption")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
    }

}