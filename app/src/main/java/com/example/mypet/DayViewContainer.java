package com.example.mypet;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.view.ViewContainer;

import kotlin.jvm.internal.Intrinsics;

public class DayViewContainer extends ViewContainer {

    public TextView textView;

    public boolean selected;

    public DayViewContainer(@NonNull View view) {
        super(view);

        textView = view.findViewById(R.id.calendar_day_TV);
    }
}

