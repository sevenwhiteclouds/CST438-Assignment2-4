package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.SectionRepository;
import com.cst438.test.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureMockMvc
@SpringBootTest
class StudentControllerSystemTests {

    WebDriver driver;

    @Autowired
    MockMvc mvc;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    SectionRepository sectionRepository;

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
    public void terminateDriver() {
        if (driver != null) {
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test // student enrolls into a section TODO: View schedule to verify it was added.
    void studentEnrollment() throws Exception {
        final String desiredSection = "11";

        driver.findElement(By.id("addCourse")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        driver.findElement(By.id("enrollButton-" + desiredSection)).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        driver.findElement(By.id("confirmOption")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        String responseMsg = driver.findElement(By.id("msg")).getText();

        assertEquals("Added Course", responseMsg);

        // Check if enrollment actually occurred

        driver.findElement(By.id("viewSchedule")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        driver.findElement((By.id("qYear"))).sendKeys("2025");
        driver.findElement((By.id("qSem"))).sendKeys("Spring");
        driver.findElement((By.id("query"))).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        WebElement secId = driver.findElement(By.id("secNo" + desiredSection));
        assertEquals(desiredSection, secId.getText());

        // Remove enrollment to reset test
        driver.findElement((By.id("drop" + desiredSection))).click();
        driver.findElement((By.id("confirm"))).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

    }


}