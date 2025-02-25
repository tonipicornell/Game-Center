package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InitialBreakout extends AppCompatActivity {

    private Button playBreakout;
    private Button globalStaticsBreakout;
    private Button exitBreakout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_initial_breakout);

        playBreakout = findViewById(R.id.play_breakout);
        globalStaticsBreakout = findViewById(R.id.statics_breakout);
        exitBreakout = findViewById(R.id.exit_breakout);

        playBreakout.setOnClickListener(v -> {
            Intent intent = new Intent(InitialBreakout.this, Breakout.class);
            startActivity(intent);
        });

        // Pulsando en el botón de "Exit Game" sales a la actividad GameCenter
        exitBreakout.setOnClickListener(v -> {
            Intent intent = new Intent(InitialBreakout.this, GameCenter.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}