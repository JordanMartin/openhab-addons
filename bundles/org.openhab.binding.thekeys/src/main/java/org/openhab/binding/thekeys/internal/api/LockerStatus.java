package org.openhab.binding.thekeys.internal.api;

public class LockerStatus {

    public static int CODE_LOCK_CLOSE = 0x31;
    public static int CODE_LOCK_OPEN = 0x32;

    private String status;
    private int code;
    private int id;
    private int version;
    private int position;
    private int rssi;
    private int battery;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public boolean isClosed() {
        return CODE_LOCK_CLOSE == code;
    }

    public boolean isOpened() {
        return CODE_LOCK_OPEN == code;
    }
}
