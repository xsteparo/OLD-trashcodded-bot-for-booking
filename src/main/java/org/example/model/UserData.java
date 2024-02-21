package org.example.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserData {

    private String nameSurname;

    private String phone;

    private String email;

    private String appointmentNumber;

    private boolean registered;

    public String getLogInfo() {
        return String.format("%s %s %s %s", getNameSurname(), getPhone(), getEmail(), getAppointmentNumber());
    }
}
