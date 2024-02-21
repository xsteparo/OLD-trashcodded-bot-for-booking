package org.example.captcha;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CaptchaSolver {
    private final Logger consoleLogger = LogManager.getLogger("consoleLogger");
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CaptchaSolver(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofMillis(10000L));
    }

    public void solveCaptcha(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tcaptcha_iframe_dy")));
        driver.switchTo().frame("tcaptcha_iframe_dy");

        consoleLogger.info("Загрузка изображения капчи");
        CaptchaImageDownloader captchaImageDownloader = new CaptchaImageDownloader(driver);
        captchaImageDownloader.downloadCaptchaImage();

        PuzzleDetector puzzleDetector = new PuzzleDetector();
        puzzleDetector.detectPuzzleOnImage(captchaImageDownloader.getFileName());

        holdAndMoveSlider(puzzleDetector);

        captchaImageDownloader.deleteDownloadedCaptchaImage(captchaImageDownloader.getFileName());

        implicitWait(4000L);

        driver.switchTo().defaultContent();
    }

    private void holdAndMoveSlider(PuzzleDetector puzzleDetector){
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='tc-fg-item tc-slider-normal']")));

        Actions actions = new Actions(driver);
        actions.clickAndHold(slider)
                .moveByOffset((int) puzzleDetector.getX() - 54, 0)
                .release()
                .perform();

        implicitWait(5000L);

        driver.switchTo().defaultContent();
    }

    private void implicitWait(Long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            consoleLogger.error("Ошибка при ожидании: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

