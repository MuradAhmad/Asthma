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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by muradahmad on 14/08/2018.
 */

public class Symptoms extends Fragment {

    private QuestionLibrary questionLibrary = new QuestionLibrary();
    private int severetyQuestionNumber = 0;
    private int frequencyQuestionNumber = 0;
    private int estimationQuestionNumber = 0;

    private Button btn1, btn2, btn3, btn4;

    private TextView txtQuestion;
    private Button btnNext;
    private RadioButton rdStrong, rdMild, rdModerate, rdNotatall, rdButton;
    private RadioGroup rdGroupQuestions;

    private ColorStateList textColorDefaultRb;

    String mild, strong, moderate, notAtAll;


    private List<String> qList;
    private List<String> aList;


    Database dbHandler;
    SQLiteDatabase db;
    Cursor cursor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_symptoms, container, false);


        txtQuestion = (TextView) view.findViewById(R.id.txtQuestion);
        //btnNext = (Button)view.findViewById(R.id.btnNext);

        btn1 = (Button) view.findViewById(R.id.btn1);
        btn2 = (Button) view.findViewById(R.id.btn2);
        btn3 = (Button) view.findViewById(R.id.btn3);
        btn4 = (Button) view.findViewById(R.id.btn4);



/*
        rdGroupQuestions= (RadioGroup) view.findViewById(R.id.rdGroupQuestions);

        rdMild = (RadioButton)view.findViewById(R.id.rdMild);
        rdStrong = (RadioButton)view.findViewById(R.id.rdStrong);
        rdModerate = (RadioButton)view.findViewById(R.id.rdModerate);
        rdNotatall = (RadioButton)view.findViewById(R.id.rdNotatall);


        textColorDefaultRb = rdMild.getTextColors();
      //  RadioButton rbSelected = (RadioButton)view.findViewById(rdGroupQuestions.getCheckedRadioButtonId());
*/


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
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn3.getText().toString());


                updateQuestion();

                Toast.makeText(getContext(),
                        btn3.getText(), Toast.LENGTH_SHORT).show();

            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aList.add(btn4.getText().toString());


                updateQuestion();

                Toast.makeText(getContext(),
                        btn4.getText(), Toast.LENGTH_SHORT).show();

            }
        });






/*
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuestion();
                //saveQuestion();

                // get selected radio button from radioGroup
                *//*int selectedId = rdGroupQuestions.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                rdButton = (RadioButton) view.findViewById(selectedId);


                Toast.makeText(getContext(),
                        rdButton.getText(), Toast.LENGTH_SHORT).show();*//*
             *//*   RadioButton rbSelected = (RadioButton) view.findViewById(rdGroupQuestions.getCheckedRadioButtonId());



                Toast.makeText(getContext(),
                        rdButton.getText(), Toast.LENGTH_SHORT).show();*//*




            }
        });*/


        return view;


    }


    public void updateQuestion() {


        if (severetyQuestionNumber < 4) {


            txtQuestion.setText(questionLibrary.getSeverityQuestion(severetyQuestionNumber));


            btn1.setText(questionLibrary.getSeverityOptions(severetyQuestionNumber, 0));
            btn2.setText(questionLibrary.getSeverityOptions(severetyQuestionNumber, 1));
            btn3.setText(questionLibrary.getSeverityOptions(severetyQuestionNumber, 2));
            btn4.setText(questionLibrary.getSeverityOptions(severetyQuestionNumber, 3));
            severetyQuestionNumber++;


            qList.add(txtQuestion.getText().toString());


        } else if (severetyQuestionNumber > 3 && frequencyQuestionNumber < 3) {


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



/*


            // Instantiate the RequestQueue.
            final RequestQueue queue = Volley.newRequestQueue(getContext());

            String url = "https://co2.awareframework.com:8443";


// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.d("Response is: ", response.substring(0,500));




                        }
                    }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Toast.makeText(getContext(),
                            "Data not sent to server", Toast.LENGTH_SHORT).show();
                }
            }
            ) {


                    @Override
                    protected Map<String, String> getParams ()
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name", "Alif");
                        params.put("domain", "http://itsalif.info");

                        return params;
                    }


                           *//*private JSONObject buildJsonObject() throws JSONException {

                    JSONObject jsonObject = new JSONObject();

                    for (int i = 0; i <= qList.size(); i++) {
                        jsonObject.accumulate(qList.get(i), aList.get(i));
                    }
                    Log.d("Questions and Answers",jsonObject.toString());
                    return jsonObject;
                }
*//*




            };

// Add the request to the RequestQueue.
            queue.add(stringRequest);*/


            sendPostRequest();
            Toast.makeText(getContext(),
                    "Finished", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);


        }


    }




/*

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








            btnNext.setText("Finish");

         */
/*   int index =0;
            for(int i =0; i<= qList.size();i++) {
                Log.d("question:",  qList.get(index) + "answer:" + aList.get(index));

                index++;
            }*//*




        }
        else
         {


             */
/*if(index<= qList.size()) {

                 // save data in database

                 ContentValues values = new ContentValues();
                 values.put(Database.Q1, qList.get(index));
                 values.put(Database.A1, aList.get(index));

                 values.put(Database.reg_timestamp, System.currentTimeMillis());


                 dbHandler.insertSymptomsData(values);
             }*//*


           //  Intent intent = new Intent(getContext(), MainActivity.class);
            // startActivity(intent);
             displayQuestionDatabase();

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


    public void displayQuestionDatabase() {

        int index = 0;
        for (int i = 0; index <= qList.size(); i++) {
            Log.d(" question:  ", qList.get(index) + "  answer:  " + aList.get(index));

            index++;

        }
    }


*/


    private void sendPostRequest() {

        // Instantiate the RequestQueue.
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://co2.awareframework.com:8443/insert";
        // String url = "https://co2.awareframework.com:3306/insert";


        try {

            final JSONObject tempJsonObject = new JSONObject();
            tempJsonObject.put("tableName", "RuuviTag");
            tempJsonObject.put("deviceId", "temporaryId");

            final JSONObject jsonObject = new JSONObject();

            jsonObject.put("tableName", "RuuviTag");
            jsonObject.put("deviceId", "temporaryId");
            jsonObject.put("data", tempJsonObject);
            jsonObject.put("timeStamp", "123456");

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("Response is: ", response.toString());
                            Log.d("Json data: ", jsonObject.toString());

                            Toast.makeText(getContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                            // error
                            Log.d("Error is: ", error.toString());
                            Toast.makeText(getContext(),
                                    "Data not sent to server", Toast.LENGTH_SHORT).show();


                        }
                    }

            ) {    //this is the part, that adds the header to the request
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };


            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        ;


    }
    // Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();


}
