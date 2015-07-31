package org.egov.android.data.cache;

import java.util.List;

import org.egov.android.data.ActiveDAO;

import android.util.Log;

public class CacheDAO extends ActiveDAO<Cache> {

    public CacheDAO() {
        super(Cache.class);
    }

    /**
     * It will check whether data is available or not in the local database if data available and it
     * will check the expiry also both or ok then it will return true otherwise it will return false
     * 
     * @return boolean
     * 
     */
    public boolean hasData(String url) {
        Log.d(TAG, "hasData");
        return this.get(url) != null;
    }
    

    public Cache get(String url) {
        Log.d(TAG, "_get");
        List<Cache> list = super.get("url=?", new String[] { url });
        if (list.size() < 1) {
            Log.d(TAG, "Not data");
            return null;
        }
        Cache cache = list.get(0);
        if (cache.hasExpired()) {
            Log.d(TAG, "expired");
            this.delete("url=?", new String[] { url });
            return null;
        }
        return cache;
    }
}
