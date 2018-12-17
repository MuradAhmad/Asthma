package com.ubicomp.murahmad.asthma;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muradahmad on 14/08/2018.
 */

public class Symptoms extends Fragment {

    private QuestionLibrary questionLibrary; //= new QuestionLibrary();
    private int severityQuestionNumber = 0;
    private int frequencyQuestionNumber = 0;
    private int estimationQuestionNumber = 0;

    private static final int REQUEST_FEEDBACK_FRAGMENT_CODE = 11;

    private Button btn1, btn2, btn3, btn4;

    private TextView txtQuestion, txtQuestionTitle;


    private ColorStateList textColorDefaultRb;

    private List<String> qList;
    private List<String> aList;


    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;

    Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_symptoms, container, false);


        context = this.getContext();

        questionLibrary = new QuestionLibrary(context);

        txtQuestionTitle = (TextView) view.findViewById(R.id.txtQuestiontitle);
        txtQuestionTitle.setText(R.string.symptoms_title);

        txtQuestion = (TextView) view.findViewById(R.id.txtQuestion);


        btn1 = (Button) view.findViewById(R.id.btn1);
        btn2 = (Button) view.findViewById(R.id.btn2);
        btn3 = (Button) view.findViewById(R.id.btn3);
        btn4 = (Button) view.findViewById(R.id.btn4);


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
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn3.getText().toString());

                updateQuestion();

            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn4.getText().toString());

                updateQuestion();

            }
        });

        return view;

    }


    public void updateQuestion() {


        if (severityQuestionNumber < 4) {


            txtQuestion.setText(questionLibrary.getSeverityQuestion(severityQuestionNumber));


            btn1.setText(questionLibrary.getSeverityOptions(0));
            btn2.setText(questionLibrary.getSeverityOptions(1));
            btn3.setText(questionLibrary.getSeverityOptions(2));
            btn4.setText(questionLibrary.getSeverityOptions(3));
            severityQuestionNumber++;


            qList.add(txtQuestion.getText().toString());


        } else if (severityQuestionNumber > 3 && frequencyQuestionNumber < 3) {


            txtQuestion.setText(questionLibrary.getFrequencyQuestion(frequencyQuestionNumber));
            btn1.setText(questionLibrary.getFrequencyOption(0));
            btn2.setText(questionLibrary.getFrequencyOption(1));
            btn3.setText(questionLibrary.getFrequencyOption(2));
            btn4.setText(questionLibrary.getFrequencyOption(3));
            frequencyQuestionNumber++;
            qList.add(txtQuestion.getText().toString());


        } else if (estimationQuestionNumber < 1) {


            txtQuestion.setText(questionLibrary.getEstimationQuestion(estimationQuestionNumber));
            btn1.setText(questionLibrary.getEstimationOption(0));
            btn2.setText(questionLibrary.getEstimationOption(1));
            btn3.setText(questionLibrary.getEstimationOption(2));
            btn4.setText(questionLibrary.getEstimationOption(3));
            estimationQuestionNumber++;

            qList.add(txtQuestion.getText().toString());
            estimationQuestionNumber++;


        } else {

            saveSymptoms();

        }


    }


    public void saveSymptoms() {

        final JSONObject symptomsJsonObject = new JSONObject();
        for (int index = 0; index < qList.size(); index++) {
            try {
                symptomsJsonObject.put(qList.get(index), aList.get(index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String stringSymptoms = symptomsJsonObject.toString();

        // send symptoms data to Feedback class as a string
        Feedback feedback = new Feedback();
        Bundle args = new Bundle();
        args.putString("symptoms", stringSymptoms);
        feedback.setArguments(args);

        //Inflate the fragment Feedback
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, feedback).commit();
    }
}
