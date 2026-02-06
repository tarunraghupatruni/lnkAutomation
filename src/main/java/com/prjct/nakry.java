package com.prjct;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;

public class nakry {

    public static void main(String[] args) throws Exception {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        driver.manage().window().maximize();

        driver.get("https://www.naukri.com/");

        System.out.println("---------- LOGIN ----------");
        /* ---------- LOGIN ---------- */
        driver.findElement(By.id("login_Layer")).click();

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[placeholder*='Email ID']")
                )
        );
        email.sendKeys("taruntej.2024@gmail.com");

        driver.findElement(
                By.cssSelector("input[type='password']")
        ).sendKeys("Sudhasury@123");

        driver.findElement(
                By.xpath("//button[contains(text(),'Login')]")
        ).click();

        // Let Naukri settle (OTP / CAPTCHA / redirects)
        // Thread.sleep(15000);

        /* ---------- STABILIZE PAGE ---------- */
        driver.navigate().refresh();
        Thread.sleep(5000);

        // Close login drawer if still visible
        try {
            driver.findElement(By.cssSelector(".naukri-drawer .close")).click();
            Thread.sleep(2000);
        } catch (Exception ignored) {}

        System.out.println("---------- SEARCH JOB ----------");
        /* ---------- SEARCH JOB ---------- */
      WebElement searchBar = wait.until(
        ExpectedConditions.elementToBeClickable(
                By.id("ni-gnb-searchbar")
        )
        );
        searchBar.click();


        WebElement skillsInput = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"ni-gnb-searchbar\"]/div/div[2]/div/div/div/input")
        )
        );

        skillsInput.clear();
        skillsInput.sendKeys("Manual Tester Fresher");
        // driver.findElement(By.xpath("//*[@id=\"ni-gnb-searchbar\"]/div/div[2]/div/div/div/input")).sendKeys("Manual Tester Fresher");
        driver.findElement(By.xpath("//*[@id=\"ni-gnb-searchbar\"]/div/div[4]/div/div")).click();
        driver.findElement(By.xpath("//*[@id=\"sa-dd-scrollexperienceDD\"]/div[1]/ul/li[2]")).click();
        driver.findElement(
                By.xpath("//input[contains(@placeholder,'location')]")
        ).sendKeys("Hyderabad");

        driver.findElement(
                By.xpath("//button[contains(text(),'Search')]")
        ).click();

        System.out.println("---------- APPLY FILTERS ----------");
        /* ---------- APPLY FILTERS ---------- */
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='0-1 Yrs']"))
        ).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'Last 7')]"))
        ).click();

        System.out.println("---------- FETCH JOBS ----------");
        /* ---------- FETCH JOBS ---------- */
        List<WebElement> jobs = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(".jobTuple"))
        );

        System.out.println("---------- EXCEL SETUP ----------");
        /* ---------- EXCEL SETUP ---------- */
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Fresher Jobs");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Company Name");
        header.createCell(1).setCellValue("Experience Required");
        header.createCell(2).setCellValue("Job Link");

        int rowNum = 1;

        System.out.println("---------- EXTRACT DATA ----------");
        /* ---------- EXTRACT DATA ---------- */
        for (WebElement job : jobs) {
            try {
                String company = job.findElement(
                        By.cssSelector(".companyInfo.subheading a")
                ).getText();

                String experience = job.findElement(
                        By.cssSelector(".expwdth")
                ).getText();

                String link = job.findElement(
                        By.cssSelector("h1 a")
                ).getAttribute("href");

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(company);
                row.createCell(1).setCellValue(experience);
                row.createCell(2).setCellValue(link);

            } catch (Exception ignored) {}
        }

        System.out.println("---------- SAVE EXCEL ----------");
        /* ---------- SAVE EXCEL ---------- */
        FileOutputStream fos =
        new FileOutputStream("Naukri_Fresher_Manual_Tester_Jobs.xlsx");
        workbook.write(fos);
        workbook.close();
        fos.close();

        System.out.println("Excel created successfully.");
        driver.quit();
    }
}
