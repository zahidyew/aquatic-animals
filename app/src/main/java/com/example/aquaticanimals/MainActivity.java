package com.example.aquaticanimals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

import com.example.aquaticanimals.markerAR.AugmentedImageActivity;
import com.example.aquaticanimals.markerlessAR.ViewAnimals;
import com.example.aquaticanimals.quiz.QuizPage;

public class MainActivity extends AppCompatActivity {
    private Button scanButton, viewButton, quizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // lock the screen to be in portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setupButtons();
    }

    private void setupButtons() {
        scanButton = findViewById(R.id.scanButton);
        viewButton = findViewById(R.id.viewButton);
        quizButton = findViewById(R.id.quizButton);

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AugmentedImageActivity.class);
            startActivity(intent);
        });

        viewButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewAnimals.class);
            startActivity(intent);
        });

        quizButton.setOnClickListener(v -> {
            // Toast.makeText(MainActivity.this, "Clicked Quiz", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, QuizPage.class);
            startActivity(intent);
        });
    }
}
