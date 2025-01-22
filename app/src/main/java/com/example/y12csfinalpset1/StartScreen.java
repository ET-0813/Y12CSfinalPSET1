package com.example.y12csfinalpset1;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StartScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        EditText editText = findViewById(R.id.editText);
        Button pressButton = findViewById(R.id.pressButton);

        pressButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String fileName = editText.getText().toString().trim();

                if (fileName.isEmpty()) {
                    Toast.makeText(StartScreen.this, "Please enter a filename", Toast.LENGTH_SHORT).show();
                    return;
                }

                AssetManager assetManager = getAssets();
                try {
                    InputStream inputStream = assetManager.open(fileName);
                    // If the file exists, navigate to MainActivity
                    Intent intent = new Intent(StartScreen.this, MainActivity.class);
                    intent.putExtra("fileName", fileName); // Pass the filename to the next activity
                    startActivity(intent);
                } catch (IOException e) {
                    Toast.makeText( StartScreen.this, "File not found in assets", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}