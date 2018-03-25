package com.rachitgoyal.screentimer.modules.reminder;

import com.rachitgoyal.screentimer.model.Reminder;

import java.util.List;

/**
 * Created by Rachit Goyal on 19/03/18
 */

public interface ReminderContract {
    interface View {
        void updateReminders(List<Reminder> reminderList);

        void showMessageSnackbar(String message);

        void toggleDeleteState(boolean isEnabled);
    }

    interface Presenter {
        void fetchReminders();

        void addReminder(int timePosition, int frequencyPosition);

        void updateReminder(Reminder reminder, boolean isEnabled);

        void deleteClicked();

        void checkboxToggled(Reminder reminder, boolean isChecked);

        void setDeleteChecked(int position);

        void clearAllDeletes();
    }
}
