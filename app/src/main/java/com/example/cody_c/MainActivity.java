package com.example.cody_c;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("hello");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}