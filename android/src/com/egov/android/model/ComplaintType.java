package com.egov.android.model;

import com.egov.android.library.model.BaseModel;
import com.egov.android.library.model.IModel;

public class ComplaintType extends BaseModel implements IModel {

    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
