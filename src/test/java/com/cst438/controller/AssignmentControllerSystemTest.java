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
        System.setProperty(
                "webdriver.chrome.driver", Constants.CHROME_DRIVER_FILE_LOCATION.getValue());
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
        // with current data, there is only 1 grade record
        driver.findElement(By.id("showGrades")).click();
        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        // For the first Grade record in the table, save the current score in a variable
//        WebElement inputScore = driver.findElement(By.id("scoreInput"));
//        String currentScore = inputScore.getAttribute("value");

        WebElement table = driver.findElement(By.xpath("//tbody"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        List<String> currentScore = new ArrayList<>();
        for (WebElement row : rows) {
            WebElement inputScore = row.findElement(By.id("scoreInput"));
            currentScore.add(inputScore.getAttribute("value"));
            //String currentScore = inputScore.getAttribute("value");
            // Delete the score that is currently there
            inputScore.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
            // Update the existing score to something other than what is currently there
            row.findElement(By.id("scoreInput")).sendKeys("30");
            // Click "Update" for the first Grade record
            row.findElement(By.id("updateButton")).click();
            Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
            // Assert message indicates new score was saved
            String message = driver.findElement(By.id("addMessage")).getText();
            assertTrue(message.startsWith("Grade saved"));
            // Click "Back" to go back to list of Assignments
            //driver.findElement(By.id("backButton")).click();
            //Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
            // Click "Show Grades" again for first row (assignment) of table
            //driver.findElement(By.id("showGrades")).click();
            //Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
            // Confirm score saved as the new input above
            // For the first Grade record in the table, it should show the new score that was entered
            //WebElement inputNewScore = row.findElement(By.id("scoreInput"));
            //String newScore = inputNewScore.getAttribute("value");
            //assertEquals("30", newScore);
            // Set score back to what it was before
//            WebElement inputScore2 = row.findElement(By.id("scoreInput"));
//            inputScore2.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
//            row.findElement(By.id("scoreInput")).sendKeys(currentScore);
//            row.findElement(By.id("updateButton")).click();
//            Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
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
        for (WebElement row: rows2) {
            WebElement inputNewScore = row.findElement(By.id("scoreInput"));
            assertEquals("30", inputNewScore.getAttribute("value"));
            // Set score back to what it was before
            //WebElement inputScore2 = row.findElement(By.id("scoreInput"));
            inputNewScore.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
            row.findElement(By.id("scoreInput")).sendKeys(currentScore.get(i));
            row.findElement(By.id("updateButton")).click();
            Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
            i++;
        }

//        // Delete the score that is currently there
//        inputScore.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));

//        // Update the existing score to something other than what is currently there
////        driver.findElement(By.id("scoreInput")).sendKeys("30");
////        // Click "Update" for the first Grade record
////        driver.findElement(By.id("updateButton")).click();
////        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

//        // Assert message indicates new score was saved
//        String message = driver.findElement(By.id("addMessage")).getText();
//        assertTrue(message.startsWith("Grade saved"));

        // Click "Back" to go back to list of Assignments
//        driver.findElement(By.id("backButton")).click();
//        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

        // Click "Show Grades" again for first row (assignment) of table
//        driver.findElement(By.id("showGrades")).click();
//        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());

//        // Confirm score saved as the new input above
//        // For the first Grade record in the table, it should show the new score that was entered
//        WebElement inputNewScore = driver.findElement(By.id("scoreInput"));
//        String newScore = inputNewScore.getAttribute("value");
//        assertEquals("30", newScore);

        // Set score back to what it was before
//        WebElement inputScore2 = driver.findElement(By.id("scoreInput"));
//        inputScore2.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
//        driver.findElement(By.id("scoreInput")).sendKeys(currentScore);
//        driver.findElement(By.id("updateButton")).click();
//        Thread.sleep(Constants.SLEEP_DURATION.getIntValue());
    }
}
