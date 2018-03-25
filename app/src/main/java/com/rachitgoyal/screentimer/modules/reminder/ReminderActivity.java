package com.rachitgoyal.screentimer.modules.reminder;

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

import com.rachitgoyal.screentimer.R;
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

    /*@BindView(R.id.frequency_spinner)
    Spinner mFrequencySpinner;

    @BindView(R.id.time_spinner)
    Spinner mTimeSpinner;

    @BindView(R.id.add_iv)
    ImageView mAddIV;*/

    @BindView(R.id.time_picker)
    TimePicker mTimePicker;

    @BindView(R.id.reminders_rv)
    RecyclerView mRemindersRV;

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

        /*mFrequencyList = Arrays.asList(mContext.getResources().getStringArray(R.array.reminder_frequency_list));
        ReminderSpinnerAdapter frequencySpinnerAdapter = new ReminderSpinnerAdapter(mContext, mFrequencyList);
        mFrequencySpinner.setAdapter(frequencySpinnerAdapter);
        mFrequencySpinner.setSelection(0);
        mTimeList = Arrays.asList(mContext.getResources().getStringArray(R.array.pref_usage_threshold_titles));
        ReminderSpinnerAdapter timeSpinnerAdapter = new ReminderSpinnerAdapter(mContext, mTimeList);
        mTimeSpinner.setAdapter(timeSpinnerAdapter);
        mTimeSpinner.setSelection(7);*/

        mTimePicker.setIs24HourView(true);

        mPresenter = new ReminderPresenter(this, mFrequencyList, mTimeList, mReminderList);
        mPresenter.fetchReminders();
    }

    /*@OnClick(R.id.add_iv)
    public void addReminder(View view) {
        mPresenter.addReminder(mTimeSpinner.getSelectedItemPosition(), mFrequencySpinner.getSelectedItemPosition());
    }*/

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
        } else {
            if (mActionMode != null) {
                mActionMode.finish();
                mActionMode = null;
            }
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
        if (mActionMode == null) {
            super.onBackPressed();
        } else {
            mPresenter.clearAllDeletes();
            toggleActionMode(false);
            mAdapter.setDeleteMode(false);
            mAdapter.notifyDataSetChanged();
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
