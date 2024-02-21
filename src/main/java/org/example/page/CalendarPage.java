package org.example.page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.UserData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class CalendarPage {

    private final Logger filelogger = LogManager.getLogger("fileLogger");
    private WebDriver driver;
    public CalendarPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    public void chooseAvailableDate(String city, UserData userData) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(15000L)); // wait for maximum of 20 seconds

        boolean dateFound = false;

        int maxArrowClicks = 3;
        int arrowClicks = 0;

        int backArrowClicks = 0;
        int maxBackArrowClicks = 3;

        while (!dateFound && arrowClicks < maxArrowClicks) {
            List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("calendar-ul-li-enable")));

            for (WebElement element : elements) {
                WebElement innerElement = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(element, By.cssSelector(".disable.enable")));
                String available = innerElement.getText();
                if (available.contains("доступны") && !available.contains("доступны:0")) {
                    innerElement.click();
                    dateFound = true;
                    break;
                }
            }

            if (!dateFound) {
                // <span class="nextBtn">&gt;</span>
                WebElement arrow = wait.until(ExpectedConditions.elementToBeClickable(By.className("nextBtn")));
                arrow.click();
                arrowClicks++;
            }
        }

        if (!dateFound) {
            while (!dateFound && backArrowClicks < maxBackArrowClicks) {
                List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("calendar-ul-li-enable")));

                for (WebElement element : elements) {
                    WebElement innerElement = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(element, By.cssSelector(".disable.enable")));
                    String available = innerElement.getText();
                    if (available.contains("доступны") && !available.contains("доступны:0")) {
                        innerElement.click();
                        dateFound = true;
                        break;
                    }
                }

                if (!dateFound) {
                    // <span class="prevBtn">&lt;</span>
                    WebElement arrow = wait.until(ExpectedConditions.elementToBeClickable(By.className("prevBtn")));
                    arrow.click();
                    backArrowClicks++;
                }
            }
        }

        if (dateFound) {
            filelogger.info("["+city+"] Даты для "+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" НАШЛИСЬ" );

            List<WebElement> timeSlots = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".calendar-timezone-disable.calendar-timezone-enable")));

            for(int i = 0 ; i < timeSlots.size();i++){
                System.out.println(timeSlots.get(i).getText());
            }

            if (!timeSlots.isEmpty()) {
                filelogger.info("["+city+"] Слоты со временем "+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" НАШЛИСЬ" );

                WebElement firstSlot = timeSlots.get(0);
                WebDriverWait waitToClick = new WebDriverWait(driver, Duration.ofMillis(10000L));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                waitToClick.until(ExpectedConditions.elementToBeClickable(firstSlot));
                firstSlot.click();
                filelogger.info("["+city+"] Слот со временем для "+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" был КЛИКНУТ" );


                WebDriverWait waitDialog = new WebDriverWait(driver, Duration.ofMillis(10000L));
                filelogger.info("["+city+"] Диалог после клика слота для "+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" ПОЯВИЛСЯ" );

                WebElement yesButton = waitDialog.until(ExpectedConditions.visibilityOfElementLocated(By.className("aui_state_highlight")));
                filelogger.info("["+city+"] Кнопка диалога 'да'"+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" ПОЯВИЛАСЬ" );

                yesButton.click();
                filelogger.info("["+city+"] Кнопка диалога 'да'"+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" была НАЖАТА" );


                WebElement yesButtonConfirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".aui_buttons .aui_state_highlight")));
                filelogger.info("["+city+"] Еще одна кнопка да'"+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" НАШЛАСЬ" );

                yesButtonConfirmation.click();
                filelogger.info("["+city+"] Еще одна кнопка да'"+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" была НАЖАТА" );

                WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'Сохранить на компьютер')]")));
                filelogger.info("["+city+"] Кнопка сохранить'"+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" была НАЙДЕНА" );

                saveButton.click();
                filelogger.info("["+city+"] Кнопка сохранить'"+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" была КЛИКНУТА" );
                try {
                    Thread.sleep(15000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                // Если нет доступных временных слотов, обработайте эту ситуацию
                filelogger.warn("["+city+"] Дата найдена, но слоты со временем нет для: '"+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber()  );

            }
            // proceed with the available date
        } else {
            filelogger.warn("["+city+"] Даты для "+ userData.getNameSurname() + " " + userData.getPhone() + " " + userData.getEmail() + " " + userData.getAppointmentNumber() +" НЕ НАШЛИСЬ" );
            // handle the situation when no available date was found after maxArrowClicks attempts
        }

        WebElement yesButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'Да')]")));

        yesButton.click();

    }
}