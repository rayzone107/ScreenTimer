package com.rachitgoyal.screentimer.modules.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.pixplicity.easyprefs.library.Prefs;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.modules.tears.TearsActivity;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.ModelUtil;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AhoyOnboarderActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        mContext = getBaseContext();
        if (Prefs.getBoolean(Constants.PREFERENCES.PREFS_SHOW_TUTORIAL, true)) {
            setupCards();
        } else {
            onFinishButtonPressed();
            finish();
        }
    }

    private void setupCards() {

        AhoyOnboarderCard card1 = new AhoyOnboarderCard(getString(R.string.onboarding_card1_title),
                getString(R.string.onboarding_card1_desc), R.drawable.logo_without_background);
        card1.setIconLayoutParams(400, 400, 50, 20, 20, 50);

        AhoyOnboarderCard card2 = new AhoyOnboarderCard(getString(R.string.onboarding_card2_title),
                getString(R.string.onboarding_card2_desc), R.drawable.ic_onboarding_calendar);
        card2.setIconLayoutParams(300, 300, 70, 20, 20, 50);

        AhoyOnboarderCard card3 = new AhoyOnboarderCard(getString(R.string.onboarding_card3_title),
                getString(R.string.onboarding_card3_desc), R.drawable.ic_onboarding_reminders);
        card3.setIconLayoutParams(300, 300, 70, 20, 20, 50);

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(card1);
        pages.add(card2);
        pages.add(card3);

        for (AhoyOnboarderCard card : pages) {
            card.setBackgroundColor(R.color.black_transparent);
            card.setTitleColor(R.color.white);
            card.setDescriptionColor(R.color.white);
            card.setTitleTextSize(ModelUtil.dp2px(mContext, 12));
            card.setDescriptionTextSize(ModelUtil.dp2px(mContext, 8));
        }

        setOnboardPages(pages);
        setGradientBackground();
        setFinishButtonTitle("Finish");
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
    }

    @Override
    public void onFinishButtonPressed() {
        Prefs.putBoolean(Constants.PREFERENCES.PREFS_SHOW_TUTORIAL, false);
        startActivity(new Intent(this, TearsActivity.class));
    }
}
