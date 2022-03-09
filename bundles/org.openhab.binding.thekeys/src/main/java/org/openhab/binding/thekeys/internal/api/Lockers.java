package org.openhab.binding.thekeys.internal.api;

import java.util.ArrayList;
import java.util.List;

public class Lockers {

    private String status;
    private List<Locker> devices = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Locker> getDevices() {
        return devices;
    }

    public void setDevices(List<Locker> devices) {
        this.devices = devices;
    }
}
