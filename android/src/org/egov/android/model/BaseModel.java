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

package org.egov.android.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.egov.android.annotation.Column;
import org.egov.android.data.ColumnType;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseModel implements IModel {

	@Column(type = ColumnType.INTEGER, isAutoIncrement = true, isPrimaryKey = true)
	private int id = 0;

	@Column(type = ColumnType.TIMESTAMP)
	private Date timestamp = null;

	// ------------------------------------------------------------------------------------------------//

	public BaseModel() {

	}

	// ------------------------------------------------------------------------------------------------//

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	// ------------------------------------------------------------------------------------------------//

	public String getTimestampAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		return sdf.format(this.timestamp);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@JsonIgnore
	public void setTimestamp(String timestamp) {
		try {
			this.timestamp = new SimpleDateFormat("yyy-MM-dd HH:mm:ss")
					.parse(timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
