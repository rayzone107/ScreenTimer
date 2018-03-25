package com.rachitgoyal.screentimer.modules.tears;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rachit Goyal on 17/03/18
 */

public interface TearsContract {
    interface View {
        void setScale(List<TextView> bars);

        void setData(float currentPercentage, int waterColor, String timeValue, float eyeOverlayOpacity);

        void createAllowedTimeBar(android.view.View allowedTimeBar);

        void addTearDrop(ImageView teardropIV);

        void removeTearDrop(ImageView teardropIV);
    }

    interface Presenter {
        void setData(Context context);

        void reloadData(Context context);

        void animateTearDrops(Context context, float eyeX, float eyeY, int eyeHeight, int eyeWidth, int waveHeight, int parentHeight);

        void showAllowedTimeBar(Context context, int waveHeight, int parentHeight);
    }
}
