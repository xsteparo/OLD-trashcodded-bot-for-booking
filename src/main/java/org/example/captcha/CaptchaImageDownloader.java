package org.example.captcha;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Date;

import static org.example.util.ConfigLoader.getPathDownloadedCaptcha;

public class CaptchaImageDownloader {
    private static final Logger consoleLogger = LogManager.getLogger("consoleLogger");

    private final WebDriver driver;
    @Getter
    private String fileName;

    public CaptchaImageDownloader(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void downloadCaptchaImage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(10000));
        WebElement captchaElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("slideBg")));

        String style = captchaElement.getCssValue("background-image");
        String imageUrl = style.substring(5, style.length() - 2);

        long currentTimeInMillis = new Date().getTime();
        fileName = "%s.png".formatted(currentTimeInMillis);

        String targetPath = "%s/%s".formatted(getPathDownloadedCaptcha(), fileName);

        File directory = new File(getPathDownloadedCaptcha());

        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (InputStream in = new URL(imageUrl).openStream();
             FileOutputStream out = new FileOutputStream(targetPath)) {

            byte[] buffer = new byte[4096];
            int length;

            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

        } catch (IOException e) {
            consoleLogger.error("Ошибка при загрузке изображения капчи.", e);
        }
    }

    public void deleteDownloadedCaptchaImage(String fileName){
        try {
            File file = new File(getPathDownloadedCaptcha() + "/"+ fileName);
            if (file.exists() && !file.delete()) {
                consoleLogger.error("Не удалось удалить файл {}", file);
            }
        } catch (Exception e) {
            consoleLogger.error("Ошибка при удалении изображения капчи.", e);
        }
    }
}

