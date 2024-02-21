package org.example;

import org.example.managers.ProxyManager;
import org.example.managers.UserDataManager;
import org.example.thread.CheckerThreadHolder;
import org.example.util.ConsoleCommandExecutor;
import org.example.util.CsvReader;

import java.util.List;

public class Main {

    public static long lastClearTime = System.currentTimeMillis();
    private static final long CLEAR_INTERVAL = 10 * 60 * 1000; // 1 минута в миллисекундах

    public static boolean shouldClearConsole() {
        return System.currentTimeMillis() - lastClearTime >= CLEAR_INTERVAL;
    }

    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("powershell", "Clear-Host");
            } else {
                // Для других ОС, возможно, потребуется другой код. Здесь мы просто оставим "clear" для Unix/Linux.
                processBuilder = new ProcessBuilder("clear");
            }

            processBuilder.inheritIO().start().waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLastClearTime() {
        lastClearTime = System.currentTimeMillis();
    }
    public static void main(String[] args) {


        List<String> cities = List.of("Хабаровск");

        CsvReader csvReader = new CsvReader();
        ProxyManager proxyManager = new ProxyManager(csvReader, cities);
        UserDataManager userDataManager = new UserDataManager(csvReader, cities);
        CheckerThreadHolder checkerThreadHolder = new CheckerThreadHolder(cities, proxyManager, userDataManager);

        ConsoleCommandExecutor commandExecutor = new ConsoleCommandExecutor(checkerThreadHolder);
        commandExecutor.startCommandLoop();
    }
}