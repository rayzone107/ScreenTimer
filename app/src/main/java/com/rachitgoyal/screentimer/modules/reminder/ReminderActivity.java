package com.rachitgoyal.screentimer.modules.reminder;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.libraries.expansion_layout.ExpansionHeader;
import com.rachitgoyal.screentimer.libraries.expansion_layout.ExpansionLayout;
import com.rachitgoyal.screentimer.model.Reminder;
import com.rachitgoyal.screentimer.modules.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReminderActivity extends BaseActivity implements ReminderContract.View, RemindersListAdapter.ReminderActivityCallback {

    private List<String> mFrequencyList = new ArrayList<>();
    private List<String> mTimeList = new ArrayList<>();
    private List<Reminder> mReminderList = new ArrayList<>();
    private boolean mIsDeleteModeEnabled = false;

    private ReminderPresenter mPresenter;
    private RemindersListAdapter mAdapter;
    private ActionModeCallback mActionModeCallback;
    private ActionMode mActionMode;
    private boolean mIsRecurringEnabled;

    @BindView(R.id.parent_cl)
    CoordinatorLayout mParentCL;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.action_delete)
    ImageView mDeleteIV;

    @BindView(R.id.expandable_header)
    ExpansionHeader mExpansionHeader;

    @BindView(R.id.expandable_content)
    ExpansionLayout mExpansionContent;

    @BindView(R.id.time_picker)
    TimePicker mTimePicker;

    @BindView(R.id.recurring_iv)
    ImageView mRecurringIV;

    @BindView(R.id.reminders_rv)
    RecyclerView mRemindersRV;

    @BindView(R.id.no_reminder_rl)
    RelativeLayout mNoReminderRL;

    @BindView(R.id.shadow_view)
    View mShadowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        ButterKnife.bind(this);

        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle(R.string.reminders);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRemindersRV.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new RemindersListAdapter(this, mReminderList, mIsDeleteModeEnabled);
        mRemindersRV.setAdapter(mAdapter);
        mActionModeCallback = new ActionModeCallback();
        mTimePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(3);
            mTimePicker.setMinute(0);
        } else {
            mTimePicker.setCurrentHour(3);
            mTimePicker.setCurrentMinute(0);
        }

        mPresenter = new ReminderPresenter(this, mFrequencyList, mTimeList, mReminderList);
        mPresenter.fetchReminders();

        mExpansionContent.addIndicatorListener(new ExpansionLayout.IndicatorListener() {
            @Override
            public void onStartedExpand(ExpansionLayout expansionLayout, final boolean willExpand) {
                if (!mReminderList.isEmpty()) {
                    mDeleteIV.setVisibility(willExpand ? View.GONE : View.VISIBLE);
                } else if (mReminderList.isEmpty()) {
                    YoYo.with(willExpand ? Techniques.FadeOut : Techniques.FadeIn).duration(300).playOn(mNoReminderRL);
                }
                YoYo.with(willExpand ? Techniques.FadeIn : Techniques.FadeOut).duration(800)
                        .onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                if (willExpand) {
                                    mShadowView.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                if (!willExpand) {
                                    mShadowView.setVisibility(View.GONE);
                                }
                            }
                        }).playOn(mShadowView);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mExpansionContent.isExpanded()) {
            mExpansionContent.collapse(false);
        }
        mShadowView.setVisibility(View.GONE);
    }

    @OnClick(R.id.action_delete)
    public void deleteClicked(View view) {
        toggleActionMode(true);
        mIsDeleteModeEnabled = true;
        mAdapter.setDeleteMode(true);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.add_button)
    public void addClicked(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPresenter.addReminder(mTimePicker.getHour(), mTimePicker.getMinute(), mIsRecurringEnabled);
        } else {
            mPresenter.addReminder(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute(), mIsRecurringEnabled);
        }
    }

    @OnClick(R.id.recurring_iv)
    public void recurringClicked(View view) {
        mIsRecurringEnabled = !mIsRecurringEnabled;
        YoYo.with(Techniques.RotateIn)
                .duration(500)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        mRecurringIV.setImageResource(mIsRecurringEnabled ? R.drawable.recurring_enabled : R.drawable.recurring_disabled);
                    }
                }).playOn(findViewById(R.id.recurring_iv));
    }

    @Override
    public void updateReminders(List<Reminder> reminderList) {
        mReminderList.clear();
        mReminderList.addAll(reminderList);
        if (mReminderList.isEmpty() && mNoReminderRL.getVisibility() == View.GONE) {
            YoYo.with(Techniques.SlideInUp).duration(400).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    mNoReminderRL.setVisibility(View.VISIBLE);
                }
            }).playOn(mNoReminderRL);
        } else if (!mReminderList.isEmpty()) {
            mNoReminderRL.setVisibility(View.GONE);
        }
        mDeleteIV.setVisibility(mReminderList.isEmpty() ? View.GONE : View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMessageSnackbar(String message) {
        Snackbar.make(mParentCL, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void toggleDeleteState(boolean isEnabled) {
        MenuItem deleteItem = mActionMode.getMenu().findItem(R.id.action_delete);
        deleteItem.setVisible(isEnabled);
    }

    @Override
    public void toggleAddState(boolean isExpanded) {
        if (isExpanded) {
            mExpansionContent.expand(true);
        } else {
            mExpansionContent.collapse(true);
        }

    }

    @Override
    public void scrollToEnd() {
        mRemindersRV.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void toggleActionMode(boolean isEnabled) {
        if (isEnabled) {
            if (mActionMode == null) {
                boolean isDeleteEnabled = false;
                for (Reminder reminder : mReminderList) {
                    if (reminder.isDeleteChecked()) {
                        isDeleteEnabled = true;
                        break;
                    }
                }
                mActionMode = startSupportActionMode(mActionModeCallback);
                if (mActionMode != null && !isDeleteEnabled) {
                    mActionMode.setTitle("Select");
                    mActionMode.setTitleOptionalHint(true);
                    mActionMode.getMenu().findItem(R.id.action_delete).setVisible(false);
                }
            }
            mExpansionHeader.setToggleOnClick(false);
        } else {
            if (mActionMode != null) {
                mActionMode.finish();
                mActionMode = null;
            }
            mExpansionHeader.setToggleOnClick(true);
        }
    }

    @Override
    public void switchToggle(Reminder reminder, boolean isEnabled) {
        mPresenter.updateReminder(reminder, isEnabled);
    }

    @Override
    public void switchCheckbox(Reminder reminder, boolean isChecked) {
        mPresenter.checkboxToggled(reminder, isChecked);
    }

    @Override
    public void deleteLongClick(int position) {
        mPresenter.setDeleteChecked(position);
        mDeleteIV.performClick();
    }

    @Override
    public void onBackPressed() {
        if (mActionMode != null) {
            mPresenter.clearAllDeletes();
            toggleActionMode(false);
            mAdapter.setDeleteMode(false);
            mAdapter.notifyDataSetChanged();
        } else if (mExpansionContent.isExpanded()) {
            toggleAddState(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_reminders_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    mIsDeleteModeEnabled = false;
                    mAdapter.setDeleteMode(false);
                    mPresenter.deleteClicked();
                    toggleActionMode(false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            toggleActionMode(false);
            mAdapter.setDeleteMode(false);
            mPresenter.clearAllDeletes();
        }
    }
}
