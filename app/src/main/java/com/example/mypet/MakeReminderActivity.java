package com.example.mypet;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
            reminder = new Reminder(new GregorianCalendar(), "");

            reminderTime.setText(DateUtils.formatDateTime(MakeReminderActivity.this,
                    time.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

            makeReminder.setText("Добавить");
            makeReminder.setTextColor(getResources().getColor(R.color.gray));
        } else if (mode == CalendarActivity.MODE_EDIT) {
            reminder = new Gson().fromJson(getIntent().getStringExtra(CalendarActivity.KEY_REMINDER), Reminder.class);

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
                    Reminder.Manager.reminders.remove(getReminderIndex(reminder));

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
                    String out = "" + calendarDay.getDate().getDayOfMonth();
                    day.setText(out);

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

        Reminder oldReminder = new Reminder(reminder);

        reminder.text = reminderText.getText().toString();
        // выглядит сложно, но на самом деле здесь просто парсится строка с датой и временем в миллисекунды
        reminder.date.setTimeInMillis(LocalDateTime.parse(selectedDate + " " + DateUtils
                .formatDateTime(this, this.time.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME),
                DateTimeFormatter.ofPattern(Reminder.DATE_FORMAT))
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        if (mode == CalendarActivity.MODE_ADD)
            Reminder.Manager.reminders.add(new Reminder(reminder.date, reminder.text));
        else if (mode == CalendarActivity.MODE_EDIT) {
            reminder.setup();

            int index = getReminderIndex(oldReminder);
            if (index >= 0) Reminder.Manager.reminders.set(index, reminder);
        }

        Reminder.Manager.sortReminders();
        Reminder.Manager.saveReminders(this);

        finish();
    }

    private int getReminderIndex(Reminder reminder) {
        for (int i = 0; i < Reminder.Manager.reminders.size(); i++) {
            Reminder currentReminder = Reminder.Manager.reminders.get(i);
            if (currentReminder.dateInMillis == reminder.dateInMillis
                    && currentReminder.text.equals(reminder.text))
                return i;
        }

        return -1;
    }
}