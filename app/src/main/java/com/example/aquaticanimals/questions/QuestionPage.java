package com.example.aquaticanimals.questions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.aquaticanimals.R;
import com.example.aquaticanimals.utils.Apis;
import com.example.aquaticanimals.utils.NetworkSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionPage extends AppCompatActivity {
    private int questionId, quizId, numOfQues, timeLimit;
    private String ques, choiceA, choiceB, choiceC, choiceD, answer, quizName;
    private JSONObject result;
    private List<Question> questionList;
    private Question question;

    private TextView minsElem, secsElem;
    private int sixtySec = 60, minute;
    private CountDownTimer countDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_page);

        // lock the screen to be in portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        quizId = getIntent().getExtras().getInt("quizId");
        quizName = getIntent().getExtras().getString("quizName");
        numOfQues = getIntent().getExtras().getInt("numOfQues");
        timeLimit = getIntent().getExtras().getInt("timeLimit");

        questionList = new ArrayList<>();

        getQuestions();
        // Toast.makeText(QuestionPage.this, "" + quizId + ", " + quizName + ", " + numOfQues + ", " + timeLimit, Toast.LENGTH_SHORT).show();

        startTimer(timeLimit);
    }

    private void getQuestions() {
        final String getUrl = Apis.GET_LIST_QUESTIONS + quizId;

        // Toast.makeText(QuestionPage.this, "" + quizId, Toast.LENGTH_SHORT).show();

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
                // adapter.notifyDataSetChanged();
                // Toast.makeText(getApplicationContext(), ques + ", " + answer, Toast.LENGTH_SHORT).show();
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

    void startTimer(int time) {
        minsElem = findViewById(R.id.minuteElem);
        //secsElem = findViewById(R.id.secondsElem);

        int timeInMillis = time * 60 * 1000;
        minute = time - 1;

        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sixtySec -= 1;

                if(sixtySec == 0 && minute != 0) {
                    sixtySec = 60;
                    minute -= 1;
                }

                minsElem.setText("" + minute + ":" + sixtySec);
                //secsElem.setText(":" + sixtySec);
                String trackTimer = "Timer";
                Log.i(trackTimer,"" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                minsElem.setText("00:00");
                //secsElem.setText("00");
                // show prompt here to inform times up
                // stop the quiz.
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
    void stopTimer() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}
