package org.example.webdriver;

import lombok.Data;
import org.example.model.ChinaProxy;
import org.openqa.selenium.WebDriver;

@Data
public class WebDriverWrapper {
    private WebDriver webDriver;
    private ChinaProxy proxy;
    private int currentIndex;
}
