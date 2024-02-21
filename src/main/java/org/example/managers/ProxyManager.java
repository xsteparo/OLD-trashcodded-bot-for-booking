package org.example.managers;

import lombok.Getter;
import org.example.model.ChinaProxy;
import org.example.util.CsvReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


@Getter
public class ProxyManager {

    private static final String CHECKER_PROXIES_FILE = "avail_check_proxies.csv";

    private static final String BOOKING_PROXIES_FILE = "book_proxies.csv";

    private final Map<String, List<ChinaProxy>> proxiesChecker = new HashMap<>();

    private final Map<String, List<ChinaProxy>> proxiesBooking = new HashMap<>();

    public ProxyManager(CsvReader csvReader, List<String> cities) {
        initProxies(csvReader, cities);
    }

    private void initProxies(CsvReader csvReader, List<String> cities) {
        initMaps(cities);
        for (String city : cities) {
            List<String[]> proxyRowChecker = csvReader.extractProxies(city, CHECKER_PROXIES_FILE);
            proxyRowChecker.forEach(r -> {
                List<ChinaProxy> proxies = proxiesChecker.get(city);
                proxies.add(buildProxy(r));
            });
            List<String[]> proxyRowsBooking = csvReader.extractProxies(city, BOOKING_PROXIES_FILE);
            proxyRowsBooking.forEach(r -> {
                List<ChinaProxy> proxies = proxiesBooking.get(city);
                proxies.add(buildProxy(r));
            });
        }
    }

    private ChinaProxy buildProxy(String[] row) {
        return ChinaProxy.builder()
                .username(row[0])
                .password(row[1])
                .ipAddress(row[2])
                .port(Integer.parseInt(row[3]))
                .valid(new AtomicBoolean(true))
                .build();
    }

    private void initMaps(List<String> cities) {
        for (String city : cities) {
            proxiesChecker.put(city, new ArrayList<>());
            proxiesBooking.put(city, new ArrayList<>());
        }
    }

    public List<ChinaProxy> getCheckerProxiesForCity(String city) {
        return proxiesChecker.get(city);
    }

    public List<ChinaProxy> getBookingProxiesForCity(String city) {
        return proxiesBooking.get(city);
    }

}
