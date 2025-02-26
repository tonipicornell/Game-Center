package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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

        Intent intent = getIntent();

        String username = intent.getStringExtra("USERNAME");
        int userId = intent.getIntExtra("USER_ID", -1);

        playBreakout.setOnClickListener(v -> {
            Intent breakoutIntent = new Intent(InitialBreakout.this, Breakout.class);
            breakoutIntent.putExtra("USERNAME", username);
            breakoutIntent.putExtra("USER_ID", userId);

            startActivity(breakoutIntent);
        });

        // Pulsando en el botón de "Exit Game" sales a la actividad GameCenter
        exitBreakout.setOnClickListener(v -> {
            Intent exitGameIntent = new Intent(InitialBreakout.this, GameCenter.class);
            exitGameIntent.putExtra("USERNAME", username);
            exitGameIntent.putExtra("USER_ID", userId);
            startActivity(exitGameIntent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}