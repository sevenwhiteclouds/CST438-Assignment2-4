package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.cst438.test.utils.Constants;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnrollmentControllerSystemTest {

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
    void finalGradeAdd() throws Exception {

        // Enter 2024 Spring and then click "Show Sections" on Instructor home page
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("showSections")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        // clicks "View Enrollments"
        driver.findElement(By.id("viewEnrollments")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        // get original grade
        String og = driver.findElement(By.id("grade")).getAttribute("value");

        // enters "A" in Grade text box and clicks "UPDATE"
        driver.findElement(By.id("grade")).clear();
        driver.findElement(By.id("grade")).sendKeys("A");
        driver.findElement(By.id("update")).click();

        // confirms message says "Enrollment saved"
        String message = driver.findElement(By.id("e_message")).getText();
        assertTrue(message.startsWith("Enrollment saved"));
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        // return Grade back to original grade
        driver.findElement(By.id("grade")).clear();
        driver.findElement(By.id("grade")).sendKeys(og);
        driver.findElement(By.id("update")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        // confirms message says "Enrollment saved"
        String msg = driver.findElement(By.id("e_message")).getText();
        assertTrue(msg.startsWith("Enrollment saved"));
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

    }

}