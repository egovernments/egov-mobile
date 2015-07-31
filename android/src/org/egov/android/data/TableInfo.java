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
