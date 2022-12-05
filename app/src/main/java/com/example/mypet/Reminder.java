package com.example.mypet;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Reminder {

    public String date, text;
    public String[] decomposedDate;

    public long dateInMillis, dateInMillisYMD; // YMD - Year Month Day

    public Reminder(String date, String text) {
        this.date = date;
        this.text = text;

        if (!this.date.isEmpty()) setup();
    }

    public void setup() {
        decomposedDate = DateAndTime.decomposeDate(date);
        dateInMillis = dateToMillis();
        dateInMillisYMD = DateAndTime.dateToMillis(getYearMonthDay());
    }

    public long dateToMillis() {
        return DateAndTime.dateToMillis(date);
    }

    public String getYearMonthDay() { // yyyy-MM-dd
        return decomposedDate[DateAndTime.MODE_YEAR] + "-" + decomposedDate[DateAndTime.MODE_MONTH] + "-" + decomposedDate[DateAndTime.MODE_DAY];
    }

    public String getHourMinute() { // HH:mm
        return decomposedDate[DateAndTime.MODE_HOUR] + ":" + decomposedDate[DateAndTime.MODE_MINUTE];
    }

    public String getDayMonthYear() { // dd.MM.yy
        String year = decomposedDate[DateAndTime.MODE_YEAR];
        return decomposedDate[DateAndTime.MODE_DAY] + "." + decomposedDate[DateAndTime.MODE_MONTH] + "." +
                year.charAt(year.length() - 2) + year.charAt(year.length() - 1);
    }

    public static class DateAndTime {

        public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

        public static final int MODE_YEAR = 0;
        public static final int MODE_MONTH = 1;
        public static final int MODE_DAY = 2;
        public static final int MODE_HOUR = 3;
        public static final int MODE_MINUTE = 4;

        public static String getTime(String date, int mode, boolean withZeroAtBeginning) {
            StringBuilder result = new StringBuilder(decomposeDate(date)[mode]);
            if (!withZeroAtBeginning && result.charAt(0) == '0') result.deleteCharAt(0);

            return result.toString();
        }

        public static long dateToMillis(String date) {
            String[] decomposedDate = decomposeDate(date);
            for (int i = 0; i < decomposedDate.length; i++)
                if (decomposedDate[i].equals("")) decomposedDate[i] = "00";

            date = formatDate(decomposedDate);

            LocalDateTime localDateTime = LocalDateTime.parse(date,
                    DateTimeFormatter.ofPattern(DATE_FORMAT));

            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        public static String[] decomposeDate(String date) {
            String[] decomposedDate = new String[5];
            Arrays.fill(decomposedDate, "");

            for (int i = 0, count = 0; count < decomposedDate.length && i < date.length(); i++) {
                char currentChar = date.charAt(i);

                if (!Character.isDigit(currentChar)) count++;
                else decomposedDate[count] += currentChar;
            }

            return decomposedDate;
        }

        private static String formatDate(String[] decomposedDate) {
            while (decomposedDate[0].length() < 4)
                decomposedDate[0] = "0" + decomposedDate[0];

            for (int i = 1; i < decomposedDate.length; i++) {
                while (decomposedDate[i].length() < 2)
                    decomposedDate[i] = "0" + decomposedDate[i];
            }

            return decomposedDate[0] + "-" +  // yyyy
                    decomposedDate[1] + "-" + // MM
                    decomposedDate[2] + " " + // dd
                    decomposedDate[3] + ":" + // HH
                    decomposedDate[4];
        }
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
            String savedReminders = "";
            for (int i = 0; i < reminders.size(); i++) {
                savedReminders += new Gson().toJson(reminders.get(i)) + "\n";
            }

            FileOutputStream file = null;
            try {
                file = context.openFileOutput(REMINDERS_FILE_NAME, Context.MODE_PRIVATE);

                file.write(savedReminders.getBytes());
            } catch (IOException e) {
                //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {

                try {
                    if (file != null) file.close();
                } catch (IOException e) {
                    //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
