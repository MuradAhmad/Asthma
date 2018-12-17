package com.ubicomp.murahmad.asthma;

import android.app.DatePickerDialog;
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
import android.widget.TextView;

/**
 * Created by muradahmad on 07/08/2018.
 */

public class UserProfile extends Fragment {

    Database handler;
    SQLiteDatabase db;
    Cursor cursor;

    TextView txtdrugs, txtusername, txtdateofbirth, txtcurrentMeds;

    Button btnSave;

    String drugs, userName, dateOfBirth;

    int year, month, day;
    DatePickerDialog datePickerDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_userprofile, container, false);


        txtusername = (TextView) view.findViewById(R.id.txtusername);
        txtdateofbirth = (TextView) view.findViewById(R.id.txtdateofbirth);
        txtcurrentMeds = (TextView) view.findViewById(R.id.txtCurrentMeds);
        txtdrugs = (TextView) view.findViewById(R.id.txtdrugs);


        txtusername.setText(R.string.user);
        txtdateofbirth.setText(R.string.userid);
        txtcurrentMeds.setText(R.string.medication);
        txtdrugs.setText(R.string.drugs);


        handler = new Database(getContext());
        db = handler.getReadableDatabase();


        displayUserProfile();


        return view;


    }

    public void displayUserProfile() {


        cursor = db.rawQuery("SELECT * FROM " + Database.REGISTRATION_TABLE + "," + Database.MEDICATION_TABLE, null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
// get values from cursor here


                String name = cursor.getString(cursor.getColumnIndex(Database.USERNAME));
                String userId = cursor.getString(cursor.getColumnIndex(Database.UUID));
                String drugs = cursor.getString(cursor.getColumnIndex(Database.DRUGS));

                        /*if(drugs == null)
                        {
                            drugs = "insert drugs";
                        }*/
                Log.d("username", name);
                Log.d("UUID", userId);

                txtusername.setText(name);
                txtdateofbirth.setText(userId);
                txtdrugs.setText(drugs);

            }
        }
        cursor.close();
        db.close();

    }


}