package com.example.murahmad.asthma;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by muradahmad on 20/08/2018.
 */

public class Feedback extends Fragment {

    private QuestionLibrary questionLibrary = new QuestionLibrary();
    private int feedbackQuestion = 0;


    private TextView txtFeedback;
    private Button btn1,btn2;


    private List<String> qList;
    private List<String> aList;

    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_feedback, container, false);



        txtFeedback = (TextView)view.findViewById(R.id.txtFeedback);
        btn1 = (Button)view.findViewById(R.id.btn1);
        btn2 = (Button)view.findViewById(R.id.btn2);




        dbHandler = new Database(getContext());
        db = dbHandler.getWritableDatabase();



        qList = new ArrayList<String>();
        aList = new ArrayList<String>();




        updateQuestion();



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn1.getText().toString());


                updateQuestion();


            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn2.getText().toString());


                updateQuestion();


            }
        });



        return view;


    }


    public void updateQuestion(){


        if(feedbackQuestion<1) {


            txtFeedback.setText(questionLibrary.getFeedbackQuestion(feedbackQuestion));
            btn1.setText(questionLibrary.getFeedbackOption(0));
            btn2.setText(questionLibrary.getFeedbackOption(1));

            qList.add(txtFeedback.getText().toString());

            feedbackQuestion++;

        }
        else if(feedbackQuestion<2) {



            txtFeedback.setText(questionLibrary.getFeedbackQuestion(feedbackQuestion));
            btn1.setText(questionLibrary.getFeedbackOption(2));
            btn2.setText(questionLibrary.getFeedbackOption(3));


            qList.add(txtFeedback.getText().toString());

            feedbackQuestion++;

        }
        else {


            saveFeedback();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);


        }





    }




    public void saveFeedback() {
        String time = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss").format(new Date());

        final JSONObject symptomsJsonObject = new JSONObject();
        for (int index = 0; index < qList.size(); index++) {
            try {
                symptomsJsonObject.put(qList.get(index), aList.get(index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        String stringFeedback = symptomsJsonObject.toString();

        ContentValues values = new ContentValues();

        values.put(Database.FEEDBACK, stringFeedback);

        values.put(Database.FEEDBACK_timestamp, time);

        dbHandler.insertFeedbackData(values);

        dbHandler.close();

    }




}
