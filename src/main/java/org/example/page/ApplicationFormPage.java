package org.example.page;

import org.example.model.UserData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ApplicationFormPage {
    private final WebDriver driver;
    public ApplicationFormPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void fillForm(UserData userdata) throws InterruptedException {
        Thread.sleep(300L);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(10000L));

        WebElement fioElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("linkname")));
        fioElement.sendKeys(userdata.getNameSurname());

        WebElement phoneElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("linkphone")));
        phoneElement.sendKeys(userdata.getPhone());

        WebElement emailElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mail")));
        emailElement.sendKeys(userdata.getEmail());

        WebElement visaApplicationNumberElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("applyid1")));
        visaApplicationNumberElement.sendKeys(userdata.getAppointmentNumber());

        WebElement continueBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#GoToDefUrl > button")));
        continueBtn.click();
    }
}
