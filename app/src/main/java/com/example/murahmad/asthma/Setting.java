package com.example.murahmad.asthma;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by muradahmad on 31/08/2018.
 */

public class Setting extends Fragment {

    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;


    private Button btnMorning;
    private Button btnEvening;



    private Button btnSave;

    String morningTime;
    String eveningTime;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_settings, container,false);



        dbHandler = new Database(getContext());
        db = dbHandler.getWritableDatabase();


        // timer buttons
        btnMorning = (Button) view.findViewById(R.id.btnMorning);
        btnEvening = (Button) view.findViewById(R.id.btnEvening);

        btnSave = (Button) view.findViewById(R.id.btnSave);



        btnMorning.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        btnMorning.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });



        btnEvening.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        btnEvening.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });




        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1. save data,
                morningTime = btnMorning.getText().toString();
                eveningTime = btnEvening.getText().toString();




                ContentValues values = new ContentValues();
                values.put(Database.MORNING_TIME, morningTime);
                values.put(Database.EVENING_TIME, eveningTime);
                values.put(Database.Setting_timestamp,System.currentTimeMillis());

                dbHandler.insertSettingData(values);

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
    });

                return view;
    }



}
