package com.atividade.mycity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        findViewById(R.id.registerButton).setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.loginButton).setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}