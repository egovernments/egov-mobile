package com.egovernments.egov.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.Toast;

import com.egovernments.egov.R;

public class BadgeUpdater {
    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {
        BadgeDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.notification_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }
        badge.setCount(count);
        icon.mutate();
        //noinspection ConstantConditions
        if(badge==null)
        {
            Toast.makeText(context, "Is NULL :(", Toast.LENGTH_LONG).show();
        }

        icon.setDrawableByLayerId(R.id.notification_badge, badge);

    }
}