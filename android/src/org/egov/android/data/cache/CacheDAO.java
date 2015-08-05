/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.data.cache;

import java.util.List;

import org.egov.android.data.ActiveDAO;

import android.util.Log;

public class CacheDAO extends ActiveDAO<Cache> {

    public CacheDAO() {
        super(Cache.class);
    }

    /**
     * It will check whether data is available or not in the local database. If data is available and is within expiry
     * then it will return true otherwise it will return false
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
