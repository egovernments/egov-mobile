package com.egov.android.data;

import android.content.Context;

import com.egov.android.library.data.SQLiteHelper;

public class SQLiteDB extends SQLiteHelper {

    public SQLiteDB(Context context, String database, int version) {
        super(context, database, version);
    }
}
