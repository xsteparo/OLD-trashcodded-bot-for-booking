package org.example.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class InstructionPage {
    private final WebDriver driver;
    public InstructionPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);

    }
    public ApplicationFormPage goToApplicationFormPage(){
        WebDriverWait wait= new WebDriverWait(driver, Duration.ofMillis(20000L));
        WebElement makeAnAppointmentBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#first-info > div.item > div > button")));
        makeAnAppointmentBtn.click();
        return new ApplicationFormPage(driver);
    }
}
