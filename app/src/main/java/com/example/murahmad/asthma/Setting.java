package com.example.murahmad.asthma;

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
import android.widget.Toast;

/**
 * Created by muradahmad on 31/08/2018.
 */

public class Setting extends Fragment {

    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;


    private TextView edtMorningHr;
    private TextView edtMorningMin;
    private TextView edtEveningHr;
    private TextView edtEveningMin;
    private NumberPicker numberPicker;

    private Button btnSave;

    String morningHr,morningMin,eveningHr,eveningMin;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_settings, container,false);



        dbHandler = new Database(getContext());
        db = dbHandler.getWritableDatabase();


        edtMorningHr = (TextView) view.findViewById(R.id.edtMorningHour);
        edtMorningMin = (TextView) view.findViewById(R.id.edtMorningMinutes);
        edtEveningHr = (TextView) view.findViewById(R.id.edtEveningHour);
        edtEveningMin = (TextView) view.findViewById(R.id.edtEveningMinutes);

        btnSave = (Button) view.findViewById(R.id.btnSave);

/*

        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
                numberPicker.setMaxValue(24);
                numberPicker.setMinValue(1);
               // numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(onValueChangeListener);
                //edtMorningHr.setText(String.valueOf(numberPicker.getValue()));

*/







        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1. save data,
                morningHr = edtMorningHr.getText().toString();
                morningMin = edtMorningMin.getText().toString();
                eveningHr = edtEveningHr.getText().toString();
                eveningMin = edtEveningMin.getText().toString();



                ContentValues values = new ContentValues();
                values.put(Database.MORNING_HR, morningHr);
                values.put(Database.MORNING_MIN, morningMin);
                values.put(Database.EVENING_HR, eveningHr);
                values.put(Database.EVENING_MIN, eveningMin);
                values.put(Database.Setting_timestamp,System.currentTimeMillis());

                dbHandler.insertSettingData(values);

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
    });

                return view;
    }
 /*   NumberPicker.OnValueChangeListener onValueChangeListener =
            new 	NumberPicker.OnValueChangeListener(){
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    Toast.makeText(getContext(),
                            "selected number "+numberPicker.getValue(), Toast.LENGTH_SHORT);
                    //edtMorningHr.setText(numberPicker.getValue());

                }
            };*/


}
