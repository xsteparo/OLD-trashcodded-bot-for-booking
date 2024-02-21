package org.example.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.managers.ProxyDistributor;
import org.example.model.ChinaProxy;
import org.example.model.UserData;
import org.example.page.ApplicationFormPage;
import org.example.page.InstructionPage;
import org.example.page.MainPage;
import org.example.captcha.CaptchaSolver;
import org.example.webdriver.WebDriverWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;


import java.util.List;
import java.util.concurrent.*;

import static org.example.util.ConfigLoader.getPathWebUrl;


public abstract class BrowserSessionThread extends Thread {
    protected static final String TARGET_URL = getPathWebUrl();

    protected Logger consoleLogger;
    protected Logger fileLogger;
    protected String threadName;
    protected String city;
    protected volatile boolean shouldStop = false;
    protected UserData userData;
    protected List<ChinaProxy> chinaProxies;
    protected WebDriver driver;

    protected ProxyDistributor proxyDistributor;



    public BrowserSessionThread(String city, UserData userData, List<ChinaProxy> chinaProxies, String threadName) {
        this.city = city;
        this.userData = userData;
        this.chinaProxies = chinaProxies;
        this.threadName = threadName;
        proxyDistributor = new ProxyDistributor(chinaProxies);
        initLoggers();
    }

    public abstract WebDriverWrapper initDriverWrapperWithCurrentProxy();


    public final void runBrowserSession() {
        WebDriverWrapper wrapper = initDriverWrapperWithCurrentProxy();
        if (wrapper == null) {
            consoleLogger.warn("[{}] WebDriverWrapper инициализация вернула null {}", city, threadName);
            return;
        }

        driver = wrapper.getWebDriver();
        consoleLogger.info("[{}] Используется прокси {} {}", city, wrapper.getProxy().getIpAddress(), threadName);
        consoleLogger.info("[{}] Попытка подключения к главной странице {}", city, threadName);



        try {
            driver.manage().timeouts().pageLoadTimeout(10L, TimeUnit.SECONDS);
            driver.get(TARGET_URL);
            if (shouldStop) return;

            MainPage mainPage = new MainPage(driver);
            if (shouldStop) return;

            InstructionPage instructionPage = mainPage.chooseLocation(city);
            consoleLogger.info("[{}] Выбрал город {}", city, threadName);
            if (shouldStop) return;

            ApplicationFormPage applicationFormPage = instructionPage.goToApplicationFormPage();
            consoleLogger.info("[{}] Перешел на страницу с формой {}", city, threadName);
            if (shouldStop) return;

            applicationFormPage.fillForm(userData);
            consoleLogger.info("[{}] Заполнил данные {}", city, threadName);
            if (shouldStop) return;

            CaptchaSolver captchaSolver = new CaptchaSolver(driver);
            consoleLogger.info("[{}] Начинает решать капчу {}", city, threadName);
            captchaSolver.solveCaptcha();
            consoleLogger.info("[{}] Решил капчу {}", city, threadName);
            if (shouldStop) return;

//            if(isThereDialogWithErrorMessage(driver))return;
            if (shouldStop) return;

            performSpecificTask(driver);
        } catch (InterruptedException e) {
            consoleLogger.warn("[{}] Поток был прерван {}", city, threadName);
        } catch (WebDriverException e) {
            consoleLogger.error("[{}] Сайт не загрузился за 10 сек, либо прокси забанено на время {}", city, threadName);
        } catch (Exception e) {
            consoleLogger.error("[{}] Не нашел элементы {}", city, threadName);
        } finally {
            try {
                Thread.sleep(2000L);
                driver.quit();
                consoleLogger.info("[{}] Закрылась сессия WebDriver {}", city, threadName);

            }catch (Exception e){
                consoleLogger.info("[{}] ТА САМАЯ ЕБАНУТАЯ ХУЙНЯ {}", city, threadName );
                e.printStackTrace();
            }
        }
    }

    private boolean isThereDialogWithErrorMessage(WebDriver driver) {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<WebElement> dialogCaptcha = driver.findElements(By.cssSelector(".aui_content"));
        if (!dialogCaptcha.isEmpty()) {
            String errorMessage = dialogCaptcha.get(0).getText();
            consoleLogger.error("[{}] Сообщение: {}", city, errorMessage);
            return true;
        }
        return false;
    }

    private boolean connectToURLWithTimeout(WebDriver driver) {
        // Запуск в отдельном потоке с таймаутом

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> driver.get(TARGET_URL));
        try {
            future.get(20, TimeUnit.SECONDS);
            return true;
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
//            driver.quit();
            consoleLogger.warn("[" + city + "] Произошла ошибка при загрузке страницы " + e);
        } finally {

            executor.shutdownNow();
        }
        return false;
    }

    // Всегда епт переопределяем специфик таск для двух разных скриптов
    protected abstract void performSpecificTask(WebDriver driver) throws Exception;



//    protected boolean checkConnection(WebDriver driver) {
//        return connectToURLWithTimeout(driver);
//    }

    // Метод для установки флага
    public void stopThread() {
        shouldStop = true;
    }


    private void initLoggers() {
        this.consoleLogger = LogManager.getLogger("consoleLogger");
        this.fileLogger = LogManager.getLogger("fileLogger");
    }
}
