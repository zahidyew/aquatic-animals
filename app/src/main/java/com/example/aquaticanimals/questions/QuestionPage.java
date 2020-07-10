package com.example.aquaticanimals.questions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.aquaticanimals.R;
import com.example.aquaticanimals.utils.AlertDialogHelper;
import com.example.aquaticanimals.utils.Apis;
import com.example.aquaticanimals.utils.NetworkSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuestionPage extends AppCompatActivity {
    private int questionId, quizId, numOfQues;
    private String ques, choiceA, choiceB, choiceC, choiceD, answer, quizName;
    private JSONObject result;
    private List<Question> questionList;
    private Question question;

    private TextView minsElem, quesElem, choiceElemA, choiceElemB, choiceElemC, choiceElemD;
    private Button nextBtn, backBtn;
    private int sixtySec = 60, minute;
    private CountDownTimer countDownTimer;
    private int num = 1;
    private String[] answerList;
    private int marks;
    private String date, time;
    private String username;
    private AlertDialogHelper alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_page);

        // lock the screen to be in portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        quizId = getIntent().getExtras().getInt("quizId");
        quizName = getIntent().getExtras().getString("quizName");
        numOfQues = getIntent().getExtras().getInt("numOfQues");
        username = getIntent().getExtras().getString("username");
        int timeLimit = getIntent().getExtras().getInt("timeLimit");

        getSupportActionBar().setTitle(quizName);

        questionList = new ArrayList<>();
        answerList = new String[numOfQues];
        alertDialog = new AlertDialogHelper();

        getQuestions();
        startTimer(timeLimit);
        getDateAndTime();
    }

    private void getQuestions() {
        final String getUrl = Apis.GET_LIST_QUESTIONS + quizId;

        // Formulate the request and handle the response.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getUrl, response -> {
            try {
                // Parsing json array response. loop through each json object and get its values
                for (int i = 0; i < response.length(); i++) {
                    result = (JSONObject) response.get(i);

                    questionId = result.getInt("id");
                    ques = result.getString("question");
                    choiceA = result.getString("choiceA");
                    choiceB = result.getString("choiceB");
                    choiceC = result.getString("choiceC");
                    choiceD = result.getString("choiceD");
                    answer = result.getString("answer");

                    // passing the data to the Question obj
                    question = new Question(questionId, ques, choiceA, choiceB, choiceC, choiceD, answer, quizId);
                    questionList.add(question);
                }
                if(questionList.size() > 0)
                    setQuestion();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> {
                    // Handle error
                    Log.d("Error.Response", error.toString());
                });
        // Add a request to the RequestQueue.
        NetworkSingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void startTimer(int time) {
        minsElem = findViewById(R.id.minuteElem);
        int timeInMillis = time * 60 * 1000;
        minute = time - 1;

        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sixtySec -= 1;

                if(sixtySec == -1 && minute != 0) {
                    sixtySec = 59;
                    minute -= 1;
                }
                if(sixtySec < 10) {
                    minsElem.setText("" + minute + ":0" + sixtySec);
                } else {
                    minsElem.setText("" + minute + ":" + sixtySec);
                }

                minsElem.setTextColor(Color.BLUE);
                //String trackTimer = "Timer";
                //Log.i(trackTimer,"" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                minsElem.setText("00:00");
                checkAnswer();
                saveRecordToDB();

                final String msg = "Time's up. Your mark is " + marks;
                alertDialog.buildMsgAndFinish(msg, QuestionPage.this, QuestionPage.this);
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stop timer to prevent memory leak
        stopTimer();
    }

    // cancel the timer
    private void stopTimer() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void setQuestion() {
        displayQuestion();

        nextBtn.setOnClickListener(v -> {
            if(answerList[num-1] == null) {
                answerList[num-1] = "placeholder";
            }

            // if it's not the last question, then display the next question
            if(num < numOfQues) {
                num += 1;
                displayQuestion();
                backBtn.setVisibility(View.VISIBLE);

                // if it's the last question, then change the nextBtn's text from Next to Finish
                if(num == numOfQues) {
                    nextBtn.setText("Finish");
                }
            }
            // if it's the last question, then stop timer, check answers & submit the details to DB
            else {
                nextBtn.setEnabled(false);
                backBtn.setEnabled(false);
                stopTimer();
                checkAnswer();
                saveRecordToDB();

                String TAG = "array: ";
                for(int i=0; i<numOfQues; i++) {
                    Log.i(TAG, answerList[i]);
                }
            }
        });

        backBtn.setOnClickListener(v -> {
            // if it's not the 1st question & last question
            if(num > 1 && num < numOfQues) {
                num -= 1;
                displayQuestion();

                // if it's the 1st question, then make the back button invisible
                if(num == 1) {
                    backBtn.setVisibility(View.INVISIBLE);
                }
            }
            // if it's the last question, then change the nextBtn's text from Finish to Next
            else if(num == numOfQues) {
                num -= 1;
                displayQuestion();
                nextBtn.setText("Next");
            }
        });
    }

    // to display the question one by one
    private void displayQuestion() {
        quesElem = findViewById(R.id.quesElem);
        choiceElemA = findViewById(R.id.choiceElemA);
        choiceElemB = findViewById(R.id.choiceElemB);
        choiceElemC = findViewById(R.id.choiceElemC);
        choiceElemD = findViewById(R.id.choiceElemD);
        nextBtn = findViewById(R.id.nextBtn);
        backBtn = findViewById(R.id.backBtn);

        question = questionList.get(num - 1);
        quesElem.setText(num + ". " + question.getQues());
        choiceElemA.setText("A. " + question.getChoiceA());
        choiceElemB.setText("B. " + question.getChoiceB());

        // check if C & D is empty. Because sometimes its a True & False Question.
        if(question.getChoiceC().equals("")) {
            choiceElemC.setText("");
        }
        else{
            choiceElemC.setText("C. " + question.getChoiceC());
        }
        if(question.getChoiceD().equals("")) {
            choiceElemD.setText("");
        }
        else{
            choiceElemD.setText("D. " + question.getChoiceD());
        }
        resetColor();
        clickableChoices();
        userHasSelectedAns();
    }

    // allow the choice of answers to be clickable and once clicked saves the latest choice to answerList.
    private void clickableChoices() {
        choiceElemA.setOnClickListener(v -> {
            chosenAnswer(choiceElemA, choiceElemB, choiceElemC, choiceElemD);

            final String pickAns = "A";
            answerList[num-1] = pickAns;
        });

        choiceElemB.setOnClickListener(v -> {
            chosenAnswer(choiceElemB, choiceElemA, choiceElemC, choiceElemD);

            final String pickAns = "B";
            answerList[num-1] = pickAns;
        });

        choiceElemC.setOnClickListener(v -> {
            chosenAnswer(choiceElemC, choiceElemA, choiceElemB, choiceElemD);

            final String pickAns = "C";
            answerList[num-1] = pickAns;
        });

        choiceElemD.setOnClickListener(v -> {
            chosenAnswer(choiceElemD, choiceElemA, choiceElemB, choiceElemC);

            final String pickAns = "D";
            answerList[num-1] = pickAns;
        });
    }

    private void resetColor() {
        choiceElemA.setTextColor(Color.BLACK);
        choiceElemB.setTextColor(Color.BLACK);
        choiceElemC.setTextColor(Color.BLACK);
        choiceElemD.setTextColor(Color.BLACK);
    }

    // the chosen/clicked answer will change color
    private void chosenAnswer(TextView chosen, TextView notChosen1, TextView notChosen2, TextView notChosen3) {
        chosen.setTextColor(Color.GREEN);
        notChosen1.setTextColor(Color.BLACK);
        notChosen2.setTextColor(Color.BLACK);
        notChosen3.setTextColor(Color.BLACK);
    }

    private void userHasSelectedAns() {
        if(answerList[num-1] != null) {
            if(answerList[num-1].equals("A")) {
                choiceElemA.setTextColor(Color.GREEN);
            }
            else if(answerList[num-1].equals("B")) {
                choiceElemB.setTextColor(Color.GREEN);
            }
            else if(answerList[num-1].equals("C")) {
                choiceElemC.setTextColor(Color.GREEN);
            }
            else if(answerList[num-1].equals("D")) {
                choiceElemD.setTextColor(Color.GREEN);
            }
        }
    }

    /*private void userHasSelectedAns() {
        if(answerList[num-1] != null) {
            if(answerList[num-1] == "A") {
                choiceElemA.setTextColor(Color.GREEN);
            }
            else if(answerList[num-1] == "B") {
                choiceElemB.setTextColor(Color.GREEN);
            }
            else if(answerList[num-1] == "C") {
                choiceElemC.setTextColor(Color.GREEN);
            }
            else if(answerList[num-1] == "D") {
                choiceElemD.setTextColor(Color.GREEN);
            }
        }
    }*/

    private void checkAnswer() {
        // fyi, in java cannot check string with ==, use .equals() instead
        for(int i = 0; i < numOfQues; i++) {
            question = questionList.get(i);
            if(answerList[i] == null) {
                answerList[i] = "empty";
            } else {
                if(answerList[i].equals(question.getAnswer())) {
                    marks++;
                }
            }
        }
    }

    private void getDateAndTime() {
        Instant instant = Instant.now();
        ZoneId zoneId = ZoneId.of( "Asia/Singapore" );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy,hh:mm a");
        String[] output = zdt.format(formatter.withLocale(Locale.ENGLISH)).split(",");

        date = output[0];
        time = output[1];
    }

    // send the quiz record to the DB via REST API
    private void saveRecordToDB() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, Apis.POST_SAVE_RECORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        if (response.equals("201")) { // saved to DB
                            final String MSG = "All done. Your mark is " + marks;
                            alertDialog.buildMsgAndFinish(MSG, QuestionPage.this, QuestionPage.this);
                        }
                        else if(response.equals("500")) { // error with DB/server
                            final String MSG = "Error.";
                            alertDialog.buildMsgAndFinish(MSG, QuestionPage.this, QuestionPage.this);
                        }
                        else { // something unexpected
                            final String MSG = "Some error here.";
                            alertDialog.buildMsgAndFinish(MSG, QuestionPage.this, QuestionPage.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            // Map & set params to be send in POST request
            protected java.util.Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("mark", Integer.toString(marks));
                params.put("time", time);
                params.put("date", date);
                params.put("quizId", Integer.toString(quizId));

                return params;
            }
        };
        NetworkSingleton.getInstance(QuestionPage.this).addToRequestQueue(postRequest);
    }
}
