
package com.egovernments.egov.helper;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is to read the egov.conf file. If the value is assigned in the assets/egov.conf file then it will take
 * that value. If not then it will take the default value from here.
 */
public class ConfigManager {

    Properties property = null;
    Context appContext = null;

    public ConfigManager(InputStream inputStream, Context appContext) {

        try {
            property = new Properties();
            property.load(inputStream);
            inputStream.close();
            this.appContext = appContext;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
