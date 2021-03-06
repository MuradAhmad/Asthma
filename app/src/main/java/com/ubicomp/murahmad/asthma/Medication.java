package com.ubicomp.murahmad.asthma;

import android.app.DatePickerDialog;
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
import android.widget.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by muradahmad on 05/08/2018.
 */

public class Medication extends Fragment {

    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;

    private TextView edtmedicationDate, txtmedication;

    private EditText edtdrugs, edtotherDrugs, edtnewDrugs, edtvisits, edtotherVisits, edtotherconsiderations;

    private Button btnSave;

    String medicationDate, drugs, otherDrugs, newDrugs, visits, otherVisits, otherconsiderations;

    int year, month, day;
    DatePickerDialog datePickerDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_medication, container, false);

        txtmedication = (TextView) view.findViewById(R.id.txtmeds);
        edtmedicationDate = (TextView) view.findViewById(R.id.edtmedicationDate);
        edtdrugs = (EditText) view.findViewById(R.id.edtdrugs);
        edtotherDrugs = (EditText) view.findViewById(R.id.edtotherMedication);
        edtnewDrugs = (EditText) view.findViewById(R.id.edtnewDrugs);
        edtvisits = (EditText) view.findViewById(R.id.edtvisits);
        edtotherVisits = (EditText) view.findViewById(R.id.edtotherVisits);
        edtotherconsiderations = (EditText) view.findViewById(R.id.edtotherConsideration);

        btnSave = (Button) view.findViewById(R.id.btnSave);

        txtmedication.setText(R.string.meds);
        edtmedicationDate.setHint(R.string.openmeds);
        edtdrugs.setHint(R.string.therameds);
        edtotherDrugs.setHint(R.string.othermeds);
        edtnewDrugs.setHint(R.string.changmeds);
        edtvisits.setHint(R.string.docvisit1);
        edtotherVisits.setHint(R.string.docvisit2);
        edtotherconsiderations.setHint(R.string.otherconsider);

        btnSave.setText(R.string.save);


        dbHandler = new Database(getContext());
        db = dbHandler.getWritableDatabase();


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

                //medicationDate = edtmedicationDate.getText().toString();

                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");

                double medicate_time = 0;

                try {
                    medicate_time = format.parse(edtmedicationDate.getText().toString()).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                drugs = edtdrugs.getText().toString();
                otherDrugs = edtotherDrugs.getText().toString();
                newDrugs = edtnewDrugs.getText().toString();
                visits = edtvisits.getText().toString();
                otherVisits = edtotherVisits.getText().toString();
                otherconsiderations = edtotherconsiderations.getText().toString();

                if (validate()) {
                    ContentValues values = new ContentValues();
                    values.put(Database.MED_DATE, medicate_time);
                    values.put(Database.DRUGS, drugs);
                    values.put(Database.OTHER_DRUGS, otherDrugs);
                    values.put(Database.NEW_DRUGS, newDrugs);
                    values.put(Database.DOC_VISITS_ASTHMA, visits);
                    values.put(Database.DOC_VISITS_ALLERGY, otherVisits);
                    values.put(Database.OTHER, otherconsiderations);

                    dbHandler.insertMedicationData(values);

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), getString(R.string.feedback_error_form), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public boolean validate() {
        medicationDate = edtmedicationDate.getText().toString();
        drugs = edtdrugs.getText().toString();
        otherDrugs = edtotherDrugs.getText().toString();
        newDrugs = edtnewDrugs.getText().toString();
        visits = edtvisits.getText().toString();
        otherVisits = edtotherVisits.getText().toString();
        otherconsiderations = edtotherconsiderations.getText().toString();

        if (drugs.isEmpty()) {
            edtdrugs.setError("enter drugs");
            return false;
        } else {
            return true;
        }
    }
}
