package com.example.mypet;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.ImageView;

import com.google.android.material.resources.TextAppearance;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar);

        calendar = findViewById(R.id.calendar_CV);
    }
}