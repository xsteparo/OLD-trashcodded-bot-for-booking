package org.example.webdriver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.managers.ProxyDistributor;
import org.example.model.ChinaProxy;
import org.example.model.ThreadType;
import org.example.util.CsvReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.example.util.ConfigLoader.*;

public class ChromeDriverUtil {
    private static final Logger consoleLogger = LogManager.getLogger(CsvReader.class);

    public static WebDriverWrapper toWrapDriver(ProxyDistributor proxyDistributor, ThreadType threadType) {
        WebDriver driver = null;
        ChinaProxy proxy = null;

        while (driver == null) {
            if (threadType == ThreadType.CHECKER) {
                proxy = proxyDistributor.getValidProxyForChecker();
            } else if (threadType == ThreadType.BOOKING) {
                proxy = proxyDistributor.getValidProxyForBooker();
            }
            if (proxy == null) {
                return null;
            }
            File plugin = getProxyFileWithAuthorization(proxy);
            driver = setUpDriver(plugin, threadType);
        }

        WebDriverWrapper webDriverWrapper = new WebDriverWrapper();
        webDriverWrapper.setWebDriver(driver);
        webDriverWrapper.setProxy(proxy);
        return webDriverWrapper;
    }

    public static ChromeDriver setUpDriver(File plugin, ThreadType threadType) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addExtensions(plugin);
        chromeOptions.addArguments("--remote-allow-origins=*");

//        if (threadType == ThreadType.CHECKER) {
//            chromeOptions.addArguments("--headless=new");
//        }

        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        deleteExtension(plugin.getAbsolutePath());
        return chromeDriver;
    }

    private static File getProxyFileWithAuthorization(ChinaProxy chinaProxy) {
        String manifestJson = readFile(getPathManifest());

        String backgroundJsTemplate = readFile(getPathBackgroundJs());
        String backgroundJs = backgroundJsTemplate.replace("{HOST}", chinaProxy.getIpAddress())
                .replace("{PORT}", String.valueOf(chinaProxy.getPort()))
                .replace("{USERNAME}", chinaProxy.getUsername())
                .replace("{PASSWORD}", chinaProxy.getPassword());

        long currentTimeInMillis = new Date().getTime();

        String threadId = String.valueOf(Thread.currentThread().getId());
        String pluginFile = String.format("%s/%s_%s.zip", getPathProxyExtensions(), currentTimeInMillis, threadId);

        try (var fos = new FileOutputStream(pluginFile);
             var zipOut = new ZipOutputStream(fos)) {
            var manifestEntry = new ZipEntry(new File(getPathManifest()).getName());
            zipOut.putNextEntry(manifestEntry);
            zipOut.write(manifestJson.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();

            var jsEntry = new ZipEntry(new File(getPathBackgroundJs()).getName());
            zipOut.putNextEntry(jsEntry);
            zipOut.write(backgroundJs.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new File(pluginFile);
    }

    private static String readFile(String path) {
        try (InputStream is = new FileInputStream(path);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            consoleLogger.info("Ошибка при чтении файла: {}", path);
            throw new RuntimeException();
        }
    }

    private static void deleteExtension(String fileName) {
        try {
            File file = new File(fileName);
            if (file.delete()) {
                consoleLogger.info("Прокси файл успешно удален");
            } else {
                consoleLogger.info("Прокси файл не существует: {}", fileName);
            }
        } catch (Exception e) {
            consoleLogger.info("Ошибка при удалении файла: {}", fileName);
        }
    }
}
