package com.ubicomp.murahmad.asthma;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by muradahmad on 10/07/2018.
 */

public class UserRegister extends AppCompatActivity {

    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;

    TextView txtDateOfBirth, txtFillForm;

    EditText txtName, txtEmail, txtPassword, txtConfirmPassword;
    CheckBox chkConsent;
    Button btnRegister;

    String uniqueID, name, dateOfBirth, email, password, confirmPassword;


    int year, month, day;
    DatePickerDialog datePickerDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_userregister);

        txtFillForm = (TextView) findViewById(R.id.txtfillform);
        txtName = (EditText) findViewById(R.id.userName);
        txtDateOfBirth = (TextView) findViewById(R.id.userDOB);
        txtEmail = (EditText) findViewById(R.id.userEmail);
        txtPassword = (EditText) findViewById(R.id.userPassward);
        txtConfirmPassword = (EditText) findViewById(R.id.userPasswardConfirm);
        chkConsent = (CheckBox) findViewById(R.id.chkbConsent);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        txtFillForm.setText(R.string.fillform);
        txtName.setHint(R.string.username);
        txtDateOfBirth.setHint(R.string.dob);
        txtEmail.setHint(R.string.email);
        txtPassword.setHint(R.string.pasword);
        txtConfirmPassword.setHint(R.string.conpassword);

        chkConsent.setText(R.string.consent);
        btnRegister.setText(R.string.register);

        chkConsent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Intent intent = new Intent(UserRegister.this, Consent.class);
                    startActivity(intent);
                }
            }
        });

        dbHandler = new Database(getApplicationContext());
        db = dbHandler.getWritableDatabase();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uniqueID = UUID.randomUUID().toString();
                name = txtName.getText().toString();
                dateOfBirth = txtDateOfBirth.getText().toString();
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                confirmPassword = txtConfirmPassword.getText().toString();

                if (validate()) {
                    ContentValues values = new ContentValues();
                    values.put(Database.USERNAME, name);
                    values.put(Database.DOB, dateOfBirth);
                    values.put(Database.EMAIL, email);
                    values.put(Database.PASSWORD, password);
                    values.put(Database.UUID, uniqueID);
                    values.put(Database.reg_timestamp, System.currentTimeMillis());

                    dbHandler.insertRegistrationData(values);
                    db.close();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(UserRegister.this, getString(R.string.feedback_error_form), Toast.LENGTH_LONG).show();
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
    }


    public boolean validate() {
        String name = txtName.getText().toString();
        String dateOfBirth = txtDateOfBirth.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        if (chkConsent.isChecked()
                && !name.isEmpty()
                && !dateOfBirth.isEmpty()
                && (!email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                && !password.isEmpty() && !confirmPassword.isEmpty() && confirmPassword.equals(password))
            return true;

        return false;
    }

}
