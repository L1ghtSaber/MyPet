package com.example.mypet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.TreeMap;


@RequiresApi(api = Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
public class Reminder {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    public GregorianCalendar date;
    public String text;

    public long dateInMillis, dateInMillisYMD; // YMD - Year Month Day

    public Reminder(GregorianCalendar date, String text) {
        this.date = date;
        this.text = text;

        if (this.date != null) setup();
    }

    public Reminder(Reminder reminder) {
        date = reminder.date;
        text = reminder.text;
        dateInMillis = reminder.dateInMillis;
        dateInMillisYMD = reminder.dateInMillisYMD;
    }

    public void setup() {
        dateInMillis = date.getTimeInMillis();
        dateInMillisYMD = LocalDateTime.ofInstant(date.toInstant(),
                date.getTimeZone().toZoneId()).toLocalDate().toEpochDay();
    }

    public String getYearMonthDay() {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    public String getHourMinute() {
        return new SimpleDateFormat("HH:mm").format(date.getTime());
    }

    public String getDayMonthYear() {
        return new SimpleDateFormat("dd.MM.yy").format(date.getTime());
    }

    public static class Comparator implements java.util.Comparator<Reminder> {

        @Override
        public int compare(Reminder r1, Reminder r2) {
            return Long.compare(r1.dateInMillis, r2.dateInMillis);
        }
    }

    public static class Manager {

        public static final ArrayList<Reminder> reminders = new ArrayList<>();
        public static final TreeMap<Long, Reminder> reminderMapYMD = new TreeMap<>(); // YMD - Year Month Day

        private static final String REMINDERS_FILE_NAME = "reminders.txt";

        public static void loadReminders(Context context) {
            reminders.clear();

            FileInputStream file = null;

            String savedReminders = "";
            try {
                file = context.openFileInput(REMINDERS_FILE_NAME);

                byte[] bytes = new byte[file.available()];
                file.read(bytes);
                savedReminders = new String(bytes);
            } catch (IOException e) {
                //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {

                try {
                    if (file != null) file.close();
                } catch (IOException ex) {
                    //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            if (savedReminders.equals("")) return;

            String reminder = "";
            for (int i = 0; i < savedReminders.length(); i++) {
                if (savedReminders.charAt(i) == '\n') {
                    Reminder r = new Gson().fromJson(reminder, Reminder.class);
                    reminders.add(r);
                    reminderMapYMD.put(r.dateInMillisYMD, r);

                    reminder = "";
                } else
                    reminder += savedReminders.charAt(i);
            }
        }

        public static void sortReminders() {
            Collections.sort(reminders, new Comparator());

            reminderMapYMD.clear();
            for (int i = 0; i < reminders.size(); i++) {
                Reminder reminder = reminders.get(i);
                reminderMapYMD.put(reminder.dateInMillisYMD, reminder);
            }
        }

        public static boolean reminderExist(long dateInMillisYMD) {
            if (reminderMapYMD.containsKey(dateInMillisYMD)) return true;
            else {
                for (int i = 0; i < reminders.size(); i++) {
                    Reminder reminder = reminders.get(i);
                    reminderMapYMD.put(reminder.dateInMillisYMD, reminder);
                    if (reminder.dateInMillisYMD == dateInMillisYMD) break;
                }

                return reminderMapYMD.containsKey(dateInMillisYMD);
            }
        }

        public static void saveReminders(Context context) {
            deletePastReminders();

            String savedReminders = "";
            for (int i = 0; i < reminders.size(); i++) {
                savedReminders += new Gson().toJson(reminders.get(i)) + "\n";
            }

            try (FileOutputStream file = context.openFileOutput(REMINDERS_FILE_NAME, Context.MODE_PRIVATE)) {

                file.write(savedReminders.getBytes());
            } catch (IOException e) {
                //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        public static void deletePastReminders() {
            long now = LocalDate.now().toEpochDay();

            for (int i = 0; i < reminders.size(); i++) {
                Reminder reminder = reminders.get(i);
                if (reminder.dateInMillisYMD < now) reminders.remove(reminder);
                else break;
            }
        }
    }
}
