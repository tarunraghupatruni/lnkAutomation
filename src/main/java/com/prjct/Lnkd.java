package com.prjct;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;

public class Lnkd {

    public static void main(String[] args) throws Exception {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.manage().window().maximize();

        System.out.println("=======================\r\n" + //
                        "STEP 1: Auto Login\r\n" + //
                        "========================");
        /* =======================
           STEP 1: Auto Login
        ======================== */
        driver.get("https://www.linkedin.com/login");
        
        driver.findElement(By.id("username")).sendKeys("taruntej.2024@gmail.com");
        driver.findElement(By.id("password")).sendKeys("Sudhasury@123");
        driver.findElement(By.xpath("//*[@id=\"organic-div\"]/form/div[4]/button")).click();

        System.out.println("➡ Login manually within 30 seconds...");
        Thread.sleep(30000);

        if (driver.getCurrentUrl().contains("login")) {
            throw new RuntimeException("Login not completed.");
        }

        System.out.println(" =======================\r\n" + //
                        "STEP 2: Load Resume\r\n" + //
                        "========================");
        /* =======================
           STEP 2: Load Resume
        ======================== */
        String resumeText = readResume("resume.txt");

        System.out.println(" =======================\r\n" + //
                        "STEP 3: Define Roles\r\n" + //
                        "========================");
        /* =======================
           STEP 3: Define Roles
        ======================== */
        String[] roles = {
                "Manual Tester",
                "QA Tester",
                "Software Tester",
                "Test Engineer",
                "QA Engineer",
                "Quality Analyst"
        };

        System.out.println("=======================\r\n" + //
                        "STEP 4: Excel Setup\r\n" + //
                        "========================");
        /* =======================
           STEP 4: Excel Setup
        ======================== */
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("LinkedIn Jobs");

        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Role");
        header.createCell(1).setCellValue("Company Name");
        header.createCell(2).setCellValue("Experience Required");
        header.createCell(3).setCellValue("Job Link");
        header.createCell(4).setCellValue("Resume Match %");

        int rowNum = 1;

        System.out.println("=======================\r\n" + //
                        "STEP 5: Loop Roles\r\n" + //
                        "========================");
        /* =======================
           STEP 5: Loop Roles
        ======================== */
        for (String role : roles) {

            String encodedRole = role.replace(" ", "%20");

            String searchUrl =
                    "https://www.linkedin.com/jobs/search/?" +
                    "keywords=" + encodedRole +
                    "&location=Hyderabad%2C%20Telangana%2C%20India" +
                    "&f_E=2" +
                    "&f_TPR=r604800";

            System.out.println("\n🔍 Searching for role: " + role);
            driver.get(searchUrl);

            wait.until(d ->
                    js.executeScript("return document.readyState")
                            .equals("complete"));

            Thread.sleep(3000);

            /* Scroll left job list */
            WebElement jobList = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("div.scaffold-layout__list"))
            );

            for (int i = 0; i < 4; i++) {
                js.executeScript(
                        "arguments[0].scrollTop = arguments[0].scrollHeight",
                        jobList);
                Thread.sleep(2000);
            }

            List<WebElement> jobCards = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("li.scaffold-layout__list-item"))
            );

            System.out.println("Jobs found for " + role + ": " + jobCards.size());

            System.out.println(" =======================\r\n" + //
                                "STEP 6: Extract Jobs\r\n" + //
                                "======================== ");
            /* =======================
               STEP 6: Extract Jobs
            ======================== */
            for (int i = 0; i < jobCards.size(); i++) {

                try {
                    jobCards = driver.findElements(
                            By.cssSelector("li.scaffold-layout__list-item"));

                    WebElement job = jobCards.get(i);

                    js.executeScript(
                            "arguments[0].scrollIntoView(true);", job);
                    Thread.sleep(1000);

                    /* Job Link */
                    WebElement linkElement =
                            job.findElement(By.cssSelector(
                                    "a[href*='/jobs/view']"));

                    String rawLink = linkElement.getAttribute("href");
                    String jobLink = rawLink.startsWith("http")
                            ? rawLink
                            : "https://www.linkedin.com" + rawLink;

                    job.click();
                    Thread.sleep(2500);

                    /* Company Name */
                    WebElement companyElement = wait.until(
                            ExpectedConditions.presenceOfElementLocated(
                                    By.cssSelector(
                                            "a[href*='/company/'], " +
                                            "span.artdeco-entity-lockup__subtitle"))
                    );

                    String companyName = companyElement.getText().trim();
                    if (companyName.isEmpty()) {
                        companyName = companyElement.getAttribute("aria-label");
                    }
                    if (companyName == null || companyName.isEmpty()) {
                        companyName = "Not Available";
                    }

                    /* Job Description */
                    WebElement jdElement = wait.until(
                            ExpectedConditions.presenceOfElementLocated(
                                    By.cssSelector("div.jobs-description__content"))
                    );

                    String jobDescription = jdElement.getText();

                    double matchPercent =
                            TextSimilarityUtil.calculateSimilarity(
                                    resumeText, jobDescription);

                    /* Write to Excel */
                    XSSFRow row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(role);
                    row.createCell(1).setCellValue(companyName);
                    row.createCell(2).setCellValue("Fresher - 1 Year");
                    row.createCell(3).setCellValue(jobLink);
                    row.createCell(4).setCellValue(
                            String.format("%.2f", matchPercent));

                    /* Highlight ≥40% */
                    if (matchPercent >= 40) {
                        XSSFCellStyle greenStyle =
                                workbook.createCellStyle();
                        greenStyle.setFillForegroundColor(
                                IndexedColors.LIGHT_GREEN.getIndex());
                        greenStyle.setFillPattern(
                                FillPatternType.SOLID_FOREGROUND);

                        for (int c = 0; c <= 4; c++) {
                            row.getCell(c).setCellStyle(greenStyle);
                        }
                    }

                    System.out.println(
                            "Captured: " + companyName +
                            " | Match: " +
                            String.format("%.2f", matchPercent));

                } catch (Exception e) {
                    System.out.println("Skipped one job");
                }
            }
        }

        System.out.println("=======================\r\n" + //
                        "STEP 7: Save Excel\r\n" + //
                        "========================");
        /* =======================
           STEP 7: Save Excel
        ======================== */
        FileOutputStream fos =
                new FileOutputStream(
                        "LinkedIn_Multiple_Testing(2)_Roles.xlsx");
        workbook.write(fos);
        workbook.close();
        fos.close();

        driver.quit();
        System.out.println("\n✅ Excel file created successfully.");
    }

    /* =======================
       Resume Reader
    ======================== */
    private static String readResume(String path) throws Exception {
        System.out.println("=======================\r\n" + //
                        "Resume Reader\r\n" + //
                        "========================");
        return new String(
                java.nio.file.Files.readAllBytes(
                        java.nio.file.Paths.get(path)));
    }
}
