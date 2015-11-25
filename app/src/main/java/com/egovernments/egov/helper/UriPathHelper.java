package com.egovernments.egov.helper;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Returns the absolute path of a file referred to by a uri
 **/

public class UriPathHelper {

    public static String getRealPathFromURI(Uri contentUri, Context context) {

        try {
            String[] strings = {MediaStore.Images.Media.DATA};
            String s = null;

            Cursor cursor = context.getContentResolver().query(contentUri, strings, null, null, null);
            int column_index;
            if (cursor != null) {
                column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                s = cursor.getString(column_index);
                cursor.close();
            }
            if (s != null) {
                return s;
            }
        } catch (Exception e) {
            return contentUri.getPath();
        }

        return contentUri.getPath();

    }
}
