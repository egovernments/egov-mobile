package org.egov.android.model;

import org.egov.android.model.BaseModel;
import org.egov.android.model.IModel;

public class ComplaintType extends BaseModel implements IModel{
    
    private int id = 0;
    private String name = "";
    private String description = "";
    private int image = 0;
    private String imagePath = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
