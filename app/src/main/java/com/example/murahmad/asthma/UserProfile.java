package com.example.murahmad.asthma;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by muradahmad on 07/08/2018.
 */

public class UserProfile extends Fragment {

    Database handler;
    SQLiteDatabase db;
    Cursor cursor;

    TextView txtdrugs, txtusername, txtdateofbirth;

    Button btnSave;

    String drugs, userName,dateOfBirth;

    int year, month, day;
    DatePickerDialog datePickerDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.activity_userprofile, container,false);



        txtusername = (TextView) view.findViewById(R.id.txtusername);
        txtdateofbirth = (TextView)view.findViewById(R.id.txtdateofbirth);
        txtdrugs = (TextView)view.findViewById(R.id.txtdrugs);






        handler = new Database(getContext());
        db = handler.getReadableDatabase();



        final Handler threadHandler = new Handler();
        Runnable runnable = new Runnable()
        {
            public void run() {
                //Whatever task you wish to perform
                //For eg. textView.setText("SOME TEXT")

                cursor = db.rawQuery("SELECT * FROM " + Database.REGISTRATION_TABLE  +","+ Database.MEDICATION_TABLE , null);

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
                        txtusername.setText(name);
                        txtdateofbirth.setText(userId);
                        txtdrugs.setText(drugs);



                    }
                }
                cursor.close();




                threadHandler.postDelayed(this, 1000);
            }
        };
        threadHandler.postDelayed(runnable, 1000);



        return view;


    }


}