package com.example.murahmad.asthma;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    EditText txtDateOfBirth, txtName, txtEmail,txtPassword, txtConfirmPassword;
    CheckBox chkConsent;
    Button btnRegister;

    String name,dateOfBirth,email,password,confirmPassword;


    int year, month, day;
    DatePickerDialog datePickerDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);


        txtName = (EditText) findViewById(R.id.userName);
        txtDateOfBirth = (EditText) findViewById(R.id.userDOB);
        txtEmail = (EditText) findViewById(R.id.userEmail);
        txtPassword = (EditText) findViewById(R.id.userPassward);
        txtConfirmPassword = (EditText) findViewById(R.id.userPasswardConfirm);

        chkConsent = (CheckBox)findViewById(R.id.chkbConsent);

        btnRegister = (Button)findViewById(R.id.btnRegister);


        name = txtName.getText().toString();
        dateOfBirth = txtDateOfBirth.getText().toString();
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        confirmPassword = txtConfirmPassword.getText().toString();



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






         btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        // 1. save data,
                        // 2.validate data
                        




                        Intent intent=new Intent(UserRegister.this,MainActivity.class);
                        startActivity(intent);
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
        if(confirmPassword.equals(password)) {
               txtConfirmPassword.setError(null);

        }else {
            txtConfirmPassword.setError("password not same");
        }

        return valid;
    }

}
