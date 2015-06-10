package com.egov.android.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.egov.android.library.annotation.Table;
import com.egov.android.library.model.BaseModel;
import com.egov.android.library.model.IModel;

@Table(name = "complaint")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Complaint extends BaseModel implements IModel {
    private String name = "";
    private String image = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchString() {
        return this.name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

}
