package com.rachitgoyal.screentimer.modules.tears;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.Reminder;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeOptions;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.rachitgoyal.screentimer.util.TimeOptions.EIGHTEEN_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.EIGHT_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FIFTEEN_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FIFTEEN_MINS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FIVE_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FOURTY_FIVE_MINS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FOUR_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.ONE_HALF_HOUR;
import static com.rachitgoyal.screentimer.util.TimeOptions.ONE_HOUR;
import static com.rachitgoyal.screentimer.util.TimeOptions.SIX_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TEN_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.THIRTY_MINS;
import static com.rachitgoyal.screentimer.util.TimeOptions.THREE_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWELVE_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWENTY_FOUR_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWENTY_ONE_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWO_HALF_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWO_HOURS;

/**
 * Created by Rachit Goyal on 17/03/18
 */

public class TearsPresenter implements TearsContract.Presenter {

    private static final long SECONDS_IN_A_DAY = 86400;
    private TearsContract.View mView;
    private SharedPreferences mPrefs;
    private int mScaleMaxLimit;
    private long mMillisUsed;
    private int mTearCountdownThreshold = 0;
    private int mTearCountdown = 0;
    private int mCurrentAllowedTime = 0;

    TearsPresenter(TearsContract.View view, SharedPreferences sharedPreferences) {
        mView = view;
        mPrefs = sharedPreferences;
    }

    private int getAllowedTime() {
        return TimeUtil.getMillsFromTimeOption(mPrefs.getString(Constants.PREFERENCES.MAX_TIME_OPTION, ""));
    }

    @Override
    public void setData(Context context) {
        int allowedTime = getAllowedTime();
        setScale(context, allowedTime);
        addDefaultReminder(allowedTime);
        calculateData(context, allowedTime);
    }

    private void addDefaultReminder(int allowedTime) {
        List<Reminder> reminderList = Select.from(Reminder.class)
                .where(Condition.prop(Reminder.isAllowedTimeReminderField).eq("1")).list();
        if (reminderList.isEmpty()) {
            Reminder reminder = new Reminder(allowedTime, false, true, true);
            reminder.save();
        }
    }

    @Override
    public void reloadData(Context context) {
        int allowedTime = getAllowedTime();
        calculateData(context, allowedTime);
    }

    @Override
    public void animateTearDrops(Context context, float eyeX, float eyeY, int eyeHeight, int eyeWidth, int waveHeight, int parentHeight) {
        if (mTearCountdown == mTearCountdownThreshold) {
            mTearCountdown = 0;

            int allowedTime = getAllowedTime();
            if (allowedTime > mMillisUsed) {
                if (allowedTime < TimeOptions.THREE_HOURS) {
                    mTearCountdownThreshold = new Random().nextInt(1) + 1;
                } else if (allowedTime < TimeOptions.EIGHT_HOURS) {
                    mTearCountdownThreshold = new Random().nextInt(2) + 1;
                } else if (allowedTime < TimeOptions.FIFTEEN_HOURS) {
                    mTearCountdownThreshold = new Random().nextInt(3) + 1;
                } else {
                    mTearCountdownThreshold = new Random().nextInt(4) + 1;
                }
            } else {
                mTearCountdownThreshold = 1;
            }

            final float waterLevel = parentHeight - waveHeight + ((float) (mScaleMaxLimit - mMillisUsed) / mScaleMaxLimit * waveHeight) - 10;

            final ImageView teardropIV = new ImageView(context);
            teardropIV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            teardropIV.setImageResource(R.drawable.tears);
            teardropIV.getLayoutParams().height = 40;
            teardropIV.getLayoutParams().width = 30;
            Random random = new Random();
            teardropIV.setX((eyeX + eyeWidth / 2) + (random.nextInt(61) - 30));
            teardropIV.setY((eyeY + eyeHeight / 2) + (random.nextInt(21) - 10));
            teardropIV.setImageAlpha(255);

            mView.addTearDrop(teardropIV);
            final Handler handler = new Handler();
            final int tearFlowRate = random.nextInt(3) + 2;
            final Runnable positionRunnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(this, 1);
                    teardropIV.setY(teardropIV.getY() + tearFlowRate);
                    if (teardropIV.getY() > waterLevel) {
                        handler.removeCallbacks(this);
                        mView.removeTearDrop(teardropIV);
                    }
                }
            };

            /*final Runnable alphaRunnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(this, 1);
                    teardropIV.setImageAlpha(teardropIV.getImageAlpha() + 13 >= 255 ? 255 : teardropIV.getImageAlpha() + 13);
                    if (teardropIV.getImageAlpha() >= 255) {
                        handler.removeCallbacks(this);
                        handler.post(positionRunnable);
                    }
                }
            };*/
            handler.post(positionRunnable);
        }
        mTearCountdown++;
    }

    private void calculateData(Context context, int allowedTime) {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class)
                .where(Condition.prop(ScreenUsage.dateField).eq(TimeUtil.getDateAsFormattedString(new Date()))).limit("1").list();
        ScreenUsage screenUsage;
        if (screenUsageList.isEmpty()) {
            screenUsage = new ScreenUsage(TimeUtil.getDateAsFormattedString(new Date()), 0, allowedTime);
            screenUsage.save();
        } else {
            screenUsage = screenUsageList.get(0);
        }
        mMillisUsed = screenUsage.getSecondsUsed();
        calculatePercentages(context, allowedTime);
    }

    private void calculatePercentages(Context context, int allowedTime) {
        float allowedPercentage, currentPercentage;
        if (mMillisUsed < allowedTime) {
            allowedPercentage = TimeUtil.getAllowedPercentageFromTimeOption(allowedTime);
            currentPercentage = ((float) mMillisUsed / allowedTime) * allowedPercentage;
        } else {
            int scaleMaxTime = TimeUtil.getScaleMaxTimeFromExceededUsedTime(mMillisUsed);
            allowedPercentage = TimeUtil.getAllowedPercentageFromTimeOption(scaleMaxTime);
            setScale(context, scaleMaxTime);
            currentPercentage = ((float) mMillisUsed / scaleMaxTime) * allowedPercentage;
        }
        mView.setData(currentPercentage, calculateWaterColor(allowedTime),
                TimeUtil.convertMillisToString(mMillisUsed), calculateEyeOverlayOpacity(mMillisUsed, allowedTime));
    }

    private int calculateWaterColor(int allowedTime) {
        int color;
        int startBlueR = 23, startBlueG = 152, startBlueB = 226;
        int endRedR = 178, endRedG = 35, endRedB = 10;
        if (mMillisUsed > allowedTime) {
            color = Color.rgb(endRedR, endRedG, endRedB);
        } else if (mMillisUsed > allowedTime / 2) {
            int colorR = (int) ((((mMillisUsed - (float) (allowedTime / 2)) / ((float) allowedTime / 2)) * (endRedR - startBlueR)) + startBlueR);
            int colorG = (int) ((((mMillisUsed - (float) (allowedTime / 2)) / ((float) allowedTime / 2)) * (endRedG - startBlueG)) + startBlueG);
            int colorB = (int) ((((mMillisUsed - (float) (allowedTime / 2)) / ((float) allowedTime / 2)) * (endRedB - startBlueB)) + startBlueB);
            color = Color.rgb(colorR, colorG, colorB);
        } else {
            color = Color.rgb(startBlueR, startBlueG, startBlueB);
        }
        return color;
    }

    private float calculateEyeOverlayOpacity(long usedTime, int allowedTime) {
        if (usedTime < allowedTime) {
            if (usedTime < allowedTime / 2) {
                return 0.0f;
            } else {
                return (0.6f / (allowedTime / 2)) * (usedTime - (allowedTime / 2));
            }
        } else {
            return 0.6f;
        }
    }

    private void setScale(Context context, int scaleMaxLimit) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
        List<TextView> scaleBars = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            addBar(context, layoutParams, scaleBars, "");
        }
        int timeValue = 1;
        switch (scaleMaxLimit) {
            case FIFTEEN_MINS:
                setMinsLoop(scaleBars, timeValue, 3);
                mScaleMaxLimit = 1080;
                break;
            case THIRTY_MINS:
                setMinsLoop(scaleBars, timeValue, 6);
                mScaleMaxLimit = 2160;
                break;
            case FOURTY_FIVE_MINS:
                setMinsLoop(scaleBars, timeValue, 9);
                mScaleMaxLimit = 3240;
                break;
            case ONE_HOUR:
                setMinsLoop(scaleBars, timeValue, 12);
                mScaleMaxLimit = 4320;
                break;
            case ONE_HALF_HOUR:
                setMinsLoop(scaleBars, timeValue, 18);
                mScaleMaxLimit = 6480;
                break;
            case TWO_HOURS:
                setMinsLoop(scaleBars, timeValue, 24);
                mScaleMaxLimit = 8640;
                break;
            case TWO_HALF_HOURS:
                for (int i = 1; i < scaleBars.size(); i += 2) {
                    TextView textView = scaleBars.get(i);
                    textView.setText(String.format(Locale.getDefault(), timeValue <= 2 ? " - %.1f hour" : " - %.1f hours", 0.5 * timeValue));
                    timeValue++;
                }
                mScaleMaxLimit = THREE_HOURS;
                break;
            case THREE_HOURS:
                setHourLoop(scaleBars, timeValue, 1, true);
                mScaleMaxLimit = FIVE_HOURS;
                break;
            case FOUR_HOURS:
                setHourLoop(scaleBars, timeValue, 1, true);
                mScaleMaxLimit = FIVE_HOURS;
                break;
            case FIVE_HOURS:
                setHourLoop(scaleBars, timeValue, 1, false);
                mScaleMaxLimit = SIX_HOURS;
                break;
            case SIX_HOURS:
                setHourLoop(scaleBars, timeValue, 2, true);
                mScaleMaxLimit = TEN_HOURS;
                break;
            case EIGHT_HOURS:
                setHourLoop(scaleBars, timeValue, 2, true);
                mScaleMaxLimit = TEN_HOURS;
                break;
            case TEN_HOURS:
                setHourLoop(scaleBars, timeValue, 2, false);
                mScaleMaxLimit = TWELVE_HOURS;
                break;
            case TWELVE_HOURS:
                setHourLoop(scaleBars, timeValue, 3, true);
                mScaleMaxLimit = FIFTEEN_HOURS;
                break;
            case FIFTEEN_HOURS:
                setHourLoop(scaleBars, timeValue, 3, false);
                mScaleMaxLimit = EIGHTEEN_HOURS;
                break;
            case EIGHTEEN_HOURS:
                setHourLoop(scaleBars, timeValue, 3, false);
                addBar(context, layoutParams, scaleBars, " - 21 hours");
                mScaleMaxLimit = TWENTY_ONE_HOURS;
                break;
            case TWENTY_ONE_HOURS:
                setHourLoop(scaleBars, timeValue, 3, false);
                addBar(context, layoutParams, scaleBars, " - 21 hours");
                addBar(context, layoutParams, scaleBars, " - 24 hours");
                mScaleMaxLimit = TWENTY_FOUR_HOURS;
                break;
            case TWENTY_FOUR_HOURS:
                setHourLoop(scaleBars, timeValue, 4, false);
                mScaleMaxLimit = TWENTY_FOUR_HOURS;
                break;
        }
        mView.setScale(scaleBars);
    }

    private void setMinsLoop(List<TextView> textViews, int timeValue, int multiplier) {
        for (int i = 1; i < textViews.size(); i += 2) {
            TextView textView = textViews.get(i);
            String specificTime = "";
            switch (multiplier * timeValue) {
                case 60:
                    specificTime = "1 hour";
                    break;
                case 90:
                    specificTime = "1.5 hours";
                    break;
                case 120:
                    specificTime = "2 hours";
                    break;
                case 150:
                    specificTime = "2.5 hours";
                    break;
                case 180:
                    specificTime = "3 hours";
                    break;
            }
            if (specificTime.isEmpty()) {
                textView.setText(String.format(Locale.getDefault(), " - %d mins", multiplier * timeValue));
            } else {
                textView.setText(String.format(" - %s", specificTime));
            }
            timeValue++;
        }
    }

    private void setHourLoop(List<TextView> textViews, int timeValue, int multiplier, boolean removeExtras) {
        for (int i = 1; i < (removeExtras ? textViews.size() - 2 : textViews.size()); i += 2) {
            TextView textView = textViews.get(i);
            textView.setText(String.format(Locale.getDefault(),
                    multiplier == 1 ? (timeValue <= 1 ? " - %d hour" : " - %d hours") : " - %d hours",
                    multiplier * timeValue));
            timeValue++;
        }
        if (removeExtras) {
            textViews.remove(10);
            textViews.remove(10);
        }
    }

    private void addBar(Context context, LayoutParams layoutParams, List<TextView> scaleBars, String text) {
        TextView halfBar = new TextView(context);
        halfBar.setLayoutParams(layoutParams);
        halfBar.setText(" - ");
        scaleBars.add(halfBar);
        TextView textBar = new TextView(context);
        textBar.setLayoutParams(layoutParams);
        textBar.setText(text);
        scaleBars.add(textBar);
    }

    @Override
    public void showAllowedTimeBar(Context context, int waveHeight, int parentHeight) {
        if (mCurrentAllowedTime != getAllowedTime()) {
            mCurrentAllowedTime = getAllowedTime();
            View allowedTimeLineView = new View(context);

            allowedTimeLineView.setId(Constants.TIME_BAR_ID);
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(120, 0, 50, 0);
            allowedTimeLineView.setLayoutParams(params);
            allowedTimeLineView.getLayoutParams().height = 5;

            float allowedY = parentHeight - (((float) waveHeight / mScaleMaxLimit) * mCurrentAllowedTime) + 15;
            allowedTimeLineView.setX(0);
            allowedTimeLineView.setY(allowedY);
            allowedTimeLineView.setBackgroundColor(Color.RED);
            mView.createAllowedTimeBar(allowedTimeLineView);
        }
    }
}
