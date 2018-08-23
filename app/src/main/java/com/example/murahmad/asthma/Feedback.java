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

/**
 * Created by muradahmad on 20/08/2018.
 */

public class Feedback extends Fragment {

    private QuestionLibrary questionLibrary = new QuestionLibrary();
    private int feedbackQuestion = 0;


    private TextView txtFeedback;
    private Button btnFinish;
    private RadioButton rdYes,rdNo;
    private RadioGroup rdFeedback;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_feedback, container, false);



        txtFeedback = (TextView)view.findViewById(R.id.txtFeedback);
        btnFinish = (Button)view.findViewById(R.id.btnFinish);

        rdFeedback= (RadioGroup) view.findViewById(R.id.rdFeedback);

        rdYes = (RadioButton)view.findViewById(R.id.rdYes);
        rdNo = (RadioButton)view.findViewById(R.id.rdNo);







        updateQuestion();




        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuestion();
                // get selected radio button from radioGroup
                /*int selectedId = rdGroupQuestions.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                rdButton = (RadioButton) view.findViewById(selectedId);


                Toast.makeText(getContext(),
                        rdButton.getText(), Toast.LENGTH_SHORT).show();*/
            }
        });







        return view;


    }


    public void updateQuestion(){


        if(feedbackQuestion<1) {
            rdFeedback.clearCheck();

            txtFeedback.setText(questionLibrary.getFeedbackQuestion(feedbackQuestion));
            rdYes.setText(questionLibrary.getFeedbackOption(0));
            rdNo.setText(questionLibrary.getFeedbackOption(1));
            feedbackQuestion++;

        }
        else if(feedbackQuestion<2) {

            rdFeedback.clearCheck();

            txtFeedback.setText(questionLibrary.getFeedbackQuestion(feedbackQuestion));
            rdYes.setText(questionLibrary.getFeedbackOption(2));
            rdNo.setText(questionLibrary.getFeedbackOption(3));

            feedbackQuestion++;
            btnFinish.setText("Finish");
        }
        else {

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);


        }





    }




}
