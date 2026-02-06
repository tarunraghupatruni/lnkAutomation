package com.prjct;

import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.io.FileInputStream;
import java.time.Duration;

public class ezy {

    WebDriver driver;
    WebDriverWait wait;

    String url = "https://stagingweb-corporate.ezyryd.com/auth";

    public static void main(String[] args) throws Exception {
        System.out.println("Execution started");

        ezy obj = new ezy();
        obj.setup();
        obj.login();
        obj.openAddEmployee();
        obj.addEmployeeFromExcel();

        System.out.println("Execution completed");
    }

    // ---------------- SETUP ----------------
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(url);
    }

    // ---------------- LOGIN ----------------
    public void login() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']"))).sendKeys("9154882399");

        driver.findElement(By.xpath("//input[@type='password']")).sendKeys("1234");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("nav")));
        System.out.println("Login successful");
    }

    // ---------------- NAVIGATION ----------------
    public void openAddEmployee() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav//button[3]"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Add Employee']"))).click();

        System.out.println("Opened Add Employee screen");
    }

    // ---------------- MAIN LOGIC ----------------
    public void addEmployeeFromExcel() throws Exception {

        // -------- Dropdown 1 (Role) --------
        selectFromMenu("//button[@aria-haspopup='menu']", "Employee");

        // -------- Dropdown 2 (Building) --------
        selectFromMenu("(//button[@aria-haspopup='menu'])[2]", "Building");

        // -------- Dropdown 3 (Transport) --------
        selectFromMenu("(//button[@aria-haspopup='menu'])[3]", "Transportation");

        // -------- Excel --------
        FileInputStream fis = new FileInputStream(
                "C:\\Users\\tarun\\Downloads\\Employee_Form_Data.xlsx");
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(0);

        int lastRow = sheet.getLastRowNum();

        for (int i = 1; i <= lastRow; i++) {

            Row row = sheet.getRow(i);

            type("//input[1]", row.getCell(0)); // Full Name
            type("//input[2]", row.getCell(1)); // Mobile
            type("//input[3]", row.getCell(2)); // Alt Mobile
            type("//input[5]", row.getCell(3)); // Emp ID
            type("//input[6]", row.getCell(4)); // Address
            type("//input[7]", row.getCell(5)); // Medical
            type("//div[3]//input[1]", row.getCell(6)); // Login Mobile
            type("//div[3]//input[2]", row.getCell(7)); // Passcode

            driver.findElement(By.xpath("//button[normalize-space()='Save']")).click();

            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//div[@role='dialog']")));

            System.out.println("Employee added : Row " + i);
            System.out.println("Completed the entering process");

        }

        workbook.close();
        fis.close();
        driver.quit();
    }

    // ---------------- HELPERS ----------------
    private void selectFromMenu(String buttonXpath, String value) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(buttonXpath))).click();

        By option = By.xpath("//div[@role='menuitem' and normalize-space()='" + value + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    private void type(String relativeXpath, Cell cell) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//main" + relativeXpath)));
        el.clear();
        el.sendKeys(cell.toString());
    }
}
