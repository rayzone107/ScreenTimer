package com.rachitgoyal.screentimer.modules.reminder;

import android.animation.Animator;
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

    @BindView(R.id.reminders_rv)
    RecyclerView mRemindersRV;

    @BindView(R.id.shadow_view)
    View mShadowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        mToolbar.getBackground().setAlpha(0);
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
                mDeleteIV.setVisibility(willExpand ? View.GONE : View.VISIBLE);
                YoYo.with(willExpand ? Techniques.FadeIn : Techniques.FadeOut).duration(800)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                if (willExpand) {
                                    mShadowView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                if (!willExpand) {
                                    mShadowView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        }).playOn(mShadowView);
            }
        });
    }

    @OnClick(R.id.action_delete)
    public void deleteClicked(View view) {
        toggleActionMode(true);
        mIsDeleteModeEnabled = true;
        mAdapter.setDeleteMode(true);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateReminders(List<Reminder> reminderList) {
        mReminderList.clear();
        mReminderList.addAll(reminderList);
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
                if (!isDeleteEnabled) {
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
            mExpansionContent.collapse(true);
        } else {
            super.onBackPressed();
        }
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
