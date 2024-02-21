package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicBoolean;

@Builder

public class ChinaProxy {
    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    private String ipAddress;
    @Getter
    private int port;

//    private volatile boolean valid;

    private AtomicBoolean valid;

    public synchronized boolean isValid() {
        return valid.get();
    }

    public synchronized void setValid(boolean valid) {
        this.valid.set(valid);
    }

}
