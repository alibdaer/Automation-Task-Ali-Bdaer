package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class TestBase {

    protected WebDriver driver;
    public SoftAssert softAssert = null;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {

        softAssert = new SoftAssert();

        if (!browser.equalsIgnoreCase("chrome") && !browser.equalsIgnoreCase("chrome_headless")) {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-notifications");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        if (browser.equalsIgnoreCase("chrome_headless")) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }

        String chromeBinary = System.getProperty("chrome.binary");
        if (chromeBinary != null && !chromeBinary.isBlank()) {
            options.setBinary(chromeBinary);
        }

        if (System.getProperty("webdriver.chrome.driver", "").isBlank()) {
            WebDriverManager.chromedriver().setup();
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.get("https://www.saucedemo.com/");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (driver != null) {
                if (!result.isSuccess()) {
                    saveFailureScreenshot(result.getMethod().getMethodName());
                }
                driver.quit();
            }
        } catch (WebDriverException exception) {
            if (result.isSuccess()) {
                throw exception;
            }
            Reporter.log("Browser cleanup issue: " + exception.getMessage(), true);
        } finally {
            driver = null;
            softAssert = null;
        }
    }

    private void saveFailureScreenshot(String testName) {
        try {
            Path directory = Path.of("screenshots", "screenshots");
            Files.createDirectories(directory);
            Path screenshot = directory.resolve(testName + ".png");
            byte[] image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(screenshot, image);
            Reporter.log("Failure screenshot: " + screenshot.toAbsolutePath(), true);
        } catch (IOException | WebDriverException exception) {
            Reporter.log("Could not save screenshot: " + exception.getMessage(), true);
        }
    }
}
