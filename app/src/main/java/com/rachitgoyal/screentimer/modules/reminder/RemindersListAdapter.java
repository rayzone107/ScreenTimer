package com.rachitgoyal.screentimer.modules.reminder;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.Reminder;
import com.rachitgoyal.screentimer.util.ModelUtil;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.List;

/**
 * Created by Rachit Goyal on 20/03/18
 */

public class RemindersListAdapter extends RecyclerView.Adapter {

    private ReminderActivityCallback mListener;
    private List<Reminder> mReminders;
    private boolean mIsDeleteModeEnabled;

    RemindersListAdapter(ReminderActivityCallback listener, List<Reminder> reminderList, boolean isDeleteModeEnabled) {
        mListener = listener;
        mReminders = reminderList;
        mIsDeleteModeEnabled = isDeleteModeEnabled;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RemindersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RemindersViewHolder) holder).bindData(mReminders.get(position), mIsDeleteModeEnabled, position);
    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_reminder_list;
    }


    void setDeleteMode(boolean isDeleteModeEnabled) {
        mIsDeleteModeEnabled = isDeleteModeEnabled;
    }

    public class RemindersViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private RelativeLayout mParentLayout;
        private CheckBox mCheckBox;
        private CardView mCardView;
        private TextView mFrequencyTV;
        private TextView mTimeTV;
        private TextView mTimeUnitTV;
        private Switch mEnabledSwitch;
        private Reminder mReminder = new Reminder();
        private boolean isInBind;

        RemindersViewHolder(final View itemView) {
            super(itemView);
            mParentLayout = itemView.findViewById(R.id.parent_rl);
            mCheckBox = itemView.findViewById(R.id.checkbox);
            mCardView = itemView.findViewById(R.id.card_view);
            mFrequencyTV = itemView.findViewById(R.id.frequency_tv);
            mTimeTV = itemView.findViewById(R.id.time_tv);
            mTimeUnitTV = itemView.findViewById(R.id.time_unit_tv);
            mEnabledSwitch = itemView.findViewById(R.id.enabled_switch);
            mCheckBox.setOnCheckedChangeListener(this);
            mEnabledSwitch.setOnCheckedChangeListener(this);
        }

        void bindData(Reminder reminder, final boolean isDeleteModeEnabled, final int position) {
            isInBind = true;
            mReminder = reminder;
            Context context = mCardView.getContext();
            mParentLayout.setBackgroundColor(ContextCompat.getColor(context, reminder.isDeleteChecked() ? R.color.light_gray : R.color.transparent));
            mCheckBox.setChecked(reminder.isDeleteChecked());
            mCardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, reminder.isEnabled() ? R.color.reminder_enabled : R.color.reminder_disabled));
            mFrequencyTV.setText(reminder.isRecurring() ? "Every" : "At");
            String time = TimeUtil.convertSecondsToHourMins(reminder.getSeconds()) + " hours";
            String[] timeSplit = time.split(" ");
            mTimeTV.setText(timeSplit[0]);
            mTimeUnitTV.setText(timeSplit[1]);
            mEnabledSwitch.setChecked(reminder.isEnabled());
            if (!reminder.isEnabled()) {
                int disabledColor = ContextCompat.getColor(context, R.color.disabled_text);
                mFrequencyTV.setTextColor(disabledColor);
                mTimeTV.setTextColor(disabledColor);
                mTimeUnitTV.setTextColor(disabledColor);
                mEnabledSwitch.setHighlightColor(disabledColor);
            } else {
                int enabledColor = Color.BLACK;
                mFrequencyTV.setTextColor(enabledColor);
                mTimeTV.setTextColor(enabledColor);
                mTimeUnitTV.setTextColor(enabledColor);
                mEnabledSwitch.setHighlightColor(enabledColor);
            }
            if ((mCheckBox.getVisibility() == View.GONE && isDeleteModeEnabled) ||
                    (mCheckBox.getVisibility() == View.VISIBLE && !isDeleteModeEnabled)) {
                animateDeleteMode(mParentLayout.getContext(), isDeleteModeEnabled);
            }

            mParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isDeleteModeEnabled) {
                        mCheckBox.performClick();
                    } else {
                        mEnabledSwitch.performClick();
                    }
                }
            });
            mParentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!isDeleteModeEnabled) {
                        mListener.deleteLongClick(position);
                    }
                    return false;
                }
            });
            isInBind = false;
        }

        private void animateDeleteMode(Context context, final boolean isEnabled) {
            int dp = ModelUtil.dp2px(context, 40);
            int startPosition = isEnabled ? 0 : dp;
            int endPosition = isEnabled ? dp : 0;
            int animationTime = 600;
            Skill skill = Skill.Linear;
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    Glider.glide(skill, animationTime, ObjectAnimator.ofFloat(mCheckBox, "translationX", startPosition, endPosition)),
                    Glider.glide(skill, animationTime, ObjectAnimator.ofFloat(mCardView, "translationX", startPosition, endPosition)),
                    Glider.glide(skill, animationTime, ObjectAnimator.ofFloat(mEnabledSwitch, "translationX", startPosition, endPosition))
            );
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    if (isEnabled) {
                        mCheckBox.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (!isEnabled) {
                        mCheckBox.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            set.setDuration(animationTime);
            set.start();
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (!isInBind) {
                switch (compoundButton.getId()) {
                    case R.id.checkbox:
                        mListener.switchCheckbox(mReminder, checked);
                        mParentLayout.setBackgroundColor(ContextCompat.getColor(mParentLayout.getContext(),
                                checked ? R.color.light_gray : R.color.transparent));
                        break;
                    case R.id.enabled_switch:
                        mListener.switchToggle(mReminder, checked);
                        notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    public interface ReminderActivityCallback {
        void switchToggle(Reminder reminder, boolean isChecked);

        void switchCheckbox(Reminder reminder, boolean isChecked);

        void deleteLongClick(int position);
    }
}
