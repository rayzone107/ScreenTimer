package com.rachitgoyal.screentimer.modules.reminder;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.rachitgoyal.screentimer.model.Reminder;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.List;

/**
 * Created by Rachit Goyal on 19/03/18
 */

public class ReminderPresenter implements ReminderContract.Presenter {

    private ReminderContract.View mView;
    private List<String> mFrequencyList;
    private List<String> mTimeList;
    private List<Reminder> mReminderList;

    ReminderPresenter(ReminderContract.View view, List<String> frequencyList, List<String> timeList,
                      List<Reminder> reminderList) {
        mView = view;
        mFrequencyList = frequencyList;
        mTimeList = timeList;
        mReminderList = reminderList;
    }

    @Override
    public void fetchReminders() {
        mReminderList = Select.from(Reminder.class).list();
        mView.updateReminders(mReminderList);
    }

    @Override
    public void addReminder(int timePosition, int frequencyPosition) {
        int reminderTime = TimeUtil.getMillsFromTimeOption(mTimeList.get(timePosition));
        switch (mFrequencyList.get(frequencyPosition)) {
            case "Every":
                addReminder(reminderTime, true);
                break;
            case "At":
                addReminder(reminderTime, false);
                break;
        }
    }

    @Override
    public void updateReminder(Reminder reminder, boolean isEnabled) {
        reminder.setEnabled(isEnabled);
        reminder.save();
        fetchReminders();
    }

    @Override
    public void deleteClicked() {
        List<Reminder> reminderList = Select.from(Reminder.class)
                .where(Condition.prop(Reminder.isDeleteCheckedField).eq("1")).list();
        if (!reminderList.isEmpty()) {
            Reminder.deleteAll(Reminder.class, Reminder.isDeleteCheckedField + "=?", "1");
        }
        mReminderList = Select.from(Reminder.class).list();
        mView.updateReminders(mReminderList);
    }

    @Override
    public void checkboxToggled(Reminder reminder, boolean isChecked) {
        reminder.setDeleteChecked(isChecked);
        reminder.save();
        List<Reminder> reminderList = Select.from(Reminder.class)
                .where(Condition.prop(Reminder.isDeleteCheckedField).eq("1")).list();
        mView.toggleDeleteState(!reminderList.isEmpty());
    }

    @Override
    public void setDeleteChecked(int position) {
        Reminder reminder = mReminderList.get(position);
        reminder.setDeleteChecked(true);
        reminder.save();
        fetchReminders();
    }

    @Override
    public void clearAllDeletes() {
        Reminder.executeQuery("Update REMINDER set IS_DELETE_CHECKED = 0");
        fetchReminders();
    }

    private void addReminder(int reminderTime, boolean isRecurring) {
        boolean alreadyExists = false;
        List<Reminder> reminderList = Select.from(Reminder.class).list();
        for (Reminder reminder : reminderList) {
            if (reminderTime == reminder.getMillis() && isRecurring == reminder.isRecurring()) {
                String message = "A ".concat(isRecurring ? "recurring " : "")
                        .concat("reminder at ").concat(TimeUtil.getTimeOptionFromMillis(reminderTime))
                        .concat(" already exists.");
                alreadyExists = true;
                mView.showMessageSnackbar(message);
            }
        }
        if (!alreadyExists) {
            Reminder reminder = new Reminder(reminderTime, isRecurring);
            reminder.save();
            mReminderList.add(reminder);
            mView.updateReminders(mReminderList);
        }
    }
}
