package com.northstar.crm.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/** Page Object — locate via data-testid only. */
public class CustomerFormPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CustomerFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CustomerFormPage open(String baseUrl) {
        driver.get(baseUrl + "/customers.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid=customer-id]")));
        return this;
    }

    public void fill(String id, String name, String email, String status) {
        var idEl = driver.findElement(By.cssSelector("[data-testid=customer-id]"));
        idEl.clear();
        idEl.sendKeys(id);
        var nameEl = driver.findElement(By.cssSelector("[data-testid=full-name]"));
        nameEl.clear();
        nameEl.sendKeys(name);
        var emailEl = driver.findElement(By.cssSelector("[data-testid=email]"));
        emailEl.clear();
        emailEl.sendKeys(email);
        var statusEl = driver.findElement(By.cssSelector("[data-testid=status]"));
        statusEl.clear();
        statusEl.sendKeys(status);
    }

    public void submit() {
        driver.findElement(By.cssSelector("[data-testid=submit-customer]")).click();
    }

    public String resultText() {
        wait.until(d -> !d.findElement(
                By.cssSelector("[data-testid=create-result]")).getText().isBlank());
        return driver.findElement(By.cssSelector("[data-testid=create-result]")).getText();
    }
}
