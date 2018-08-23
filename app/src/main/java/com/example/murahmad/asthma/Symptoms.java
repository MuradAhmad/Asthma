package com.example.murahmad.asthma;

import android.content.Intent;
import android.content.res.ColorStateList;
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




        updateQuestion();




        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuestion();
                // get selected radio button from radioGroup
                /*int selectedId = rdGroupQuestions.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                rdButton = (RadioButton) view.findViewById(selectedId);


                Toast.makeText(getContext(),
                        rdButton.getText(), Toast.LENGTH_SHORT).show();*/
             /*   RadioButton rbSelected = (RadioButton) view.findViewById(rdGroupQuestions.getCheckedRadioButtonId());



                Toast.makeText(getContext(),
                        rdButton.getText(), Toast.LENGTH_SHORT).show();*/

                rdGroupQuestions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // checkedId is the RadioButton selected

                        switch(checkedId) {
                            case R.id.rdMild:
                                // switch to fragment 1

                                Toast.makeText(getActivity(), "Mild Question Saved!",
                                        Toast.LENGTH_LONG).show();

                                break;
                            case R.id.rdModerate:
                                // Fragment 2

                                Toast.makeText(getActivity(), "Moderate Question Saved!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case R.id.rdNotatall:
                                // Fragment 2

                                Toast.makeText(getActivity(), "Not at all Question Saved!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case R.id.rdStrong:
                                // Fragment 2

                                Toast.makeText(getActivity(), "Strong Question Saved!",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });




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

            if(rdNotatall.isChecked() || rdMild.isChecked()||rdStrong.isChecked()||rdModerate.isChecked()) {



            }






        }
       else if(severetyQuestionNumber>3 && frequencyQuestionNumber<3)  {

            rdGroupQuestions.clearCheck();

            txtQuestion.setText(questionLibrary.getFrequencyQuestion(frequencyQuestionNumber));
            rdNotatall.setText(questionLibrary.getFrequencyOption(0));
            rdMild.setText(questionLibrary.getFrequencyOption(1));
            rdModerate.setText(questionLibrary.getFrequencyOption(2));
            rdStrong.setText(questionLibrary.getFrequencyOption(3));
            frequencyQuestionNumber++;


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


            saveQuestion();

        }
        else
         {
             Toast.makeText(getActivity(), "Done!",
                     Toast.LENGTH_LONG).show();

           // Intent intent = new Intent(getContext(), Feedback.class);
           // startActivity(intent);
        }


    }

    public void saveQuestion() {




        Toast.makeText(getActivity(), "Question Saved!",
                Toast.LENGTH_LONG).show();


    }




}
