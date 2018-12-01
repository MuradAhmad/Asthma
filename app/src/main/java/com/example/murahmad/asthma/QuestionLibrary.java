package com.example.murahmad.asthma;

import android.content.Context;

/**
 * Created by muradahmad on 14/08/2018.
 */

public class QuestionLibrary {

    private Context context;

    public QuestionLibrary(Context c) {
        this.context = c;
    }

    private String severitySymptoms[] = {
            "Shortness of breath",
            "Phlegm production",
            "Cough",
            "Wheezing"

    };
    private String severityOptions[][] = {
            {
            "Not at all",
            "Mild",
            "Moderate",
            "Strong"
            },
            {     "Not at all",
                  "Mild",
                  "Moderate",
                  "Strong"
            } ,
            {    "Not at all",
                 "Mild",
                 "Moderate",
                 "Strong"
                },
            {
                 "Not at all",
                 "Mild",
                 "Moderate",
                 "Strong"
            }


    };


    private String frequencySymptoms [] = {
            "Nocturnal wake ups caused by symptoms",
            "Opening medication of asthma(short-acting)",
            "opening medication of asthma",


    };
    private String frequencyOptions [] = {
             "Not at all",
            "Once",
             "2-3 times",
             "4 or more times"



    };


    private String estimationAsthamaBalance []= {
            "Your own estimation about asthma balance"


    };
       private String estimationOption [] = {

               this.context.getResources().getString(R.string.symptom_good),
               "More or less under control",
               "Poorly under control",
               "Not at all under control"


       } ;


       private String feedbackQuestion [] = {
               "Did your asthma prevent you to act normally in work, school or home / leisure time today ? ",
               "Where are you now? "

       };
       private String feedbackOption [] = {

               "Yes",
               "No",
               "indoor",
               "outdoor"


       };


    public String getSeverityQuestion(int a){
        String question = severitySymptoms[a];
        return question;

    }

    public String getSeverityOptions(int a, int b){
     String option = severityOptions[a][b];

     return option;


    }

    public String getFrequencyQuestion (int a){
        String question = frequencySymptoms[a];
        return question;
    }
    public String getFrequencyOption(int a){
        String option = frequencyOptions[a];
        return option;
    }


    public String getEstimationQuestion(int a){

        String question = estimationAsthamaBalance[a];
        return question;
    }

    public String getEstimationOption (int a) {
        String option = estimationOption[a];
        return option;
    }



    public String getFeedbackQuestion(int a){

        String question = feedbackQuestion[a];
        return question;
    }

    public String getFeedbackOption (int a) {
        String option = feedbackOption[a];
        return option;
    }

}
