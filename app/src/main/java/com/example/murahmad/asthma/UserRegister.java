package com.example.murahmad.asthma;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by muradahmad on 10/07/2018.
 */

public class UserRegister extends AppCompatActivity {


    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;

    TextView txtDateOfBirth;

    EditText  txtName, txtEmail,txtPassword, txtConfirmPassword;
    CheckBox chkConsent;
    Button btnRegister;

    String name,dateOfBirth,email,password,confirmPassword;


    int year, month, day;
    DatePickerDialog datePickerDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);


        txtName = (EditText) findViewById(R.id.userName);
        txtDateOfBirth = (TextView) findViewById(R.id.userDOB);
        txtEmail = (EditText) findViewById(R.id.userEmail);
        txtPassword = (EditText) findViewById(R.id.userPassward);
        txtConfirmPassword = (EditText) findViewById(R.id.userPasswardConfirm);

        chkConsent = (CheckBox)findViewById(R.id.chkbConsent);

        btnRegister = (Button)findViewById(R.id.btnRegister);










        chkConsent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    //Toast.makeText(UserRegister.this, "Check Box is selected", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(UserRegister.this, Consent.class);
                    startActivity(intent);
                }
            }
        });



        dbHandler = new Database(getApplicationContext());
        db = dbHandler.getWritableDatabase();




/*
        final Handler threadHandler = new Handler();
        Runnable runnable = new Runnable()
        {
            public void run() {
                //Whatever task you wish to perform

        cursor = db.rawQuery("SELECT * FROM " + Database.REGISTRATION_TABLE, null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
// get values from cursor here




                Log.d("User Name: ", cursor.getString(cursor.getColumnIndex(Database.USERNAME)));
                Log.d("Email ", cursor.getString(cursor.getColumnIndex(Database.EMAIL)));
                Log.d("Date of Birth: ", cursor.getString(cursor.getColumnIndex(Database.DOB)));
            }
        }
        threadHandler.postDelayed(this, 1000);
    }
};*/





        btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        name = txtName.getText().toString();
                        dateOfBirth = txtDateOfBirth.getText().toString();
                        email = txtEmail.getText().toString();
                        password = txtPassword.getText().toString();
                        confirmPassword = txtConfirmPassword.getText().toString();


                        ContentValues values = new ContentValues();
                        values.put(Database.USERNAME, name);
                        values.put(Database.DOB, dateOfBirth);
                        values.put(Database.EMAIL, email);
                        values.put(Database.PASSWORD, password);
                        values.put(Database.reg_timestamp,System.currentTimeMillis());

                        dbHandler.insertRegistrationData(values);

                        // 1. save data,



                        // 2.validate data

                     if(validate()) {
                         Intent intent = new Intent(UserRegister.this, MainActivity.class);
                         startActivity(intent);

                     }

                    }
                });






        txtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(UserRegister.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDay;
                        txtDateOfBirth.setText(new StringBuilder().append(month + 1)
                                .append("-").append(day).append("-").append(year)
                                .append(" "));

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });









   /* public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbConsent:
                if (checked){
                    // save data

                }

            else
                // must select checkbox
                break;


            // TODO: consent page
        }
    }*/

    }


    public boolean validate() {
        boolean valid = true;


       String name = txtName.getText().toString();
        String dateOfBirth = txtDateOfBirth.getText().toString();
       String email = txtEmail.getText().toString();
       String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();




        if (((CheckBox) chkConsent).isChecked()){
            return true;
        }else
        {
            Toast.makeText(this, "Select Consent", Toast.LENGTH_SHORT).show();
        }

            if(name.isEmpty()) {
            txtName.setError("enter username");
        }
        if(dateOfBirth.isEmpty()) {
            txtDateOfBirth.setError("enter date of birth");
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("enter a valid email address");
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            txtPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || confirmPassword.length() > 10) {
            txtConfirmPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            txtConfirmPassword.setError(null);
        }

        if(confirmPassword.equals(password)) {
               txtConfirmPassword.setError(null);

        }else {
            txtConfirmPassword.setError("password not same");
        }






        return valid;
    }

}
