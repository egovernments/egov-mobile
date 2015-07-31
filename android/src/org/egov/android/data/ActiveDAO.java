package org.egov.android.data;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.egov.android.AndroidLibrary;
import org.egov.android.annotation.Column;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.IApiListener;
import org.egov.android.common.DateUtil;
import org.egov.android.common.Purpose;
import org.egov.android.conf.Config;
import org.egov.android.filter.FieldFilter;
import org.egov.android.filter.IFilter;
import org.egov.android.listener.Event;
import org.egov.android.model.IModel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ActiveDAO<E> implements IApiListener {

    public static String TAG = ActiveDAO.class.getName();

    private Config conf = null;
    private E model = null;
    private Class<E> modelClass = null;
    private TableInfo tableInfo = null;

    public ActiveDAO() {
        this._init(null);
    }

    public ActiveDAO(Class<E> clazz) {
        this._init(clazz);
    }

    private void _init(Class<E> clazz) {
        this.conf = AndroidLibrary.getInstance().getConfig();
        this.modelClass = clazz;
        this.tableInfo = new TableInfo(clazz);
    }

    public E getModel() {
        return model;
    }

    public ActiveDAO<E> setModel(E model) {
        this.model = model;
        this.modelClass = (Class<E>) model.getClass();
        this.tableInfo = new TableInfo(this.modelClass);
        return this;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public E createFromCursor(Cursor cursor, List<String> columnNames) {
        E objInstance = null;
        try {
            objInstance = (E) this.modelClass.newInstance();
            List<Field> fields = this.tableInfo.getFields(this.getFieldFilter(
                    Purpose.CURSOR_TO_OBJECT, columnNames));
            Iterator<Field> itFields = fields.iterator();
            while (itFields.hasNext()) {
                try {
                    Field field = itFields.next();
                    field.setAccessible(true);

                    Column col = field.getAnnotation(Column.class);
                    int colIndex = cursor.getColumnIndex(field.getName());

                    switch (col.type()) {
                        case TEXT:
                            field.set(objInstance, cursor.getString(colIndex));
                            break;
                        case FLOAT:
                            field.set(objInstance, cursor.getFloat(colIndex));
                            break;
                        case DOUBLE:
                            field.set(objInstance, cursor.getDouble(colIndex));
                            break;
                        case INTEGER:
                            field.set(objInstance, cursor.getInt(colIndex));
                            break;
                        case TIMESTAMP:
                            field.set(
                                    objInstance,
                                    DateUtil.toDateTime(cursor.getString(colIndex),
                                            conf.getDateFormat()));
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {

                }
            }
        } catch (Exception e1) {
        }

        return objInstance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getCursorData(Cursor cursor, String name, Class<T> data) {
        String t = data.getSimpleName();
        if (cursor.getColumnIndex(name) == -1) {
            return null;
        }
        int columnIndex = cursor.getColumnIndex(name);
        if (t.equals("Integer")) {
            return (T) Integer.valueOf(cursor.getInt(columnIndex));

        } else if (t.equals("Double")) {
            return (T) Double.valueOf(cursor.getDouble(columnIndex));

        } else if (t.equals("Long")) {
            return (T) Double.valueOf(cursor.getLong(columnIndex));

        } else if (t.equals("Float")) {
            return (T) Float.valueOf(cursor.getFloat(columnIndex));

        } else if (t.equals("Date")) {
            SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            try {
                return (T) df.parse(cursor.getString(columnIndex));
            } catch (ParseException e) {
            }
        }
        return (T) cursor.getString(columnIndex);
    }

    /**
     * @return
     * 
     */
    public long save() {
        IModel m = (IModel) this.model;
        m.setTimestamp(new Date());
        ContentValues cv = toContentValues(Purpose.INSERT);
        return SQLiteHelper.getInstance().insert(this.tableInfo.getTableName(), cv);
    }

    // ------------------------------------------------------------------------------------------------//

    /**
     * 
     */
    public int update(String whereClause, String[] whereArgs) {
        ContentValues cv = toContentValues(Purpose.UPDATE);
        return SQLiteHelper.getInstance().update(this.tableInfo.getTableName(), cv, whereClause,
                whereArgs);
    }

    // ------------------------------------------------------------------------------------------------//

    /**
     * 
     */
    public int delete(String whereClause, String[] whereArgs) {
        return SQLiteHelper.getInstance().delete(this.tableInfo.getTableName(), whereClause,
                whereArgs);
    }

    public List<E> get(String selection, String[] selectionArgs) {
        return this.get(null, selection, selectionArgs);
    }

    /**
     * 
     */
    public List<E> get(String[] columns, String selection, String[] selectionArgs) {
        return this.get(columns, selection, selectionArgs, null, null, null, null);
    }

    public List<E> get(String[] columns,
                       String selection,
                       String[] selectionArgs,
                       String groupBy,
                       String having,
                       String orderBy,
                       String limit) {
        Log.d(TAG, "get");
        /**
         * Check here need of getReadableDatabase()
         */
        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.query(this.tableInfo.getTableName(), columns, selection, selectionArgs,
                groupBy, having, orderBy, limit);

        List<E> list = new ArrayList<E>();

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            List<String> columnNames = Arrays.asList(cursor.getColumnNames());
            while (!cursor.isAfterLast()) {
                list.add(this.createFromCursor(cursor, columnNames));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    protected IFilter<Field, Column> getFieldFilter(Purpose purpose) {
        return this.getFieldFilter(purpose, null);
    }

    protected IFilter<Field, Column> getFieldFilter(Purpose purpose, List<String> fieldNames) {
        FieldFilter f = new FieldFilter(purpose);
        f.setFieldNames(fieldNames);
        return f;
    }

    /**
     * 
     */
    public ContentValues toContentValues() {
        return this.toContentValues(null);
    }

    /**
     * 
     * @param excludes
     * @return
     */
    public ContentValues toContentValues(Purpose purpose) {
        Log.d(TAG, "_getContentValue");
        String fn = "";
        ContentValues cv = new ContentValues();
        List<Field> fields = this.tableInfo.getFields(this.getFieldFilter(purpose));
        Iterator<Field> itFields = fields.iterator();
        while (itFields.hasNext()) {
            try {
                Field field = itFields.next();
                fn = field.getName();
                field.setAccessible(true);
                Object value = field.get(this.model);
                Column col = field.getAnnotation(Column.class);
                if (col.type().equals(ColumnType.TIMESTAMP) && value != null && !value.equals("")) {
                    cv.put(fn, DateUtil.getCurrentDateTime((Date) value));
                } else {
                    cv.put(fn, value == null ? "" : value.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cv;
    }

    /**
     * 
     * @param source
     * @param clazz
     * @return
     * @deprecated
     */
    public <T extends IModel> List<T> parseFromJson(String source, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        List<T> result = null;
        try {
            result = mapper.readValue(source,
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 
     * @param source
     * @return
     * @deprecated
     */
    public List<E> parseFromJson(String source) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        List<E> result = null;
        try {
            result = mapper.readValue(source,
                    mapper.getTypeFactory().constructCollectionType(List.class, this.modelClass));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String generateCreateScript() {
        List<Field> fields = this.tableInfo.getFields();
        Iterator<Field> itFields = fields.iterator();
        String type = "";
        StringBuffer sb = new StringBuffer();
        while (itFields.hasNext()) {
            Field field = itFields.next();

            // .replaceAll("([A-Z])", "_$1").toLowerCase();
            Column col = field.getAnnotation(Column.class);
            type = col.type().toString();
            boolean autoInc = col.isAutoIncrement();
            boolean isNull = col.allowNull();

            if (col.isPrimaryKey()) {
                autoInc = true;
                isNull = false;
            }

            // The order should me NOT NULL, PRIMARY KEY, AUTOINCREMENT

            if (!isNull) {
                type += " NOT NULL";
            }
            if (col.isPrimaryKey()) {
                type += " PRIMARY KEY";
            }
            if (autoInc) {
                type += " AUTOINCREMENT";
            }
            if (!col.defaultValue().equals("")) {
                type += " DEFAULT " + col.defaultValue();
            }

            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(field.getName() + " " + type);

        }

        String sql = "CREATE TABLE IF NOT EXISTS " + this.tableInfo.getTableName() + "("
                + sb.toString() + ")";
        return sql;
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {

    }

}
