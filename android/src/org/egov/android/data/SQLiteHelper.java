package org.egov.android.data;

import org.egov.android.AndroidLibrary;
import org.egov.android.conf.Config;
import org.egov.android.data.cache.CacheDAO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper implements ISQLiteHelper {

	private static final String TAG = SQLiteHelper.class.getName();

	private static SQLiteHelper instance = null;
	private Context context = null;

	// ------------------------------------------------------------------------------------------------//

	public SQLiteHelper(Context context, String database, int version) {
		super(context, database, null, version);
		this.context = context;
		Log.i(TAG, "Constructor");
	}

	/**
	 * Get SQLiteDB instance
	 * 
	 * @param context
	 * @return SQLiteDB
	 */
	public static SQLiteHelper newInstance(Context context) {
		Log.d(TAG, "New Instance***************");
		if (instance == null) {
			Config cfg = AndroidLibrary.getInstance().getConfig();
			instance = new SQLiteHelper(context, cfg.getDatabaseName(),
					cfg.getDatabaseVersion());
		}
		return instance;
	}

	public static SQLiteHelper getInstance() {
		Log.d(TAG, "getInstance***************");
		return instance;
	}

	public void initialize() {
		Log.d(TAG, "initialize***************");
		SQLiteDatabase db = getReadableDatabase();
		db.close();
	}

	// ------------------------------------------------------------------------------------------------//

	@Override
	public void onCreate(SQLiteDatabase db) {

		CacheDAO helper = new CacheDAO();
		String sql = helper.generateCreateScript();
		Log.d(TAG, sql);
		db.execSQL(sql);

		// helper.createModel(City.class);
		// sql = helper.generateCreateScript();
		// Log.d(TAG, sql);
		// db.execSQL(sql);
		// Log.d(TAG, "on create-3");
	}

	// ------------------------------------------------------------------------------------------------//

	/**
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

	// ------------------------------------------------------------------------------------------------//

	public Cursor query(String sql, String[] selectionArgs) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		//db.close();
		return cursor;
	}

	// ------------------------------------------------------------------------------------------------//
	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(tableName, columns, selection, selectionArgs,
				groupBy, having, orderBy, limit);

		// db.close();
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
	public int getRecordCount(String table, String selection,
			String[] selectionArgs, String limit) {
		try {
			String[] columns = { " count(*) as totalRows" };
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.query(table, columns, selection, selectionArgs,
					null, null, null, limit);
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

	// ------------------------------------------------------------------------------------------------//

	public int getRecordCount(String table, String selection,
			String[] selectionArgs) {
		return this.getRecordCount(table, selection, selectionArgs, null);
	}

	// ------------------------------------------------------------------------------------------------//

	public void truncate(String tableName) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(tableName, null, null);
		db.close();
	}

	// ------------------------------------------------------------------------------------------------//

	public long insert(String tableName, ContentValues cv) {
		SQLiteDatabase db = getWritableDatabase();
		long result = db.insert(tableName, null, cv);
		db.close();
		return result;
	}

	// ------------------------------------------------------------------------------------------------//

	public int update(String tableName, ContentValues cv, String whereClause,
			String[] whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		int result = db.update(tableName, cv, whereClause, whereArgs);
		db.close();
		return result;
	}

	// ------------------------------------------------------------------------------------------------//

	public int delete(String tableName, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		int result = db.delete(tableName, whereClause, whereArgs);
		db.close();
		return result;
	}

}
