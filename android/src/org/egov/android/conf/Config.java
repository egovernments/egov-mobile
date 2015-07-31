package org.egov.android.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    Properties property = null;

    public Config(InputStream inputStream) {

        try {
            property = new Properties();
            property.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
        }
    }

    public String getDatabaseName() {
        return property.getProperty("db.name");
    }

    public int getDatabaseVersion() {
        return Integer.valueOf(property.getProperty("db.version", "1"));
    }

    public long getCacheDuration() {
        return Integer.valueOf(property.getProperty("cache.duration", "300"));
    }

    public String getDateFormat() {
        return property.getProperty("data.format", "yyyy-MM-dd HH:mm:ss");
    }

    public Object get(String key, Object defaultValue) {
        return (property.get(key) == null) ? defaultValue : property.get(key);
    }

    public String getString(String key) {
        return this.get(key, "").toString();
    }

    public int getInt(String key) {
        return Integer.valueOf(this.get(key, 0).toString());
    }

    public float getFloat(String key) {
        return Float.valueOf(this.get(key, 0).toString());
    }

}
