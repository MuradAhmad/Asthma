package com.example.murahmad.asthma;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_feedback, container, false);



        txtFeedback = (TextView)view.findViewById(R.id.txtFeedback);
        btn1 = (Button)view.findViewById(R.id.btn1);
        btn2 = (Button)view.findViewById(R.id.btn2);







        qList = new ArrayList<String>();
        aList = new ArrayList<String>();




        updateQuestion();



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn1.getText().toString());


                updateQuestion();

                Toast.makeText(getContext(),
                        btn1.getText(), Toast.LENGTH_SHORT).show();

            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn2.getText().toString());


                updateQuestion();

                Toast.makeText(getContext(),
                        btn2.getText(), Toast.LENGTH_SHORT).show();

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

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);


        }





    }




}