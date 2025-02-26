package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InitialGame2048 extends AppCompatActivity {

    private Button playGame2048;
    private Button staticsGlobal2048;
    private Button exitGame2048;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_initial_game2048);

        playGame2048 = findViewById(R.id.play_game2048);
        staticsGlobal2048 = findViewById(R.id.statics_game2048);
        exitGame2048 = findViewById(R.id.exit_game2048);
        Intent intent = getIntent();

        String username = intent.getStringExtra("USERNAME");
        int userId = intent.getIntExtra("USER_ID", -1);

        playGame2048.setOnClickListener(v -> {
            Intent game2048Intent = new Intent(InitialGame2048.this, game2048.class);
            game2048Intent.putExtra("USERNAME", username);
            game2048Intent.putExtra("USER_ID", userId);
            startActivity(game2048Intent);
        });
        // Si pulsas en el botón de "Exit Game" sales a la actividad GameCenter
        exitGame2048.setOnClickListener(v -> {
            Intent exitGameIntent = new Intent(InitialGame2048.this, GameCenter.class);
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