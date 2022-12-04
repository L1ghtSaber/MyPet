package com.example.mypet;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.view.ViewContainer;

public class MonthViewContainer extends ViewContainer {

    public ViewGroup titlesContainer;

    public MonthViewContainer(@NonNull View view) {
        super(view);

        titlesContainer = view.findViewById(R.id.calendar_day_titles_container_LV);
    }
}
