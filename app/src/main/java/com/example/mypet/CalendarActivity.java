package com.example.mypet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.DaySize;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalendarActivity extends AppCompatActivity {

    public static final String KEY_MODE = "mode";
    public static final String KEY_REMINDER = "reminder";

    public static final int MODE_ADD = 0;
    public static final int MODE_EDIT = 1;

    public static final String[] daysOfWeek = {"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
    public static final String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    private CalendarView calendar;
    private ListView reminders;
    private TextView remindersTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar);

        if (Reminder.Manager.reminders.isEmpty()) Reminder.Manager.loadReminders(this);

        // смена цвета кружка
        ImageView addReminderCircle = findViewById(R.id.add_reminder_circle_IV);
        GradientDrawable circle = (GradientDrawable) addReminderCircle.getBackground();
        circle.setColor(getResources().getColor(R.color.pink));
        addReminderCircle.setBackground(circle);

        remindersTitle = findViewById(R.id.reminders_title_TV);
        reminders = findViewById(R.id.reminders_LV);

        calendar = findViewById(R.id.readable_calendar_CV_lib);
        fullCalendarViewSetup(calendar);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        fullCalendarViewSetup(calendar);
    }

    public static void setupCalendarView(CalendarView calendar) {
        YearMonth[] minMaxYearMonth = getMinMaxYearMonth();
        calendar.setup(minMaxYearMonth[0], minMaxYearMonth[1], DayOfWeek.MONDAY);

        calendar.scrollToDate(LocalDate.now());
        calendar.setOverScrollMode(View.OVER_SCROLL_NEVER);
        calendar.setDaySize(DaySize.Square);
    }

    private static YearMonth[] getMinMaxYearMonth() {
        YearMonth yearMonthMin = YearMonth.now(),
                yearMonthMax = YearMonth.of(YearMonth.now().getYear() + 5,
                        YearMonth.now().getMonth());

        return new YearMonth[]{yearMonthMin, yearMonthMax};
    }

    public static void selectDay(TextView day, Context context) {
        // смена цвета кружка
        day.setBackgroundResource(R.drawable.circle_shape);
        GradientDrawable circle = (GradientDrawable) day.getBackground();
        circle.setColor(context.getResources().getColor(R.color.blue));
        day.setBackground(circle);

        day.setTextColor(Color.BLACK);
    }

    public static void unselectDay(TextView day, Context context) {
        day.setBackgroundResource(0);
        day.setTextColor(context.getResources().getColor(R.color.sunset_orange));
    }

    public static void setMonthHeaderBinder(CalendarView calendar, MonthViewContainer container, CalendarMonth calendarMonth) {
        YearMonth[] minMaxYearMonth = getMinMaxYearMonth();

        if (container.titlesContainer.getTag() == null) {
            container.titlesContainer.setTag(calendarMonth.getYearMonth());

            // цикл именно здесь для оптимизации
            for (int i = 0; i < 7; i++)
                ((TextView) container.titlesContainer.getChildAt(i)).setText(daysOfWeek[i]);
        }

        int yearMin = minMaxYearMonth[0].getYear(), monthMin = minMaxYearMonth[0].getMonthValue(),
                yearMax = minMaxYearMonth[1].getYear(), monthMax = minMaxYearMonth[1].getMonthValue(),
                year = calendarMonth.getYearMonth().getYear(), month = calendarMonth.getYearMonth().getMonthValue();

        ((TextView) container.getView().findViewById(R.id.calendar_month_TV))
                .setText(months[month - 1]);

        String out = "" + year;
        ((TextView) container.getView().findViewById(R.id.calendar_year_IB)).setText(out);

        ImageButton previousYear = container.getView().findViewById(R.id.calendar_previous_year_IB),
                nextYear = container.getView().findViewById(R.id.calendar_next_year_IB),
                previousMonth = container.getView().findViewById(R.id.calendar_previous_month_IB),
                nextMonth = container.getView().findViewById(R.id.calendar_next_month_IB);

        previousYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                calendar.smoothScrollToDate(LocalDate.of(year - 1, month, 1));
            }
        });
        nextYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                calendar.smoothScrollToDate(LocalDate.of(year + 1, month, 1));
            }
        });
        previousMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                calendar.smoothScrollToDate(LocalDate
                        .of(year - ((month - 1 == 0) ? 1 : 0), (month - 1 == 0) ? 12 : month - 1, 1));
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                calendar.smoothScrollToDate(LocalDate.of(year + month / 12, month % 12 + 1, 1));
            }
        });



        if (year == yearMin) disableChangeDateButton(previousYear);
        else enableChangeDateButton(previousYear, true);

        if (year == yearMax) disableChangeDateButton(nextYear);
        else enableChangeDateButton(nextYear, true);

        if (year == yearMin && month == monthMin) disableChangeDateButton(previousMonth);
        else enableChangeDateButton(previousMonth, false);

        if (year == yearMax && month == monthMax) disableChangeDateButton(nextMonth);
        else enableChangeDateButton(nextMonth, false);
    }

    private static void disableChangeDateButton(ImageButton changeDateButton) {
        changeDateButton.setImageResource(0);
        changeDateButton.setClickable(false);
    }

    private static void enableChangeDateButton(ImageButton changeDateButton, boolean yearChange) {
        if (yearChange)
            changeDateButton.setImageResource(R.drawable.arrow_back_small);
        else
            changeDateButton.setImageResource(R.drawable.arrow_back);

        changeDateButton.setClickable(true);
    }

    public void addReminder(View view) {
        Intent makeReminder = new Intent(this, MakeReminderActivity.class);

        makeReminder.putExtra(KEY_MODE, MODE_ADD);

        startActivity(makeReminder);
    }

    private void fullCalendarViewSetup(CalendarView calendar) {
        setupCalendarView(calendar);

        long dayNow = LocalDate.now().toEpochDay(); // для оптимизации
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

                    if (calendarDay.getDate().isBefore(LocalDate.ofEpochDay(dayNow))) {
                        day.setTextColor(getResources().getColor(R.color.gray));
                        return;
                    }

                    if (Reminder.Manager.reminderExist(calendarDay.getDate().toEpochDay())) {
                        selectDay(day, CalendarActivity.this);
                        container.selected = true;
                    } else {
                        unselectDay(day, CalendarActivity.this);
                        container.selected = false;
                    }

                    if (calendarDay.getDate().toEpochDay() == dayNow)
                        setRemindersListView(getRemindersForDay(dayNow));

                    day.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if (!container.selected) {
                                setRemindersListView(new ArrayList<>());
                                return;
                            }

                            long dateInMillis_yMd = calendarDay.getDate().toEpochDay();

                            setRemindersListView(getRemindersForDay(dateInMillis_yMd));
                        }
                    });
                } else
                    day.setText("");
            }

            private ArrayList<Reminder> getRemindersForDay(long dateInMillis_yMd) {
                ArrayList<Reminder> reminders = new ArrayList<>();
                boolean dateFound = false;

                for (int i = 0; i < Reminder.Manager.reminders.size(); i++) {
                    Reminder reminder = Reminder.Manager.reminders.get(i);

                    if (reminder.dateInMillis_yMd == dateInMillis_yMd) {
                        dateFound = true;
                        reminders.add(reminder);
                    } else if (dateFound) break;
                }

                return reminders;
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
                setMonthHeaderBinder(calendar, container, calendarMonth);
            }
        });
    }

    private void setRemindersListView(ArrayList<Reminder> reminders) {
        this.reminders.setAdapter(new ReminderAdapter(this, reminders));

        if (!reminders.isEmpty())
            remindersTitle.setText("Напоминания");
        else
            remindersTitle.setText("");
    }

    private class ReminderAdapter extends ArrayAdapter<Reminder> {

        public ReminderAdapter(Context context, ArrayList<Reminder> reminders) {
            super(context, R.layout.reminder_item, reminders);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Reminder reminder = getItem(position);

            if (convertView == null)
                convertView = LayoutInflater.from(CalendarActivity.this).inflate(R.layout.reminder_item, null);


            ((TextView) convertView.findViewById(R.id.reminder_date_TV)).setText(reminder.getDayMonthYear());
            ((TextView) convertView.findViewById(R.id.reminder_time_TV)).setText(reminder.getHourMinute());
            ((TextView) convertView.findViewById(R.id.reminder_text_TV)).setText(reminder.text);

            ((ImageButton) convertView.findViewById(R.id.edit_reminder_IB)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent makeReminder = new Intent(CalendarActivity.this, MakeReminderActivity.class);

                    makeReminder.putExtra(KEY_MODE, MODE_EDIT);
                    makeReminder.putExtra(KEY_REMINDER, new Gson().toJson(reminder));

                    startActivity(makeReminder);
                }
            });

            return convertView;
        }
    }
}