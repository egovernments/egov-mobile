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

import java.util.Calendar;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.egov.android.annotation.Column;
import org.egov.android.annotation.Table;
import org.egov.android.data.ColumnType;
import org.egov.android.model.BaseModel;

/**
 * This is to create cache table with the fields url, data etc. We will check the cache time expiry.
 */

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
        long timeDiff = (Calendar.getInstance().getTimeInMillis() - this.getTimestamp().getTime()) / 1000;
        return (timeDiff > this.getDuration());
    }

}
