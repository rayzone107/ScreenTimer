package com.rachitgoyal.screentimer.libraries.duration_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.rachitgoyal.screentimer.R;

/**
 * Created by Rachit Goyal on 25/04/18
 */

public class DurationView extends View {

    private final String DEFAULT_DAYS_LABEL = "DAYS";
    private final String DEFAULT_HOURS_LABEL = "HOURS";
    private final String DEFAULT_MINS_LABEL = "MINUTES";
    private final String DEFAULT_SECS_LABEL = "SECONDS";
    private final float DEFAULT_NUMBER_TEXT_SIZE = 18.0f;
    private final float DEFAULT_LABEL_TEXT_SIZE = 12.0f;
    private final int DEFAULT_LABEL_POSITION = LabelPosition.BOTTOM.ordinal();
    private final float DEFAULT_NUMBER_HORIZONTAL_PADDING = 10.0f;
    private final float DEFAULT_LABEL_VERTICAL_PADDING = 6.0f;
    private final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private final boolean DEFAULT_SHOW_ITEM = true;
    private final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;
    private final boolean DEFAULT_SHOW_DIVIERS = false;
    private final float DEFAULT_DIVIDER_WIDTH = 2.0f;
    private final float DEFAULT_DIVIDER_TOP_BOTTOM_MARGIN = 4.0f;

    public enum Label {
        DAYS,
        HOURS,
        MINS,
        SECS
    }

    public enum LabelPosition {
        TOP,
        BOTTOM
    }

    private Context mContext;
    private int mCanvasHeight;
    private int mCanvasWidth;
    private Paint mDaysPaint;
    private Paint mDaysLabelPaint;
    private Paint mHoursPaint;
    private Paint mHoursLabelPaint;
    private Paint mMinsPaint;
    private Paint mMinsLabelPaint;
    private Paint mSecsPaint;
    private Paint mSecsLabelPaint;


    private int mDays;
    private int mHours;
    private int mMins;
    private int mSecs;
    private String mDaysLabel = DEFAULT_DAYS_LABEL;
    private String mHoursLabel = DEFAULT_HOURS_LABEL;
    private String mMinsLabel = DEFAULT_MINS_LABEL;
    private String mSecsLabel = DEFAULT_SECS_LABEL;
    private float mNumberTextSize = sp2px(DEFAULT_NUMBER_TEXT_SIZE);
    private float mLabelTextSize = sp2px(DEFAULT_LABEL_TEXT_SIZE);
    private int mLabelPosition = DEFAULT_LABEL_POSITION;
    private float mNumberHorizontalPadding = dp2px(DEFAULT_NUMBER_HORIZONTAL_PADDING);
    private float mLabelVerticalPadding = dp2px(DEFAULT_LABEL_VERTICAL_PADDING);
    private int mDaysColor = DEFAULT_TEXT_COLOR;
    private int mHoursColor = DEFAULT_TEXT_COLOR;
    private int mMinsColor = DEFAULT_TEXT_COLOR;
    private int mSecsColor = DEFAULT_TEXT_COLOR;
    private int mDaysLabelColor = DEFAULT_TEXT_COLOR;
    private int mHoursLabelColor = DEFAULT_TEXT_COLOR;
    private int mMinsLabelColor = DEFAULT_TEXT_COLOR;
    private int mSecsLabelColor = DEFAULT_TEXT_COLOR;
    private int mNumberColor = DEFAULT_TEXT_COLOR;
    private int mLabelsColor = DEFAULT_TEXT_COLOR;
    private int mTextColor = DEFAULT_TEXT_COLOR;

    private boolean mShowDays = DEFAULT_SHOW_ITEM;
    private boolean mShowHours = DEFAULT_SHOW_ITEM;
    private boolean mShowMins = DEFAULT_SHOW_ITEM;
    private boolean mShowSecs = DEFAULT_SHOW_ITEM;
    private boolean mShowLabels = DEFAULT_SHOW_ITEM;

    private int mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    private Drawable mBackgroundResource;

    private boolean mShowDividers = DEFAULT_SHOW_DIVIERS;
    private float mDividersWidth = dp2px(DEFAULT_DIVIDER_WIDTH);
    private int mDividersColor = DEFAULT_TEXT_COLOR;
    private float mDividersMargin = dp2px(DEFAULT_DIVIDER_TOP_BOTTOM_MARGIN);

    // Constructor & Init Method.
    public DurationView(final Context context) {
        this(context, null);
    }

    public DurationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DurationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.DurationView, defStyleAttr, 0);

        mDays = attributes.getInteger(R.styleable.DurationView_dv_days, 0);
        mHours = attributes.getInteger(R.styleable.DurationView_dv_hours, 0);
        mMins = attributes.getInteger(R.styleable.DurationView_dv_mins, 0);
        mSecs = attributes.getInteger(R.styleable.DurationView_dv_secs, 0);

        mDaysLabel = attributes.getString(R.styleable.DurationView_dv_days_label);
        mHoursLabel = attributes.getString(R.styleable.DurationView_dv_hours_label);
        mMinsLabel = attributes.getString(R.styleable.DurationView_dv_mins_label);
        mSecsLabel = attributes.getString(R.styleable.DurationView_dv_secs_label);

        mNumberTextSize = attributes.getDimension(R.styleable.DurationView_dv_number_text_size, DEFAULT_NUMBER_TEXT_SIZE);
        mLabelTextSize = attributes.getDimension(R.styleable.DurationView_dv_label_text_size, DEFAULT_LABEL_TEXT_SIZE);
        mLabelPosition = attributes.getInteger(R.styleable.DurationView_dv_label_position, DEFAULT_LABEL_POSITION);

        mNumberHorizontalPadding = attributes.getDimension(R.styleable.DurationView_dv_number_horizontal_padding, DEFAULT_NUMBER_HORIZONTAL_PADDING);
        mLabelVerticalPadding = attributes.getDimension(R.styleable.DurationView_dv_label_vertical_padding, DEFAULT_LABEL_VERTICAL_PADDING);

        mDaysColor = attributes.getColor(R.styleable.DurationView_dv_days_color, DEFAULT_TEXT_COLOR);
        mHoursColor = attributes.getColor(R.styleable.DurationView_dv_hours_color, DEFAULT_TEXT_COLOR);
        mMinsColor = attributes.getColor(R.styleable.DurationView_dv_mins_color, DEFAULT_TEXT_COLOR);
        mSecsColor = attributes.getColor(R.styleable.DurationView_dv_secs_color, DEFAULT_TEXT_COLOR);
        mDaysLabelColor = attributes.getColor(R.styleable.DurationView_dv_days_label_color, DEFAULT_TEXT_COLOR);
        mHoursLabelColor = attributes.getColor(R.styleable.DurationView_dv_hours_label_color, DEFAULT_TEXT_COLOR);
        mMinsLabelColor = attributes.getColor(R.styleable.DurationView_dv_mins_label_color, DEFAULT_TEXT_COLOR);
        mSecsLabelColor = attributes.getColor(R.styleable.DurationView_dv_secs_label_color, DEFAULT_TEXT_COLOR);
        mNumberColor = attributes.getColor(R.styleable.DurationView_dv_number_color, DEFAULT_TEXT_COLOR);
        mLabelsColor = attributes.getColor(R.styleable.DurationView_dv_label_color, DEFAULT_TEXT_COLOR);
        mTextColor = attributes.getColor(R.styleable.DurationView_dv_text_color, DEFAULT_TEXT_COLOR);

        mShowDays = attributes.getBoolean(R.styleable.DurationView_dv_show_days, DEFAULT_SHOW_ITEM);
        mShowHours = attributes.getBoolean(R.styleable.DurationView_dv_show_hours, DEFAULT_SHOW_ITEM);
        mShowMins = attributes.getBoolean(R.styleable.DurationView_dv_show_mins, DEFAULT_SHOW_ITEM);
        mShowSecs = attributes.getBoolean(R.styleable.DurationView_dv_show_secs, DEFAULT_SHOW_ITEM);
        mShowLabels = attributes.getBoolean(R.styleable.DurationView_dv_show_labels, DEFAULT_SHOW_ITEM);

        mBackgroundColor = attributes.getColor(R.styleable.DurationView_dv_background_color, DEFAULT_BACKGROUND_COLOR);
        mBackgroundResource = attributes.getDrawable(R.styleable.DurationView_dv_background_resource);

        mShowDividers = attributes.getBoolean(R.styleable.DurationView_dv_show_dividers, DEFAULT_SHOW_DIVIERS);
        mDividersWidth = attributes.getDimension(R.styleable.DurationView_dv_divider_width, DEFAULT_DIVIDER_WIDTH);
        mDividersColor = attributes.getColor(R.styleable.DurationView_dv_divider_color, DEFAULT_TEXT_COLOR);
        mDividersMargin = attributes.getDimension(R.styleable.DurationView_dv_divider_top_bottom_margin, DEFAULT_DIVIDER_TOP_BOTTOM_MARGIN);

        generatePaints();
        attributes.recycle();
    }

    private void generatePaints() {
        mDaysPaint = setPaintForValue(Label.DAYS, true);
        mHoursPaint = setPaintForValue(Label.HOURS, true);
        mMinsPaint = setPaintForValue(Label.MINS, true);
        mSecsPaint = setPaintForValue(Label.SECS, true);
        mDaysLabelPaint = setPaintForValue(Label.DAYS, false);
        mHoursLabelPaint = setPaintForValue(Label.HOURS, false);
        mMinsLabelPaint = setPaintForValue(Label.MINS, false);
        mSecsLabelPaint = setPaintForValue(Label.SECS, false);
    }

    private Paint setPaintForValue(Label label, boolean isNumber) {
        Paint paint = new Paint();
        paint.setColor(mTextColor);
        if ((isNumber && mNumberColor != DEFAULT_TEXT_COLOR && mNumberColor != mTextColor) ||
                (!isNumber && mLabelsColor != DEFAULT_TEXT_COLOR && mLabelsColor != mTextColor)) {
            paint.setColor(isNumber ? mNumberColor : mLabelsColor);
        }
        switch (label) {
            case DAYS:
                if ((isNumber && mDaysColor != DEFAULT_TEXT_COLOR) ||
                        (!isNumber && mDaysLabelColor != DEFAULT_TEXT_COLOR)) {
                    paint.setColor(isNumber ? mDaysColor : mDaysLabelColor);
                }
                break;
            case HOURS:
                if ((isNumber && mHoursColor != DEFAULT_TEXT_COLOR) ||
                        (!isNumber && mHoursLabelColor != DEFAULT_TEXT_COLOR)) {
                    paint.setColor(isNumber ? mHoursColor : mHoursLabelColor);
                }
                break;
            case MINS:
                if ((isNumber && mMinsColor != DEFAULT_TEXT_COLOR) ||
                        (!isNumber && mMinsLabelColor != DEFAULT_TEXT_COLOR)) {
                    paint.setColor(isNumber ? mMinsColor : mMinsLabelColor);
                }
                break;
            case SECS:
                if ((isNumber && mSecsColor != DEFAULT_TEXT_COLOR) ||
                        (!isNumber && mSecsLabelColor != DEFAULT_TEXT_COLOR)) {
                    paint.setColor(isNumber ? mSecsColor : mSecsLabelColor);
                }
                break;
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(isNumber ? mNumberTextSize : mLabelTextSize);
        return paint;
    }

    @Override
    public void onDraw(Canvas canvas) {
        mCanvasWidth = canvas.getWidth();
        mCanvasHeight = canvas.getHeight();

        int sectionsShown = 0;
        boolean[] showBooleans = new boolean[]{mShowDays, mShowHours, mShowMins, mShowSecs};
        for (boolean b : showBooleans) {
            sectionsShown += b ? 1 : 0;
        }
        if (sectionsShown != 0) {
            int xAreaPerSection = mCanvasWidth / sectionsShown;

            if (mShowLabels) {
                switch (mLabelPosition) {
                    case 0:
                        drawNumbers(canvas, xAreaPerSection);
                        drawLabels(canvas);
                        break;
                    case 1:
                        break;
                }
            }
        }
    }

    private void drawNumbers(Canvas canvas, int xAreaPerSection) {
        int sectionsDone = 0;
        if (mShowDays) {
            canvas.drawText(mDaysLabel, 0, 0, mDaysLabelPaint);
            canvas.drawText(String.valueOf(mDays), 0, dp2px(mNumberTextSize), mDaysPaint);
            sectionsDone++;
        }
        if (mShowHours) {
            canvas.drawText(mHoursLabel, xAreaPerSection * sectionsDone, 0, mHoursLabelPaint);
            canvas.drawText(String.valueOf(mHours), 0, dp2px(mNumberTextSize), mHoursPaint);
            sectionsDone++;
        }
        if (mShowMins) {
            canvas.drawText(mMinsLabel, xAreaPerSection * sectionsDone, 0, mMinsLabelPaint);
            canvas.drawText(String.valueOf(mMins), 0, dp2px(mNumberTextSize), mMinsPaint);
            sectionsDone++;
        }
        if (mShowSecs) {
            canvas.drawText(mSecsLabel, xAreaPerSection * sectionsDone, 0, mSecsLabelPaint);
            canvas.drawText(String.valueOf(mSecs), 0, dp2px(mNumberTextSize), mSecsPaint);
        }
    }

    private void drawLabels(Canvas canvas) {

    }

    /**
     * Paint.setTextSize(float textSize) default unit is px.
     *
     * @param spValue The real size of text
     * @return int - A transplanted sp
     */
    private int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int getDays() {
        return mDays;
    }

    public void setDays(int days) {
        mDays = days;
    }

    public int getHours() {
        return mHours;
    }

    public void setHours(int hours) {
        mHours = hours;
    }

    public int getMins() {
        return mMins;
    }

    public void setMins(int mins) {
        mMins = mins;
    }

    public int getSecs() {
        return mSecs;
    }

    public void setSecs(int secs) {
        mSecs = secs;
    }

    public String getDaysLabel() {
        return mDaysLabel;
    }

    public void setDaysLabel(String daysLabel) {
        mDaysLabel = daysLabel;
    }

    public String getHoursLabel() {
        return mHoursLabel;
    }

    public void setHoursLabel(String hoursLabel) {
        mHoursLabel = hoursLabel;
    }

    public String getMinsLabel() {
        return mMinsLabel;
    }

    public void setMinsLabel(String minsLabel) {
        mMinsLabel = minsLabel;
    }

    public String getSecsLabel() {
        return mSecsLabel;
    }

    public void setSecsLabel(String secsLabel) {
        mSecsLabel = secsLabel;
    }

    public float getNumberTextSize() {
        return mNumberTextSize;
    }

    public void setNumberTextSize(float numberTextSize) {
        mNumberTextSize = numberTextSize;
    }

    public float getLabelTextSize() {
        return mLabelTextSize;
    }

    public void setLabelTextSize(float labelTextSize) {
        mLabelTextSize = labelTextSize;
    }

    public int getLabelPosition() {
        return mLabelPosition;
    }

    public void setLabelPosition(int labelPosition) {
        mLabelPosition = labelPosition;
    }

    public float getNumberHorizontalPadding() {
        return mNumberHorizontalPadding;
    }

    public void setNumberHorizontalPadding(float numberHorizontalPadding) {
        mNumberHorizontalPadding = numberHorizontalPadding;
    }

    public float getLabelVerticalPadding() {
        return mLabelVerticalPadding;
    }

    public void setLabelVerticalPadding(float labelVerticalPadding) {
        mLabelVerticalPadding = labelVerticalPadding;
    }

    public int getDaysColor() {
        return mDaysColor;
    }

    public void setDaysColor(int daysColor) {
        mDaysColor = daysColor;
    }

    public int getHoursColor() {
        return mHoursColor;
    }

    public void setHoursColor(int hoursColor) {
        mHoursColor = hoursColor;
    }

    public int getMinsColor() {
        return mMinsColor;
    }

    public void setMinsColor(int minsColor) {
        mMinsColor = minsColor;
    }

    public int getSecsColor() {
        return mSecsColor;
    }

    public void setSecsColor(int secsColor) {
        mSecsColor = secsColor;
    }

    public int getDaysLabelColor() {
        return mDaysLabelColor;
    }

    public void setDaysLabelColor(int daysLabelColor) {
        mDaysLabelColor = daysLabelColor;
    }

    public int getHoursLabelColor() {
        return mHoursLabelColor;
    }

    public void setHoursLabelColor(int hoursLabelColor) {
        mHoursLabelColor = hoursLabelColor;
    }

    public int getMinsLabelColor() {
        return mMinsLabelColor;
    }

    public void setMinsLabelColor(int minsLabelColor) {
        mMinsLabelColor = minsLabelColor;
    }

    public int getSecsLabelColor() {
        return mSecsLabelColor;
    }

    public void setSecsLabelColor(int secsLabelColor) {
        mSecsLabelColor = secsLabelColor;
    }

    public int getNumberColor() {
        return mNumberColor;
    }

    public void setNumberColor(int numberColor) {
        mNumberColor = numberColor;
    }

    public int getLabelsColor() {
        return mLabelsColor;
    }

    public void setLabelsColor(int labelsColor) {
        mLabelsColor = labelsColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public boolean isShowDays() {
        return mShowDays;
    }

    public void setShowDays(boolean showDays) {
        mShowDays = showDays;
    }

    public boolean isShowHours() {
        return mShowHours;
    }

    public void setShowHours(boolean showHours) {
        mShowHours = showHours;
    }

    public boolean isShowMins() {
        return mShowMins;
    }

    public void setShowMins(boolean showMins) {
        mShowMins = showMins;
    }

    public boolean isShowSecs() {
        return mShowSecs;
    }

    public void setShowSecs(boolean showSecs) {
        mShowSecs = showSecs;
    }

    public boolean isShowLabels() {
        return mShowLabels;
    }

    public void setShowLabels(boolean showLabels) {
        mShowLabels = showLabels;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public Drawable getBackgroundResource() {
        return mBackgroundResource;
    }

    public void setBackgroundResource(Drawable backgroundResource) {
        mBackgroundResource = backgroundResource;
    }

    public boolean isShowDividers() {
        return mShowDividers;
    }

    public void setShowDividers(boolean showDividers) {
        mShowDividers = showDividers;
    }

    public float getDividersWidth() {
        return mDividersWidth;
    }

    public void setDividersWidth(float dividersWidth) {
        mDividersWidth = dividersWidth;
    }

    public int getDividersColor() {
        return mDividersColor;
    }

    public void setDividersColor(int dividersColor) {
        mDividersColor = dividersColor;
    }

    public float getDividersMargin() {
        return mDividersMargin;
    }

    public void setDividersMargin(float dividersMargin) {
        mDividersMargin = dividersMargin;
    }
}
