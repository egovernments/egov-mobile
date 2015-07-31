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
