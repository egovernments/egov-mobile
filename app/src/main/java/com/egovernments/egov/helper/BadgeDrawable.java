package com.egovernments.egov.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.egovernments.egov.R;


class BadgeDrawable extends Drawable {
    private Paint mBadgePaint;
    private Paint mTextPaint;
    private Rect mTxtRect = new Rect();
    private String mCount = "";
    private boolean mWillDraw = false;
    public BadgeDrawable(Context context) {
        float mTextSize = 18F;
        mBadgePaint = new Paint();
        mBadgePaint.setColor(context.getResources().getColor(R.color.colorAccent));
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }
    @Override
    public void draw(Canvas canvas) {
        if (!mWillDraw) {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;
        // Position the notification_badge in the top-right quadrant of the icon.
        float radius = ((Math.min(width, height) / 3) - 1) ;
        float centerX = width - radius - 1;
        float centerY = radius - 2;
        // Draw notification_badge circle.
        canvas.drawCircle(centerX, centerY, radius, mBadgePaint);
        // Draw notification_badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = (centerY + (textHeight / 2f)) - 2;
        canvas.drawText(mCount, centerX, textY, mTextPaint);
    }
    /*
    Sets the count (i.e notifications) to display.
     */
    public void setCount(int count) {
        mCount = Integer.toString(count);
        // Only draw a notification_badge if there are notifications.
        mWillDraw = count > 0;
        invalidateSelf();
    }
    @Override
    public void setAlpha(int alpha) {
        // do nothing
    }
    @Override
    public void setColorFilter(ColorFilter cf) {
        // do nothing
    }
    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}