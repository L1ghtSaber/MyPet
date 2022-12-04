package com.example.mypet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_menu);
    }

    public void startPetsActivity(View view) {
        startActivity(new Intent(this, PetsActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startCalendarActivity(View view) {
        startActivity(new Intent(this, CalendarActivity.class));
    }
}