package com.example.aquaticanimals.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
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

public class QuizPage extends AppCompatActivity {
    private JSONObject result;
    private String quizName, date, time;
    private int quizId, numOfQues, timeLimit;

    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private List<Quiz> quizList;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        // lock the screen to be in portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        getSupportActionBar().setTitle("Quiz");

        recyclerView = findViewById(R.id.recycler_view);
        quizList = new ArrayList<>();
        adapter = new QuizAdapter(this, quizList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getQuizzes();
    }

    private void getQuizzes() {
        // Formulate the request and handle the response.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Apis.GET_LIST_QUIZ, response -> {
            try {
                // Parsing json array response. loop through each json object and get its values
                for (int i = 0; i < response.length(); i++) {
                    result = (JSONObject) response.get(i);

                    quizId = result.getInt("quizId");
                    quizName = result.getString("quizName");
                    date = result.getString("date");
                    time = result.getString("time");
                    numOfQues = result.getInt("numOfQues");
                    timeLimit = result.getInt("timeLimit");

                    // passing the data to the Quiz obj
                    quiz = new Quiz(quizId, quizName, date, time, numOfQues, timeLimit);
                    quizList.add(quiz);
                }
                adapter.notifyDataSetChanged();
                // Toast.makeText(getApplicationContext(), quizId + quizName + date, Toast.LENGTH_SHORT).show();
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
