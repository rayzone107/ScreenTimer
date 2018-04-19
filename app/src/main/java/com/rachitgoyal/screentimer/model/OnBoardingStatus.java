package com.rachitgoyal.screentimer.model;

import com.orm.SugarRecord;

/**
 * Created by Rachit Goyal on 19/04/18
 */

public class OnBoardingStatus extends SugarRecord {

    private boolean firstScreenOnBoardingDone;

    public boolean isFirstScreenOnBoardingDone() {
        return firstScreenOnBoardingDone;
    }

    public void setFirstScreenOnBoardingDone(boolean firstScreenOnBoardingDone) {
        this.firstScreenOnBoardingDone = firstScreenOnBoardingDone;
    }
}
