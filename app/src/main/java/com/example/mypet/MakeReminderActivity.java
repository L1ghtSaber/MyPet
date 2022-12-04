package com.example.mypet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MakeReminderActivity extends AppCompatActivity {

    private TextView reminderTime, makeReminder;
    private EditText reminderText;

    private final DayViewContainer[] selectedDay = new DayViewContainer[1];
    private Reminder reminder;
    private Calendar time;
    private String selectedDate = "";

    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_make_reminder);

        time = Calendar.getInstance();
        mode = getIntent().getIntExtra(CalendarActivity.KEY_MODE, CalendarActivity.MODE_ADD);

        makeReminder = findViewById(R.id.make_reminder_TV);
        reminderTime = findViewById(R.id.reminder_time_set_TV);
        reminderText = findViewById(R.id.reminder_text_set_ET);

        reminderText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                changeMakeReminderTextColor();
            }
        });

        if (mode == CalendarActivity.MODE_ADD) {
            reminder = new Reminder("", "");

            reminderTime.setText(DateUtils.formatDateTime(MakeReminderActivity.this,
                    time.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

            makeReminder.setText("Добавить");
            makeReminder.setTextColor(getResources().getColor(R.color.gray));
        } else if (mode == CalendarActivity.MODE_EDIT) {
            int index = getIntent().getIntExtra(CalendarActivity.KEY_REMINDER_NUMBER, 0);
            reminder = Reminder.Manager.reminders.get(index);

            time.setTime(new Date(reminder.dateInMillis));

            reminderText.setText(reminder.text);
            reminderTime.setText(reminder.getHourMinute());
            makeReminder.setText("Изменить");
            makeReminder.setTextColor(getResources().getColor(R.color.sky_blue));

            TextView deleteReminder = findViewById(R.id.delete_reminder_TV);
            deleteReminder.setText("Удалить");
            deleteReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Reminder.Manager.reminders.remove(index);

                    Reminder.Manager.sortReminders();
                    Reminder.Manager.saveReminders(MakeReminderActivity.this);

                    finish();
                }
            });
        }

        CalendarView calendar = findViewById(R.id.writable_calendar_CV_lib);
        CalendarActivity.setupCalendarView(calendar);

        if (mode == CalendarActivity.MODE_EDIT)
            calendar.scrollToDate(LocalDate.parse(reminder.getYearMonthDay()));

        calendar.setDayBinder(new MonthDayBinder<DayViewContainer>() {

            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
                TextView day = container.getView().findViewById(R.id.calendar_day_TV);

                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    day.setText(Reminder.DateAndTime.getTime(calendarDay.getDate().toString(), Reminder.MODE_DAY, false));

                    if (calendarDay.getDate().isBefore(LocalDate.now())) {
                        day.setTextColor(getResources().getColor(R.color.gray));
                        return;
                    }

                    if (mode == CalendarActivity.MODE_EDIT
                            && calendarDay.getDate().toString().equals(reminder.getYearMonthDay())) {
                        CalendarActivity.selectDay(day, MakeReminderActivity.this);

                        selectedDay[0] = container;
                        selectedDay[0].selected = true;

                        selectedDate = reminder.getYearMonthDay();
                    }

                    day.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if (!container.selected) {
                                CalendarActivity.selectDay(day, MakeReminderActivity.this);

                                if (selectedDay[0] != null && selectedDay[0] != container) {
                                    CalendarActivity.unselectDay(selectedDay[0].textView, MakeReminderActivity.this);
                                    selectedDay[0].selected = false;
                                }
                                selectedDay[0] = container;
                                selectedDate = calendarDay.getDate().toString();
                            } else CalendarActivity.unselectDay(day, MakeReminderActivity.this);

                            container.selected = !container.selected;

                            changeMakeReminderTextColor();
                        }
                    });
                } else
                    day.setText("");
            }
        });
        calendar.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {

            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                if (selectedDay[0] != null) {
                    CalendarActivity.unselectDay(selectedDay[0].textView, MakeReminderActivity.this);
                    selectedDay[0].selected = false;
                }

                CalendarActivity.setMonthHeaderBinder(calendar, container, calendarMonth);
            }
        });
    }

    // открытие диалогового окна
    public void setTime(View view) {
        new TimePickerDialog(this, 0, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);

                reminderTime.setText(DateUtils.formatDateTime(MakeReminderActivity.this,
                        time.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

                changeMakeReminderTextColor();
            }
        }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true).show();
    }

    private boolean checkRulesToMakeReminder() {
        return selectedDay[0] != null && selectedDay[0].selected && reminderText.getText().toString().length() != 0;
    }

    private void changeMakeReminderTextColor() {
        if (!checkRulesToMakeReminder())
            makeReminder.setTextColor(getResources().getColor(R.color.gray));
        else
            makeReminder.setTextColor(getResources().getColor(R.color.sky_blue));
    }

    public void makeReminder(View view) {
        if (!checkRulesToMakeReminder()) {
            Toast.makeText(this, "Вы не ввели необходимые данные =(\nПопробуте снова",
                    Toast.LENGTH_SHORT).show();

            changeMakeReminderTextColor();

            return;
        }

        reminder.text = reminderText.getText().toString();

        String time = reminderTime.getText().toString(),
                hour = time.charAt(0) + "" + time.charAt(1), // я просто знаю, что время будет в формате HH:mm
                minute = time.charAt(3) + "" + time.charAt(4);

        reminder.date = selectedDate + " " + hour + ":" + minute;

        if (mode == CalendarActivity.MODE_ADD)
            Reminder.Manager.reminders.add(new Reminder(reminder.date, reminder.text));
        else if (mode == CalendarActivity.MODE_EDIT)
            reminder.setup();

        Reminder.Manager.sortReminders();
        Reminder.Manager.saveReminders(this);

        finish();
    }
}