package org.egov.android.data;

import org.egov.android.data.SQLiteHelper;

import android.content.Context;

public class SQLiteDB extends SQLiteHelper {

    public SQLiteDB(Context context, String database, int version) {
        super(context, database, version);
    }
}
