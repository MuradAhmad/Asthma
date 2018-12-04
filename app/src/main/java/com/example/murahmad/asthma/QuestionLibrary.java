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
    public QuestionLibrary() {

    }


    private String severitySymptoms[] = {
            this.context.getResources().getString(R.string.symptom_question_shortness),
            this.context.getResources().getString(R.string.symptom_question_phlegm),
            this.context.getResources().getString(R.string.symptom_question_cough),
            this.context.getResources().getString(R.string.symptom_question_wheezing)

    };
    private String severityOptions[][] = {
            {
                    this.context.getResources().getString(R.string.symptom_option_not),
                    this.context.getResources().getString(R.string.symptom_option_mild),
                    this.context.getResources().getString(R.string.symptom_option_moderate),
                    this.context.getResources().getString(R.string.symptom_option_strong)


            },
            {
                    this.context.getResources().getString(R.string.symptom_option_not),
                    this.context.getResources().getString(R.string.symptom_option_mild),
                    this.context.getResources().getString(R.string.symptom_option_moderate),
                    this.context.getResources().getString(R.string.symptom_option_strong)
            } ,
            {
                    this.context.getResources().getString(R.string.symptom_option_not),
                    this.context.getResources().getString(R.string.symptom_option_mild),
                    this.context.getResources().getString(R.string.symptom_option_moderate),
                    this.context.getResources().getString(R.string.symptom_option_strong)
                },
            {
                    this.context.getResources().getString(R.string.symptom_option_not),
                    this.context.getResources().getString(R.string.symptom_option_mild),
                    this.context.getResources().getString(R.string.symptom_option_moderate),
                    this.context.getResources().getString(R.string.symptom_option_strong)
            }


    };


    private String frequencySymptoms [] = {
            this.context.getResources().getString(R.string.symptom_frequency_wakeup),
            this.context.getResources().getString(R.string.symptom_frequency_openeningmeds1),
            this.context.getResources().getString(R.string.symptom_frequency_openeningmeds2)

    };
    private String frequencyOptions [] = {
            this.context.getResources().getString(R.string.symptom_option_not),
            this.context.getResources().getString(R.string.symptom_frequency_optn_once),
            this.context.getResources().getString(R.string.symptom_frequency_optn_times),
            this.context.getResources().getString(R.string.symptom_frequency_optn_times1)

    };


    private String estimationAsthamaBalance []= {
            this.context.getResources().getString(R.string.symptom_estimation)


    };
       private String estimationOption [] = {
               this.context.getResources().getString(R.string.symptom_option_good),
               this.context.getResources().getString(R.string.symptom_option_more),
               this.context.getResources().getString(R.string.symptom_option_poorly),
               this.context.getResources().getString(R.string.symptom_option_notatall)



       } ;


       private String feedbackQuestion [] = {
               this.context.getResources().getString(R.string.feedback_question1),
               this.context.getResources().getString(R.string.feedback_question2)

       };
       private String feedbackOption [] = {
               this.context.getResources().getString(R.string.feedback_option_yes),
               this.context.getResources().getString(R.string.feedback_option_no),
               this.context.getResources().getString(R.string.feedback_option_indoor),
               this.context.getResources().getString(R.string.feedback_option_outdoor)
             

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
