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
    public static final String DATE_FORMAT_yMd = "yyyy-MM-dd"; // yMd - year month day
    public static final String DATE_FORMAT_Hm = "HH:mm"; // Hm - hour minute
    public static final String DATE_FORMAT_dMy = "dd.MM.yy"; // dMy - day month year

    public GregorianCalendar date;
    public String text;

    // эти поля нужны для оптимизации
    public long dateInMillis, dateInMillis_yMd; // yMd - year month day

    public Reminder(GregorianCalendar date, String text) {
        this.date = date;
        this.text = text;

        if (this.date != null) setup();
    }

    public Reminder(Reminder reminder) {
        date = reminder.date;
        text = reminder.text;
        dateInMillis = reminder.dateInMillis;
        dateInMillis_yMd = reminder.dateInMillis_yMd;
    }

    public void setup() {
        dateInMillis = date.getTimeInMillis();
        dateInMillis_yMd = LocalDateTime.ofInstant(date.toInstant(),
                date.getTimeZone().toZoneId()).toLocalDate().toEpochDay();
    }

    public String getYearMonthDay() {
        return new SimpleDateFormat(DATE_FORMAT_yMd).format(dateInMillis);
    }

    public String getHourMinute() {
        return new SimpleDateFormat(DATE_FORMAT_Hm).format(dateInMillis);
    }

    public String getDayMonthYear() {
        return new SimpleDateFormat(DATE_FORMAT_dMy).format(dateInMillis);
    }

    public static class Comparator implements java.util.Comparator<Reminder> {

        @Override
        public int compare(Reminder r1, Reminder r2) {
            return Long.compare(r1.dateInMillis, r2.dateInMillis);
        }
    }

    public static class Manager {

        public static final ArrayList<Reminder> reminders = new ArrayList<>();
        public static final TreeMap<Long, Reminder> reminderMap_yMd = new TreeMap<>(); // yMd - year month day

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
                    reminderMap_yMd.put(r.dateInMillis_yMd, r);

                    reminder = "";
                } else
                    reminder += savedReminders.charAt(i);
            }
        }

        public static void sortReminders() {
            Collections.sort(reminders, new Comparator());

            reminderMap_yMd.clear();
            for (int i = 0; i < reminders.size(); i++) {
                Reminder reminder = reminders.get(i);
                reminderMap_yMd.put(reminder.dateInMillis_yMd, reminder);
            }
        }

        public static boolean reminderExist(long dateInMillis_yMd) {
            if (reminderMap_yMd.containsKey(dateInMillis_yMd)) return true;
            else {
                for (int i = 0; i < reminders.size(); i++) {
                    Reminder reminder = reminders.get(i);
                    reminderMap_yMd.put(reminder.dateInMillis_yMd, reminder);
                    if (reminder.dateInMillis_yMd == dateInMillis_yMd) break;
                }

                return reminderMap_yMd.containsKey(dateInMillis_yMd);
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
                if (reminder.dateInMillis_yMd < now) reminders.remove(reminder);
                else break;
            }
        }
    }
}
