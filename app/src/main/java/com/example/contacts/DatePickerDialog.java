package com.example.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment {

    Calendar selectedDate;
    CalendarView cv;
    Button select, cancel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_date, container);
        getDialog().setTitle("Select Date");
        selectedDate = Calendar.getInstance();
        initLayouts(view);
        return view;
    }

    private void initLayouts(View view) {
        cv = view.findViewById(R.id.calendarView);
        select = view.findViewById(R.id.buttonSelect);
        cancel = view.findViewById(R.id.buttonCancel);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate.set(year, month, dayOfMonth);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        select.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveItem(selectedDate);
            }
        });
    }

    private void saveItem(Calendar selectedDate) {
        SaveDateListener activity = (SaveDateListener) getActivity();
        activity.didFinishDatePickerDialog(selectedDate);
        getDialog().dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
    }

    public interface SaveDateListener{
        void didFinishDatePickerDialog(Calendar selectedDate);
    }
}
