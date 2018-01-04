package com.example.administrator.linebylinetext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LineByLineTextView lineByLineTextView = findViewById(R.id.text);
        lineByLineTextView.bindText("Hello World!\n" +
                "Hello World!\n" +
                "Hello World!\n" +
                "Hello World!\n" +
                "Hello World!\n" +
                "Hello World!\n" +
                "Hello World!\n");

    }
}
