package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import com.cst438.test.utils.Constants;


@SpringBootTest
public class EnrollmentControllerTests {
    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {
        System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER_FILE_LOCATION.getValue());
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(ops);

        driver.get(Constants.URL.getValue());
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
    }

    @AfterEach
    public void terminateDriver() {
        if(driver != null) {
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test
    void finalGradeAdd() {

        // TODO
        // verify final grades are added for all enrolled students

    }

}
