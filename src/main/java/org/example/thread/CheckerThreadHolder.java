package org.example.thread;

import org.example.managers.ProxyManager;
import org.example.managers.UserDataManager;
import org.example.model.ChinaProxy;
import org.example.model.UserData;

import java.util.ArrayList;
import java.util.List;

public class CheckerThreadHolder {
    private final List<String> cities;
    private final ProxyManager proxyManager;
    private final UserDataManager userDataManager;
    private final List<BrowserSessionThread> allThreads = new ArrayList<>();  // Хранение всех потоков

    public CheckerThreadHolder(List<String> cities, ProxyManager proxyManager, UserDataManager userDataManager) {
        this.cities = cities;
        this.proxyManager = proxyManager;
        this.userDataManager = userDataManager;


    }
    public void startCheckers() {
        for (String city : cities) {
            List<ChinaProxy> proxies = proxyManager.getCheckerProxiesForCity(city);
            List<UserData> userData = userDataManager.getCheckerUserDataForCity(city);
            List<ChinaProxy> proxiesBooker = proxyManager.getBookingProxiesForCity(city);
            List<UserData> userDataBooker = userDataManager.getBookingUserDataForCity(city);

            AvailabilityCheckerThread checkerThread = new AvailabilityCheckerThread(city, userData.get(0), proxies, "CHECKER", userDataBooker, proxiesBooker);
            allThreads.add(checkerThread);

            checkerThread.start();
        }

    }

    public void stopAllCheckers() {
        for (BrowserSessionThread thread : allThreads) {
            thread.stopThread();  // Вызов метода остановки потока
        }
        allThreads.clear(); // Очищаем список потоков
    }
}
