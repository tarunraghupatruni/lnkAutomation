package com.prjct;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public class App {
    String url = "https://node-admin.lifelet.in/auth/login";
    WebDriver driver = new ChromeDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    Actions actions = new Actions(driver);

    public static void main(String[] args) throws Exception {
        System.out.println("Execution started");
        App obj = new App(); 
        obj.opn();
        obj.nav();
        obj.crtcntry();
        
    }
    public void opn(){
        driver.navigate().to(url);
        driver.findElement(By.name("email")).sendKeys("ana@admin.com");
        driver.findElement(By.name("password")).sendKeys("Analogue@123");
        driver.findElement(By.className("login-button")).click();

        System.out.println("Login successfull");
    }

    public void nav(){
        System.out.println("nav");
        WebElement log = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/aside[1]/div[1]/a/img")));

        driver.findElement(By.xpath("/html/body/div[2]/aside[1]/div[2]/nav/ul/li[6]/a/p")).click();
        System.out.println("countries");

    }

    public void crtcntry() throws Exception {

    FileInputStream fis = new FileInputStream(
            "C:\\Users\\tarun\\Downloads\\Country_Codes_Complete.xlsx"
    );
    

    Workbook workbook = new XSSFWorkbook(fis);
    Sheet sheet = workbook.getSheetAt(0);

    int rowCount = sheet.getLastRowNum();

    for (int i = 1; i <= rowCount; i++) {

        Row row = sheet.getRow(i);
        if (row == null) {
            continue;
        }

        String countryName = row.getCell(0).getStringCellValue().trim();
        String countryCode = row.getCell(1).getStringCellValue().trim();
        String mobileCode  = row.getCell(2).getStringCellValue().trim();

        try {
            // Open Add Country modal
            WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='example1_wrapper']/div/div[1]/ol/button")
            ));
            addBtn.click();

            // Wait for modal to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add_Modal")));

            // Fill fields
            WebElement nameField = driver.findElement(By.id("country_Name"));
            nameField.clear();
            nameField.sendKeys(countryName);

            WebElement codeField = driver.findElement(By.id("country_Code"));
            codeField.clear();
            codeField.sendKeys(countryCode);

            WebElement mobileField = driver.findElement(By.id("mobile_Code"));
            mobileField.clear();
            mobileField.sendKeys(mobileCode);

            // Submit form
            driver.findElement(By.id("submitBtn")).click();

            // Success → modal closes
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addModal")));

            System.out.println("Added: " + countryName);

        } catch (TimeoutException e) {

            // Validation / duplicate / format warning
            System.out.println("Skipped (validation or duplicate): " + countryName);

            // Close modal safely
            try {
                driver.findElement(By.xpath("//button[contains(text(),'Close')]")).click();
            } catch (Exception ignored) {}

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addModal")));
        }
    }

    workbook.close();
    fis.close();
    }
    
}

//         driver.findElement(By.id("username")).sendKeys("taruntej.2024@gmail.com");
//         driver.findElement(By.id("password")).sendKeys("Sudhasury@123");
//         driver.findElement(By.xpath("//*[@id=\"organic-div\"]/form/div[4]/button")).click();



//  driver.findElement(By.id("login_Layer")).click();
//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Enter your active Email ID / Username']")));
//         driver.findElement(By.xpath("//input[@placeholder='Enter your active Email ID / Username']")).sendKeys("taruntej.2024@gmail.com");
//         driver.findElement(By.xpath("//*[@id=\"root\"]/div[4]/div[2]/div/div/div[2]/div/form/div[3]/input")).sendKeys("Sudhasury@123");
//         driver.findElement(By.xpath("//*[@id=\"root\"]/div[4]/div[2]/div/div/div[2]/div/form/div[6]/button")).click();
//         System.out.println("Login manually within 20 seconds...");
//         Thread.sleep(20000);