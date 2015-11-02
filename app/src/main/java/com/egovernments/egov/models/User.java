package com.egovernments.egov.models;


public class User {

    private String emailId;
    private String mobileNumber;
    private String name;
    private String password;
    private String deviceId;
    private String deviceType;
    private String OSVersion;

    public User(String emailId, String mobileNumber, String name, String password, String deviceId, String deviceType, String OSVersion) {
        this.emailId = emailId;
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.password = password;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.OSVersion = OSVersion;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getOSVersion() {
        return OSVersion;
    }
}
