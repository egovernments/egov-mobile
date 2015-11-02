package com.egovernments.egov.models;


import java.io.Serializable;

public class Property implements Serializable {

    private String propertyName;
    private String propertyAddress;
    private String propertyOwner;
    private String propertyTax;

    public Property(String propertyName, String propertyAddress, String propertyOwner, String propertyTax) {
        this.propertyName = propertyName;
        this.propertyAddress = propertyAddress;
        this.propertyOwner = propertyOwner;
        this.propertyTax = propertyTax;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public String getPropertyOwner() {
        return propertyOwner;
    }

    public String getPropertyTax() {
        return propertyTax;
    }

}
