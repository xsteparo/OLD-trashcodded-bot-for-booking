package org.example.thread;

import org.example.model.ChinaProxy;
import org.example.model.ThreadType;
import org.example.model.UserData;
import org.example.page.CalendarPage;
import org.example.webdriver.ChromeDriverUtil;
import org.example.webdriver.WebDriverWrapper;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookingThread extends BrowserSessionThread {
    private final AtomicBoolean isUserRegistered = new AtomicBoolean();
    private final AtomicBoolean areProxiesInvalid = new AtomicBoolean();

    public BookingThread(String city, UserData userData, List<ChinaProxy> proxies, String threadName) {
        super(city, userData, proxies, threadName);
    }
    @Override
    public WebDriverWrapper initDriverWrapperWithCurrentProxy() {
        WebDriverWrapper wrapper = ChromeDriverUtil.toWrapDriver(proxyDistributor, ThreadType.BOOKING);
        if (wrapper == null) {
            areProxiesInvalid.set(true);
            return null;
        }
        return wrapper;
    }

    @Override
    protected void performSpecificTask(WebDriver driver) {
        try {
            CalendarPage calendarPage = new CalendarPage(driver);
            calendarPage.chooseAvailableDate(city,userData);
            isUserRegistered.set(true);
            fileLogger.info("Запись на  '{} {} {} {}' была УСПЕШНОЙ ({})",
                    userData.getLogInfo(), threadName);
        } catch (Exception e) {
            consoleLogger.error("'[{}]'Запись на  '{} {} {} {}' НЕ была УСПЕШНОЙ ({})",
                    city,
                    userData.getLogInfo(),
                    threadName);
        }
    }

    //TODO: пока юзер не зарегается, пока круг с проксами не пройдет 3 раза.
    @Override
    public void run() {
        while (!isUserRegistered.get() && !areProxiesInvalid.get()) {
            runBrowserSession();
        }
        this.interrupt();
        consoleLogger.info("[{}] БУКЕР ДЛЯ {} {} завершился, зарегистрирован?: {}",
                userData.getNameSurname(), userData.getAppointmentNumber(), isUserRegistered.get());
    }
}
