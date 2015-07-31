package org.egov.android.model;

import java.util.Date;

public interface IModel {

    public void setId(int id);

    public int getId();

    public void setTimestamp(Date timestamp);

    public Date getTimestamp();

}
