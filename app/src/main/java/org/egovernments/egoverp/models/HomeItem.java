package org.egovernments.egoverp.models;


public class HomeItem {

    private String title;
    private String description;
    private int icon;

    public HomeItem(String title, int icon, String description) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIcon() {
        return icon;
    }
}
