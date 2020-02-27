package com.example.aquaticanimals.questions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
    private int questionId, quizId;
    private String ques, choiceA, choiceB, choiceC, choiceD, answer;
    private JSONObject result;
    private List<Question> questionList;
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_page);

        quizId = getIntent().getExtras().getInt("quizId");

        questionList = new ArrayList<>();

         getQuestions();
         // Toast.makeText(QuestionPage.this, "" + quizId, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), ques + ", " + answer, Toast.LENGTH_SHORT).show();
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
}
