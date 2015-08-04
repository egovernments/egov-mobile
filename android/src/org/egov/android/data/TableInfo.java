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

package org.egov.android.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.egov.android.annotation.Column;
import org.egov.android.annotation.Table;
import org.egov.android.filter.IFilter;

public class TableInfo {

	private Class<?> clazz = null;
	private String tableName = "";

	/**
	 * 
	 * @param clazz
	 */
	public TableInfo(Class<?> clazz) {
		this.clazz = clazz;
		this._init();
	}

	/**
     * 
     */
	private void _init() {
		if (this.clazz == null) {
			return;
		}
		if (this.clazz.isAnnotationPresent(Table.class)) {
			Table e = this.clazz.getAnnotation(Table.class);
			this.tableName = e.name().equals("") ? "" : "tbl_" + e.name();
		}
		if (this.tableName.equals("")) {
			String tbl = "tbl" + this.clazz.getSimpleName();
			this.tableName = tbl.replaceAll("([A-Z])", "_$1").toLowerCase();
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * 
	 * @return
	 */
	public List<Field> getFields() {
		return this.getFields(null);
	}

	public List<Field> getFields(IFilter<Field, Column> filter) {
		return this._getFields(this.clazz, filter);
	}

	/**
	 * 
	 * @param filter
	 * @return
	 */
	public List<Field> _getFields(Class<?> clazz, IFilter<Field, Column> filter) {
		List<Field> fieldList = new ArrayList<Field>();

		Class<?> c = clazz.getSuperclass();
		if (c != null && !c.getName().equals("java.lang.Object")) {
			fieldList.addAll(_getFields(c, filter));
		}
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class)) {
				Column col = field.getAnnotation(Column.class);
				if (filter == null) {
					fieldList.add(field);
				} else if (filter.filter(field, col)) {
					fieldList.add(field);
				}
			}
		}

		return fieldList;
	}

}
