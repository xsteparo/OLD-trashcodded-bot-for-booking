package org.example.managers;

import lombok.Getter;
import org.example.model.UserData;
import org.example.util.CsvReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class UserDataManager {

    private static final String DATA_FOR_FORM_FILE = "data_for_form.csv";

    private static final String TRAINING_DATA_FORM_FILE = "training_data_form.csv";

    private final Map<String, List<UserData>> checkerUserData = new HashMap<>();

    private final Map<String, List<UserData>> bookingUserData = new HashMap<>();

    public UserDataManager(CsvReader csvReader, List<String> cities) {
        initProxies(csvReader,cities);
    }

    private void initProxies(CsvReader csvReader, List<String> cities){
        initMaps(cities);
        for (String city : cities){
            List<String[]> traininguserDataList = csvReader.extractAppointmentsData(city, TRAINING_DATA_FORM_FILE);
            traininguserDataList.forEach(r -> {
                List<UserData> proxies = checkerUserData.get(city);
                proxies.add(buildUserData(r));
            });
            List<String[]> userDataList = csvReader.extractAppointmentsData(city, DATA_FOR_FORM_FILE);
            userDataList.forEach(r -> {
                List<UserData> proxies = bookingUserData.get(city);
                proxies.add(buildUserData(r));
            });
        }
    }

    private UserData buildUserData(String[] row){
        return UserData.builder()
                .nameSurname(row[0])
                .phone(row[1])
                .email(row[2])
                .appointmentNumber(row[3])
                .build();
    }

    private void initMaps(List<String> cities){
        for (String city: cities){
            checkerUserData.put(city, new ArrayList<>());
            bookingUserData.put(city, new ArrayList<>());
        }
    }


    public List<UserData> getCheckerUserDataForCity(String city){
        return checkerUserData.get(city);
    }

    public List<UserData> getBookingUserDataForCity(String city){
        return bookingUserData.get(city);
    }
}
