package org.egov.android.listener;

public class Event<E> {

    private String type = "";
    private E data = null;
    private boolean stopPropogation = false;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public boolean isStopPropogation() {
        return stopPropogation;
    }

    public void stopPropogation(boolean stopPropogation) {
        this.stopPropogation = stopPropogation;
    }

}
