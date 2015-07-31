package org.egov.android.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface ISQLiteHelper {

	public void initialize();

	public Cursor query(String sql, String[] selectionArgs);

	public Cursor query(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit);

	public int getRecordCount(String table, String selection, String[] selectionArgs, String limit);

	public long insert(String tableName, ContentValues cv);

	public int update(String tableName, ContentValues cv, String whereClause, String[] whereArgs);

	public int delete(String tableName, String whereClause, String[] whereArgs);

	public void truncate(String tableName);

	public SQLiteDatabase getReadableDatabase();

	public SQLiteDatabase getWritableDatabase();

	public void execSQL(String sql);

}
