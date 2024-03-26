package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Section;
import com.cst438.domain.SectionRepository;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.test.utils.Constants;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.openqa.selenium.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.cst438.test.utils.TestUtils.fromJsonString;
import static org.junit.jupiter.api.Assertions.*;

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
    public void terminateDriver() throws Exception {
        if (driver != null) {
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test // student enrolls into a section TODO: Reset Test at End
    void studentEnrollment() throws Exception {
        final int desiredSection = 11;
        MockHttpServletResponse response;

        driver.findElement(By.id("addCourse")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        driver.findElement(By.id("enrollButton-" + desiredSection)).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        driver.findElement(By.id("confirmOption")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        String responseMsg = driver.findElement(By.id("msg")).getText();

        assertEquals("Added Course", responseMsg);
    }


}