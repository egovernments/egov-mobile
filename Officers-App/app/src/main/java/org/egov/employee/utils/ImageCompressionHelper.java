/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egov.employee.utils;

/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Reduces image resolution and color density before upload
 **/

public class ImageCompressionHelper {

    private static final int GRID_HEIGHT = 40;
    private static final int OFFSET_X_TEXT = 15;
    private static final int GRID_MARIGN = 13;
    private static final String CAPTURED_DATE_PREFIX_TEXT = "Captured Date & Time: ";
    private static final String DATE_FORMAT_TO_DISPLAY = "dd-MM-yyyy hh:mm:ss aa";
    private static final String LATITUDE_PREFIX_TEXT = "Latitude: ";
    private static final String LONGITUDE = "Longitude: ";
    private static final String LONGITUDE_PREFIX_TEXT = LONGITUDE;
    private static final String COMMA_SEPARATOR = ", ";

    public static String compressImage(String sourceFilePath, String outputFilePath) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = null; //= BitmapFactory.decodeFile(sourceFilePath, options);

        try {
            InputStream in = new FileInputStream(sourceFilePath);
            bmp = BitmapFactory.decodeStream(in, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 1280.0f;
        float maxWidth = 720.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image cddc0ef0-f9fb-43f4-ad65-59873d3689dc

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        /*options.inPurgeable = true;*/
        options.inBitmap = bmp;
        /*options.inInputShareable = true;*/
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inTempStorage = new byte[8 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(outputFilePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        assert scaledBitmap != null;
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        String attrLatitute = null;
        String attrLatituteRef = null;
        String attrLONGITUDE=null;
        String attrLONGITUDEREf=null;

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(sourceFilePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }

            attrLatitute = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            attrLatituteRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            attrLONGITUDEREf = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);


            /*Canvas canvasInfo = new Canvas(scaledBitmap);

            TextPaint mTextPaint=new TextPaint();
            StaticLayout mTextLayout = new StaticLayout("Photo Taken On : 23132131321", mTextPaint, canvasInfo.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            canvasInfo.save();
            // calculate x and y position where your text will be placed

            canvas.translate(0, canvas.getHeight()-mTextLayout.getHeight());
            mTextLayout.draw(canvas);
            canvasInfo.restore();*/


            Canvas canvasInfo = new Canvas(scaledBitmap);

            int[] textSizes = getTextSize(canvasInfo);

            Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE); // Text Color
            paint.setStrokeWidth(textSizes[0]); // Text Size
            paint.setTextSize(textSizes[1]);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
            // some more settings...

            canvasInfo.drawBitmap(scaledBitmap, 0, 0, paint);

            Paint transBlackPaint = new Paint();
            transBlackPaint.setColor(Color.BLACK);
            transBlackPaint.setAlpha(127);
            transBlackPaint.setStyle(Paint.Style.FILL);

            String createDateTimeText = exif.getAttribute(ExifInterface.TAG_DATETIME);
            Date createdDate = new Date();
            if (!TextUtils.isEmpty(createDateTimeText) && !createDateTimeText.equals("null")) {
                try {
                    createdDate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH).parse(createDateTimeText);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder stringBuilder = new StringBuilder(CAPTURED_DATE_PREFIX_TEXT)
                    .append(new SimpleDateFormat(DATE_FORMAT_TO_DISPLAY).format(createdDate).toUpperCase());

            Rect rectDateTime = new Rect(0, canvas.getHeight() - GRID_HEIGHT, canvas.getWidth(), canvas.getHeight());

            //print the date time of the file
            canvasInfo.drawRect(rectDateTime, transBlackPaint);
            canvasInfo.drawText(stringBuilder.toString(), OFFSET_X_TEXT, canvas.getHeight() - GRID_MARIGN, paint);

            StringBuilder latLngStrBuilder = new StringBuilder();

            //print the lat, lng if it's has
            if (attrLatitute != null) {
                latLngStrBuilder.append(LATITUDE_PREFIX_TEXT).append(stringToDegree(attrLatitute))
                        .append(COMMA_SEPARATOR).append(LONGITUDE_PREFIX_TEXT).append(stringToDegree(attrLONGITUDE));
            } else if (attrLatituteRef != null) {
                latLngStrBuilder.append(LATITUDE_PREFIX_TEXT).append(stringToDegree(attrLatituteRef))
                        .append(COMMA_SEPARATOR).append(LONGITUDE_PREFIX_TEXT).append(stringToDegree(attrLONGITUDEREf));
            }

            if (!TextUtils.isEmpty(latLngStrBuilder.toString())) {
                Rect rectLatLng = new Rect(0, canvas.getHeight() - (GRID_HEIGHT * 2), canvas.getWidth(), canvas.getHeight() - GRID_HEIGHT);
                canvasInfo.drawRect(rectLatLng, transBlackPaint);
                canvasInfo.drawText(latLngStrBuilder.toString(), OFFSET_X_TEXT, canvas.getHeight() - (GRID_MARIGN * 4), paint);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out;
        try {
            out = new FileOutputStream(outputFilePath);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

            ExifInterface exif2 = new ExifInterface(outputFilePath);

            if(attrLatitute != null)
            {
                exif2.setAttribute(ExifInterface.TAG_GPS_LATITUDE, attrLatitute);
                exif2.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, attrLONGITUDE);
            }

            if(attrLatituteRef != null)
            {
                exif2.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, attrLatituteRef);
                exif2.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, attrLONGITUDEREf);
            }

            exif2.saveAttributes();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFilePath;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private static String stringToDegree(String text) {
        return new DecimalFormat("#0.0000000").format(AppUtils.convertToDegree(text));
    }

    private static int[] getTextSize(Canvas canvas) {

        if (canvas.getHeight() <= 720) {
            //landscape
            return new int[]{8, 18};
        } else {
            return new int[]{12, 25};
        }

    }

}
