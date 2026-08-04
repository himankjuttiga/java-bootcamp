package com.northstar.crm.ui;

import com.northstar.crm.ui.pages.CustomerFormPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerUiIT {

    @LocalServerPort
    int port;

    WebDriver driver;

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox",
                "--disable-dev-shm-usage", "--window-size=1280,900");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void quit() {
        if (driver != null) driver.quit();
    }

    @Test
    void createCustomerViaUi() {
        CustomerFormPage page = new CustomerFormPage(driver).open("http://localhost:" + port);
        page.fill("CUS-2001", "Test User", "test.user@example.com", "PROSPECT");
        page.submit();

        String result = page.resultText();
        assertTrue(result.contains("CUS-2001"),
                "result should echo the created customer id, was: " + result);
    }
}
