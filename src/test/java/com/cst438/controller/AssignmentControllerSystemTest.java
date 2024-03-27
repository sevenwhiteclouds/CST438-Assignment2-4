package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.cst438.test.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentControllerSystemTest {

    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {

        // set properties required by Chrome Driver
        System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER_FILE_LOCATION.getValue());
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        // start the driver
        driver = new ChromeDriver(ops);

        driver.get(Constants.URL.getValue());
        // must have a short wait to allow time for the page to download
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
    }

    @AfterEach
    public void terminateDriver() {
        if (driver != null) {
            // quit driver
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void systemAddAssignment() throws Exception {
        // test adding an assignment when there is already assignments present
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("showSections")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        driver.findElement(By.id("viewAssignments")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        driver.findElement(By.id("addNewAssignmentButton")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        driver.findElement(By.name("title")).sendKeys("yessir");
        driver.findElement(By.id("datePickerField")).findElement(By.tagName("input")).click();
        driver.switchTo().activeElement().sendKeys("03152024");
        driver.findElement(By.id("saveButton")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        assertEquals("Assignment added", driver.findElement(By.tagName("h4")).getText());

        List<WebElement> list = driver.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
        WebElement element = list.get(list.size() - 1);
        element.findElement(By.id("deleteButton")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        driver.findElement(By.className("react-confirm-alert-button-group")).findElement(By.tagName("button")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        assertEquals("Assignment deleted", driver.findElement(By.tagName("h4")).getText());

        // test adding an assignment when there is no assignments present
        driver.navigate().back();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        list = driver.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
        element = list.get(list.size() - 2);
        element.findElement(By.id("viewAssignments")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        driver.findElement(By.id("addNewAssignmentButton")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        driver.findElement(By.name("title")).sendKeys("yessir2");
        driver.findElement(By.id("datePickerField")).findElement(By.tagName("input")).click();
        driver.switchTo().activeElement().sendKeys("03172024");
        driver.findElement(By.id("saveButton")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        assertEquals("Assignment added", driver.findElement(By.tagName("h4")).getText());

        driver.findElement(By.id("deleteButton")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        driver.findElement(By.className("react-confirm-alert-button-group")).findElement(By.tagName("button")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
        assertEquals("Assignment deleted", driver.findElement(By.tagName("h4")).getText());
    }

    // System test to grade an assignment
    @Test
    public void systemTestGradeAssignment() throws Exception {

        // Enter a Year and Semester
        driver.findElement(By.id("year")).sendKeys("2024"); // Chose a valid year to enter
        driver.findElement(By.id("semester")).sendKeys("Spring"); // Chose a valid semester to enter
        driver.findElement(By.id("showSections")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        // Click "View Assignments" for first row (section) of table
        // year and semester entered above for this test guarantees table results contain data
        driver.findElement(By.id("viewAssignments")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        // Click "Show Grades" for first row (assignment) of table
        // selecting the first section above for this test guarantees table results contain data
        driver.findElement(By.id("showGrades")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        WebElement table = driver.findElement(By.xpath("//tbody"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        List<String> currentScore = new ArrayList<>();

        // Loop through all rows in table of Grade records.
        // Update the score to something different from what is currently there (30).
        for (WebElement row : rows) {
            WebElement inputScore = row.findElement(By.id("scoreInput"));

            // Save current scores so they can be put back at end of test
            currentScore.add(inputScore.getAttribute("value"));

            // Delete the score that is currently there
            inputScore.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));

            // Update the existing score to something other than what is currently there
            row.findElement(By.id("scoreInput")).sendKeys("30");
            // Click "Update" for the Grade record
            row.findElement(By.id("updateButton")).click();
            Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

            // Assert message indicates new score was saved
            String message = driver.findElement(By.id("addMessage")).getText();
            assertTrue(message.startsWith("Grade saved"));
        }

         //Click "Back" to go back to list of Assignments
        driver.findElement(By.id("backButton")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
         //Click "Show Grades" again for first row (assignment) of table
        driver.findElement(By.id("showGrades")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        WebElement table2 = driver.findElement(By.xpath("//tbody"));
        List<WebElement> rows2 = table2.findElements(By.tagName("tr"));

        int i = 0;
        // Assert that the new score of 30 entered above was saved to each Grade record.
        // Then put back to previous score once asserted.
        for (WebElement row: rows2) {
            WebElement inputNewScore = row.findElement(By.id("scoreInput"));
            assertEquals("30", inputNewScore.getAttribute("value"));

            // Set score back to what it was before
            inputNewScore.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
            row.findElement(By.id("scoreInput")).sendKeys(currentScore.get(i));
            row.findElement(By.id("updateButton")).click();
            Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
            i++;
        }
    }
}