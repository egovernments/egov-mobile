package org.egov.android.data.cache;

import java.util.Calendar;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.egov.android.annotation.Column;
import org.egov.android.annotation.Table;
import org.egov.android.data.ColumnType;
import org.egov.android.model.BaseModel;

import android.util.Log;

@Table(name = "cache")
public class Cache extends BaseModel {

    @Column
    private String url = "";

    @Column
    private Object data = "";

    @Column
    private String ref = "";

    @Column(type = ColumnType.INTEGER)
    private long duration = 0;

    @Column
    private String timezone = "";

    private final static String TAG = "Cache";

    public Cache() {

    }

    public Cache(String url, long duration) {
        this.url = url;
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public Cache setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getRef() {
        return ref;
    }

    public Cache setRef(String ref) {
        this.ref = ref;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public Cache setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public String getTimezone() {
        return timezone;
    }

    public Cache setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public Object getData() {
        return this.data;
    }

    public Cache setData(Object data) {
        this.data = data;
        return this;
    }

    @JsonIgnore(value = true)
    public boolean hasExpired() {
        Log.d(TAG, String.valueOf(this.getTimestamp()));
        long timeDiff = (Calendar.getInstance().getTimeInMillis() - this.getTimestamp().getTime()) / 1000;
        Log.d(TAG, String.valueOf(timeDiff));
        return (timeDiff > this.getDuration());
    }

}
