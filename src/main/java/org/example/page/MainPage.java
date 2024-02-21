package org.example.page;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPage {
    private final WebDriver driver;
    private static final Logger consoleLogger = LogManager.getLogger("consoleLogger");
    public MainPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);

    }
    public InstructionPage chooseLocation(String city)  {

        WebDriverWait wait= new WebDriverWait(driver, Duration.ofMillis(10000L));
        WebElement continent = driver.findElement(By.id("EU"));
        continent.click();

        WebElement continentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("continentBox")));

        WebElement cityElement = continentBox.findElement(By.xpath("//li[contains(text(), '" + city.toUpperCase() + "')]"));

        cityElement.click();

        consoleLogger.info("[{}] Город был выбран", city);

        return goToInstructionPage();
    }

    public InstructionPage goToInstructionPage(){
        return new InstructionPage(driver);
    }
}
