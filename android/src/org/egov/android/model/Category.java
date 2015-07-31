package org.egov.android.model;

import org.egov.android.library.model.BaseModel;
import org.egov.android.library.model.IModel;

public class Category extends BaseModel implements IModel {

    private String title;
    private int image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
