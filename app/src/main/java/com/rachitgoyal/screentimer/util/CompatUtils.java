package com.rachitgoyal.screentimer.util;

import android.content.Context;

/**
 * Created by Rachit Goyal on 20/03/18
 */

public class CompatUtils {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
