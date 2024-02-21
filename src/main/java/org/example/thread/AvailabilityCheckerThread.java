package org.example.thread;

import org.example.Main;
import org.example.model.ChinaProxy;
import org.example.model.ThreadType;
import org.example.model.UserData;
import org.example.webdriver.ChromeDriverUtil;
import org.example.webdriver.WebDriverWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AvailabilityCheckerThread extends BrowserSessionThread {

    private final List<UserData> bookingData;

    private final List<ChinaProxy> bookingProxies;


    protected AtomicBoolean isCalendarOpened = new AtomicBoolean();


    public AvailabilityCheckerThread(String city, UserData userData,
                                     List<ChinaProxy> proxy, String threadName,
                                     List<UserData> bookingData, List<ChinaProxy> bookingProxies) {
        super(city, userData, proxy, threadName);
        this.bookingData = bookingData;
        this.bookingProxies = bookingProxies;
    }

    @Override
    public void run() {
        fileLogger.info("KEKAKAKAKAKAKAKAKAKAKAK");
        while (!isCalendarOpened.get() && !shouldStop) {
            runBrowserSession();
            if (Main.shouldClearConsole()) {
                Main.clearConsole();
                Main.updateLastClearTime();
            }
        }


        if (shouldStop) {
            this.interrupt();
            return;
        }
        for (UserData ud : bookingData) {
            BookingThread bookingThread = new BookingThread(city, ud, bookingProxies, "BOOKER");
            bookingThread.start();
        }
    }


    @Override
    public WebDriverWrapper initDriverWrapperWithCurrentProxy() {
        return ChromeDriverUtil.toWrapDriver(proxyDistributor, ThreadType.CHECKER);
    }

    @Override
    protected void performSpecificTask(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(10000L));

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("calendar-ul-li-enable")));
        consoleLogger.info("[{}] Зашел на календарь ({})", city, threadName);
        consoleLogger.warn("[{}] ------ЗАПИСЬ ОТКРЫТА!------ ({})", threadName);
        fileLogger.warn("[{}] ------ЗАПИСЬ ОТКРЫТА!------ ({})", city, threadName);

        isCalendarOpened.set(true);

//            driver.quit();

    }
}
