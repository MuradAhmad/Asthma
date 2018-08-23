package com.example.murahmad.asthma;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by muradahmad on 05/08/2018.
 */

public class Medication extends Fragment {


    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;

    TextView edtmedicationDate;

    EditText edtdrugs, edtotherDrugs, edtnewDrugs, edtvisits, edtotherVisits, edtotherconsiderations;

    Button btnSave;

    String medicationDate, drugs, otherDrugs,newDrugs,  visits,otherVisits,otherconsiderations;

    int year, month, day;
    DatePickerDialog datePickerDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.activity_medication, container,false);

        edtmedicationDate = (TextView) view.findViewById(R.id.edtmedicationDate);
        edtdrugs = (EditText) view.findViewById(R.id.edtdrugs);
        edtotherDrugs = (EditText) view.findViewById(R.id.edtotherMedication);
        edtnewDrugs = (EditText) view.findViewById(R.id.edtnewDrugs);
        edtvisits = (EditText) view.findViewById(R.id.edtvisits);
        edtotherVisits = (EditText) view.findViewById(R.id.edtotherVisits);
        edtotherconsiderations = (EditText) view.findViewById(R.id.edtotherConsideration);

        btnSave = (Button) view.findViewById(R.id.btnSave);



        dbHandler = new Database(getContext());
        db = dbHandler.getWritableDatabase();



        // Strings data

        medicationDate = edtmedicationDate.getText().toString();
        drugs = edtdrugs.getText().toString();
        otherDrugs = edtotherDrugs.getText().toString();
        newDrugs = edtnewDrugs.getText().toString();
        visits = edtvisits.getText().toString();
        otherVisits = edtotherVisits.getText().toString();
        otherconsiderations = edtotherconsiderations.getText().toString();



        edtmedicationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDay;
                        edtmedicationDate.setText(new StringBuilder().append(month + 1)
                                .append("-").append(day).append("-").append(year)
                                .append(" "));

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });




        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 1. save data,


                medicationDate = edtmedicationDate.getText().toString();
                drugs = edtdrugs.getText().toString();
                otherDrugs = edtotherDrugs.getText().toString();
                newDrugs = edtnewDrugs.getText().toString();
                visits = edtvisits.getText().toString();
                otherVisits = edtotherVisits.getText().toString();
                otherconsiderations = edtotherconsiderations.getText().toString();



                ContentValues values = new ContentValues();
                values.put(Database.MED_DATE, medicationDate);
                values.put(Database.DRUGS, drugs);
                values.put(Database.OTHER_DRUGS, otherDrugs);
                values.put(Database.NEW_DRUGS, newDrugs);
                values.put(Database.DOC_VISITS_ASTHMA,visits);
                values.put(Database.DOC_VISITS_ALLERGY, otherVisits);
                values.put(Database.OTHER,otherconsiderations );


                dbHandler.insertMedicationData(values);



                // 2.validate data

               Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);



            }
        });





        // save button
        // save data in database
        // goto dashboard


        return view;

    }
}
