package com.example.murahmad.asthma;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

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




        // send User notification






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

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.SECOND,30);
                        calendar.set(Calendar.MILLISECOND,0);
                        morningTime = String.valueOf(calendar.getTimeInMillis());

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

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.SECOND,30);
                        calendar.set(Calendar.MILLISECOND,0);
                        eveningTime = String.valueOf(calendar.getTimeInMillis());

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
               /* morningTime = btnMorning.getText().toString();
                eveningTime = btnEvening.getText().toString();

*/
               if(validate()) {
                  // notification();


                ContentValues values = new ContentValues();
                values.put(Database.MORNING_TIME, morningTime);
                values.put(Database.EVENING_TIME, eveningTime);
                values.put(Database.Setting_timestamp,System.currentTimeMillis());

                dbHandler.insertSettingData(values);
                dbHandler.close();

                   Intent intent = new Intent(getContext(), MainActivity.class);
                   startActivity(intent);
               }
               else
               {
                   Toast.makeText(getContext(), "Enter notification time ", Toast.LENGTH_LONG).show();
               }
            }
    });

                return view;
    }
/*
    public void notification(){


        if(morningTime != null && !morningTime.isEmpty()) {


            // set notification time here
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 30);
            calendar.set(Calendar.MILLISECOND, 0);
            long currentTime = calendar.getTimeInMillis();
            // System.currentTimeMillis();

            Log.d("current Time", String.valueOf(currentTime));

            Long morningTimeLong = Long.valueOf(morningTime);
            //Long eveningTimeLong = Long.valueOf(eveningTime);

            Log.d("Morning time from Set", String.valueOf(morningTimeLong));

            if (morningTimeLong.compareTo(currentTime)>= 0)  {

                Intent intent = new Intent(getContext(), NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                // AlarmManager.Interval_Day set according to settings screen
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            }

        }

        //||

        if(eveningTime != null && !eveningTime.isEmpty()) {

            // set notification time here
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 30);
            calendar.set(Calendar.MILLISECOND, 0);
            long currentTime = calendar.getTimeInMillis();
            // System.currentTimeMillis();

            Log.d("current Time", String.valueOf(currentTime));


            Long eveningTimeLong = Long.valueOf(eveningTime);

            Log.d("Morning time from Set", String.valueOf(eveningTimeLong));

            if (eveningTimeLong.compareTo(currentTime)>= 0)  {

                Intent intent = new Intent(getContext(), NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                // AlarmManager.Interval_Day set according to settings screen
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            }

        }


    }*/


    public boolean validate() {
        boolean valid = true;


        String morningTime = btnMorning.getText().toString();
        String eveningTime = btnEvening.getText().toString();


        if (morningTime.isEmpty()) {
            btnMorning.setError("select symptoms notification time");
        }
        if (eveningTime.isEmpty()) {
            btnEvening.setError("select symptoms notification time");
        } /*else {
            valid = false;
        }
*/

        return valid;

    }

}
