package com.example.aquaticanimals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.aquaticanimals.quiz.QuizPage;

public class MainActivity extends AppCompatActivity {
    private Button scanButton, viewButton, quizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtons();
    }

    private void setupButtons() {
        scanButton = findViewById(R.id.scanButton);
        viewButton = findViewById(R.id.viewButton);
        quizButton = findViewById(R.id.quizButton);

        scanButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Clicked scan", Toast.LENGTH_SHORT).show();
            /* Redirect to AR page using
            Intent intent = new Intent(MainActivity.this, ClassName.class);
            startActivity(intent);*/
        });

        viewButton.setOnClickListener(v -> {
            // Toast.makeText(MainActivity.this, "Clicked View", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, ViewAnimals.class);
            startActivity(intent);
        });

        quizButton.setOnClickListener(v -> {
            // Toast.makeText(MainActivity.this, "Clicked Quiz", Toast.LENGTH_SHORT).show();
            // Redirect to Quiz page using
            Intent intent = new Intent(MainActivity.this, QuizPage.class);
            startActivity(intent);
        });
    }

}
