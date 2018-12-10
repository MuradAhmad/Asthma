package com.ubicomp.murahmad.asthma;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by muradahmad on 10/07/2018.
 */

public class Login extends AppCompatActivity {


    Database handler;
    SQLiteDatabase db;
    Cursor cursor;

    Button btnLogin;
    TextView txtRegister, txtWelcome ;
    private String userName,userPassword;

    AutoCompleteTextView username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnLogin = (Button) findViewById(R.id.btnlogin);
        txtRegister = (TextView)findViewById(R.id.txtregister);
        txtWelcome = (TextView)findViewById(R.id.txtwelcome);
        username = (AutoCompleteTextView)findViewById(R.id.txtUserEmail);
        password = (AutoCompleteTextView)findViewById(R.id.txtUserPassward);

        btnLogin.setText(R.string.login);
        txtWelcome.setText(R.string.welcome);
        txtRegister.setText(R.string.register);
        username.setHint(R.string.email);
        password.setHint(R.string.pasword);


        handler = new Database(getApplicationContext());
        db = handler.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM " + Database.REGISTRATION_TABLE , null);

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
// get values from cursor here


                userName = cursor.getString(cursor.getColumnIndex(Database.USERNAME));
                userPassword = cursor.getString(cursor.getColumnIndex(Database.PASSWORD));


            }
        }
        cursor.close();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals(userName) &&
                        password.getText().toString().equals(userPassword)) {

                    Intent intent=new Intent(Login.this, MainActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();

                }
            }
        });



        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Login.this,UserRegister.class);
                startActivity(intent);
            }
        });


    }
}
