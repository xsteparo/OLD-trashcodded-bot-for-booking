package org.example.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger consoleLogger = LogManager.getLogger(ConfigLoader.class);
    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("constants.properties")) {
            CONFIG.load(input);
        } catch (IOException ex) {
            consoleLogger.error("Ошибка загрузки констант");
        }
    }

    public static String getPathWebUrl() {
        return CONFIG.getProperty("web.url");
    }

    public static String getPathResources() {
        return CONFIG.getProperty("resources.path");
    }

    public static String getPathDownloadedCaptcha() {
        return CONFIG.getProperty("downloaded.captcha.path");
    }

    public static String getPathProxyExtensions() {
        return CONFIG.getProperty("proxy.extension.path");
    }

    public static String getPathManifest() {
        return CONFIG.getProperty("manifest.path");
    }

    public static String getPathBackgroundJs() {
        return CONFIG.getProperty("backgroundjs.path");
    }
}

