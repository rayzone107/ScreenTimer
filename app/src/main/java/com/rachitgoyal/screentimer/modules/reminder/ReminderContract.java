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

        void toggleAddState(boolean isExpanded);

        void scrollToEnd();
    }

    interface Presenter {
        void fetchReminders();

        void addReminder(int hours, int mins, boolean isRecurring);

        void updateReminder(Reminder reminder, boolean isEnabled);

        void deleteClicked();

        void checkboxToggled(Reminder reminder, boolean isChecked);

        void setDeleteChecked(int position);

        void clearAllDeletes();
    }
}
