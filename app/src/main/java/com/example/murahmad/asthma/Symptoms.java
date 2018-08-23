package com.example.murahmad.asthma;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muradahmad on 14/08/2018.
 */

public class Symptoms extends Fragment {

    private QuestionLibrary questionLibrary = new QuestionLibrary();
    private int severetyQuestionNumber = 0;
    private int frequencyQuestionNumber = 0;
    private int estimationQuestionNumber = 0;

    private TextView txtQuestion;
    private Button btnNext;
    private RadioButton rdStrong,rdMild,rdModerate,rdNotatall, rdButton;
    private RadioGroup rdGroupQuestions;

    private ColorStateList textColorDefaultRb;

    String mild,strong,moderate,notAtAll;


    private List<String> qList;
    private List<String> aList;


    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_symptoms, container, false);



        txtQuestion = (TextView)view.findViewById(R.id.txtQuestion);
        btnNext = (Button)view.findViewById(R.id.btnNext);

        rdGroupQuestions= (RadioGroup) view.findViewById(R.id.rdGroupQuestions);

        rdMild = (RadioButton)view.findViewById(R.id.rdMild);
        rdStrong = (RadioButton)view.findViewById(R.id.rdStrong);
        rdModerate = (RadioButton)view.findViewById(R.id.rdModerate);
        rdNotatall = (RadioButton)view.findViewById(R.id.rdNotatall);


        textColorDefaultRb = rdMild.getTextColors();
      //  RadioButton rbSelected = (RadioButton)view.findViewById(rdGroupQuestions.getCheckedRadioButtonId());



        dbHandler = new Database(getContext());
        db = dbHandler.getWritableDatabase();

        qList = new ArrayList<String>();
        aList = new ArrayList<String>();


        updateQuestion();




        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuestion();
                //saveQuestion();

                // get selected radio button from radioGroup
                /*int selectedId = rdGroupQuestions.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                rdButton = (RadioButton) view.findViewById(selectedId);


                Toast.makeText(getContext(),
                        rdButton.getText(), Toast.LENGTH_SHORT).show();*/
             /*   RadioButton rbSelected = (RadioButton) view.findViewById(rdGroupQuestions.getCheckedRadioButtonId());



                Toast.makeText(getContext(),
                        rdButton.getText(), Toast.LENGTH_SHORT).show();*/




            }
        });











        return view;


    }


    public void updateQuestion(){


        if(severetyQuestionNumber<4) {

            rdGroupQuestions.clearCheck();

            txtQuestion.setText(questionLibrary.getSeverityQuestion(severetyQuestionNumber));
            rdNotatall.setText(questionLibrary.getSeverityOptions(severetyQuestionNumber,0));
            rdMild.setText(questionLibrary.getSeverityOptions(severetyQuestionNumber,1));
            rdModerate.setText(questionLibrary.getSeverityOptions(severetyQuestionNumber,2));
            rdStrong.setText(questionLibrary.getSeverityOptions(severetyQuestionNumber,3));
            severetyQuestionNumber++;


            qList.add(txtQuestion.getText().toString());


            saveQuestion();




        }
       else if(severetyQuestionNumber>3 && frequencyQuestionNumber<3)  {

            rdGroupQuestions.clearCheck();

            txtQuestion.setText(questionLibrary.getFrequencyQuestion(frequencyQuestionNumber));
            rdNotatall.setText(questionLibrary.getFrequencyOption(0));
            rdMild.setText(questionLibrary.getFrequencyOption(1));
            rdModerate.setText(questionLibrary.getFrequencyOption(2));
            rdStrong.setText(questionLibrary.getFrequencyOption(3));
            frequencyQuestionNumber++;


            qList.add(txtQuestion.getText().toString());
            saveQuestion();

        }
        else if(estimationQuestionNumber<1) {

            rdGroupQuestions.clearCheck();

            txtQuestion.setText(questionLibrary.getEstimationQuestion(estimationQuestionNumber));
            rdNotatall.setText(questionLibrary.getEstimationOption(0));
            rdMild.setText(questionLibrary.getEstimationOption(1));
            rdModerate.setText(questionLibrary.getEstimationOption(2));
            rdStrong.setText(questionLibrary.getEstimationOption(3));
            estimationQuestionNumber++;

            qList.add(txtQuestion.getText().toString());

            saveQuestion();
            btnNext.setText("Finish");

         /*   int index =0;
            for(int i =0; i<= qList.size();i++) {
                Log.d("question:",  qList.get(index) + "answer:" + aList.get(index));

                index++;
            }*/



        }
        else
         {


             /*if(index<= qList.size()) {

                 // save data in database

                 ContentValues values = new ContentValues();
                 values.put(Database.Q1, qList.get(index));
                 values.put(Database.A1, aList.get(index));

                 values.put(Database.reg_timestamp, System.currentTimeMillis());


                 dbHandler.insertSymptomsData(values);
             }*/

             Intent intent = new Intent(getContext(), MainActivity.class);
             startActivity(intent);
        }


    }

    public void saveQuestion() {

        rdGroupQuestions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.rdMild:
                        // switch to fragment 1

                        mild = rdMild.getText().toString();

                        aList.add(mild);

                        Toast.makeText(getActivity(), "Mild Question Saved:" +mild,
                                Toast.LENGTH_LONG).show();

                        break;
                    case R.id.rdModerate:
                        // Fragment 2

                        moderate = rdModerate.getText().toString();

                        aList.add(moderate);

                        Toast.makeText(getActivity(), "Moderate Question Saved:"+moderate,
                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.rdNotatall:
                        // Fragment 2

                        notAtAll = rdNotatall.getText().toString();

                        aList.add(notAtAll);

                        Toast.makeText(getActivity(), "Not at all Question Saved:"+notAtAll,
                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.rdStrong:
                        // Fragment 2

                        strong = rdStrong.getText().toString();

                        aList.add(strong);

                        Toast.makeText(getActivity(), "Strong Question Saved:"+ strong,
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });







    }




}
