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

import org.egov.android.AndroidLibrary;
import org.egov.android.conf.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This is to create database
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper instance = null;

    public SQLiteHelper(Context context, String database, int version) {
        super(context, database, null, version);
    }

    /**
     * Get SQLiteDB instance
     * 
     * @param context
     * @return SQLiteDB
     */
    public static SQLiteHelper newInstance(Context context) {
        if (instance == null) {
            Config cfg = AndroidLibrary.getInstance().getConfig();
            instance = new SQLiteHelper(context, cfg.getDatabaseName(), cfg.getDatabaseVersion());
        }
        return instance;
    }

    public static SQLiteHelper getInstance() {
        return instance;
    }

    public void initialize() {
        SQLiteDatabase db = getReadableDatabase();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * Update database version
     * 
     * @param database
     * @param oldVersion
     * @param newVersion
     * 
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SQLiteDB", "ON UPGRADE " + oldVersion + " , " + newVersion);

    }

    public Cursor query(String sql, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        return cursor;
    }

    public Cursor query(String tableName,
                        String[] columns,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderBy,
                        String limit) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having,
                orderBy, limit);

        return cursor;
    }

    public JSONArray query(String sql) {
        JSONArray data = new JSONArray();
        JSONObject row = null;
        Cursor cursor = query(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            row = new JSONObject();
            int cc = cursor.getColumnCount();
            for (int i = 0; i < cc; i++) {
                try {
                    row.put(cursor.getColumnName(i), cursor.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            data.put(row);
            cursor.moveToNext();
        }
        cursor.close();
        return data.length() == 0 ? null : data;
    }

    public void execSQL(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    /**
     * Get total records in particular table
     * 
     * @param table
     * @param selection
     * @param selectionArgs
     * @param limit
     * @return int
     */

    public int getRecordCount(String table, String selection, String[] selectionArgs, String limit) {
        try {
            String[] columns = { " count(*) as totalRows" };
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null,
                    limit);
            cursor.moveToFirst();
            int rows = cursor.getInt(0);
            cursor.close();
            db.close();
            return rows;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int getRecordCount(String table, String selection, String[] selectionArgs) {
        return this.getRecordCount(table, selection, selectionArgs, null);
    }

    /**
     * Truncate table
     * 
     * @param tableName
     */

    public void truncate(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    /**
     * Insert data into table
     * 
     * @param tableName
     * @param ContentValues
     */

    public long insert(String tableName, ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(tableName, null, cv);
        db.close();
        return result;
    }

    /**
     * Update data in table
     * 
     * @param tableName
     * @param ContentValues
     * @param whereClause
     * @param whereArgs
     */

    public int update(String tableName, ContentValues cv, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(tableName, cv, whereClause, whereArgs);
        db.close();
        return result;
    }

    /**
     * Delete data from table
     * 
     * @param tableName
     * @param whereClause
     * @param whereArgs
     */

    public int delete(String tableName, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(tableName, whereClause, whereArgs);
        db.close();
        return result;
    }

}
