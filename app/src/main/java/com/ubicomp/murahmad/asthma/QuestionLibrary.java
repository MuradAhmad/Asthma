package com.ubicomp.murahmad.asthma;

import android.content.Context;

/**
 * Created by muradahmad on 14/08/2018.
 * Edited:
 * 17.12.2018 - Denzil: code-cleanup
 */

public class QuestionLibrary {

    private Context context;
    private String severitySymptoms[] = new String[4];
    private String severityOptions[] = new String[4];
    private String frequencySymptoms[] = new String[3];
    private String frequencyOptions[] = new String[4];
    private String estimationAsthamaBalance[] = new String[1];
    private String estimationOption[] = new String[4];
    private String feedbackQuestion[] = new String[2];
    private String feedbackOption[] = new String[4];

    public QuestionLibrary(Context c) {
        this.context = c;

        //severity symptoms questions
        severitySymptoms[0] = this.context.getResources().getString(R.string.symptom_question_shortness);
        severitySymptoms[1] = this.context.getResources().getString(R.string.symptom_question_phlegm);
        severitySymptoms[2] = this.context.getResources().getString(R.string.symptom_question_cough);
        severitySymptoms[3] = this.context.getResources().getString(R.string.symptom_question_wheezing);

        // severity symptoms options
        severityOptions[0] = this.context.getResources().getString(R.string.symptom_option_not);
        severityOptions[1] = this.context.getResources().getString(R.string.symptom_option_mild);
        severityOptions[2] = this.context.getResources().getString(R.string.symptom_option_moderate);
        severityOptions[3] = this.context.getResources().getString(R.string.symptom_option_strong);

        frequencySymptoms[0] = this.context.getResources().getString(R.string.symptom_frequency_wakeup);
        frequencySymptoms[1] = this.context.getResources().getString(R.string.symptom_frequency_openeningmeds1);
        frequencySymptoms[2] = this.context.getResources().getString(R.string.symptom_frequency_openeningmeds2);

        frequencyOptions[0] = this.context.getResources().getString(R.string.symptom_option_not);
        frequencyOptions[1] = this.context.getResources().getString(R.string.symptom_frequency_optn_once);
        frequencyOptions[2] = this.context.getResources().getString(R.string.symptom_frequency_optn_times);
        frequencyOptions[3] = this.context.getResources().getString(R.string.symptom_frequency_optn_times1);

        estimationAsthamaBalance[0] = this.context.getResources().getString(R.string.symptom_estimation);

        estimationOption[0] = this.context.getResources().getString(R.string.symptom_option_good);
        estimationOption[1] = this.context.getResources().getString(R.string.symptom_option_more);
        estimationOption[2] = this.context.getResources().getString(R.string.symptom_option_poorly);
        estimationOption[3] = this.context.getResources().getString(R.string.symptom_option_notatall);

        feedbackQuestion[0] = this.context.getResources().getString(R.string.feedback_question1);
        feedbackQuestion[1] = this.context.getResources().getString(R.string.feedback_question2);

        feedbackOption[0] = this.context.getResources().getString(R.string.feedback_option_yes);
        feedbackOption[1] = this.context.getResources().getString(R.string.feedback_option_no);
        feedbackOption[2] = this.context.getResources().getString(R.string.feedback_option_indoor);
        feedbackOption[3] = this.context.getResources().getString(R.string.feedback_option_outdoor);
    }

    public String getSeverityQuestion(int a) {
        String question = severitySymptoms[a];
        return question;
    }

    public String getSeverityOptions(int a) {
        String option = severityOptions[a];
        return option;
    }

    public String getFrequencyQuestion(int a) {
        String question = frequencySymptoms[a];
        return question;
    }

    public String getFrequencyOption(int a) {
        String option = frequencyOptions[a];
        return option;
    }


    public String getEstimationQuestion(int a) {
        String question = estimationAsthamaBalance[a];
        return question;
    }

    public String getEstimationOption(int a) {
        String option = estimationOption[a];
        return option;
    }


    public String getFeedbackQuestion(int a) {
        String question = feedbackQuestion[a];
        return question;
    }

    public String getFeedbackOption(int a) {
        String option = feedbackOption[a];
        return option;
    }
}
