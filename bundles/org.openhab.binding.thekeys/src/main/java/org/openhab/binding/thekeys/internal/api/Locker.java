package org.openhab.binding.thekeys.internal.api;

public class Locker {
    private int identifier;
    private int rssi;
    private int battery;
    private int lastLog;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getLastLog() {
        return lastLog;
    }

    public void setLastLog(int lastLog) {
        this.lastLog = lastLog;
    }
}
