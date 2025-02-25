package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class VistaWin extends AppCompatActivity {

    private TextView puntuationGame;
    private TextView timeGame;

    private Button exitGame;
    private Button newGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vista_win);

        // Conseguir los putExtra del intent:
        Intent intent = getIntent();
        int puntuation = intent.getIntExtra("puntuacion", 0);
        String time = intent.getStringExtra("tiempo");

        // Referencia a los text view:
        puntuationGame = findViewById(R.id.score_win);
        timeGame = findViewById(R.id.total_time);

        // Referencia a los buttons:
        exitGame = findViewById(R.id.exit_game_button);
        newGame = findViewById(R.id.new_game_button);

        // Actualizar textos:
        puntuationGame.setText("   Score \n    " + puntuation);
        timeGame.setText("    Time \n    " + time);

        // Configuración al presionar el boton de "Exit Game":
        exitGame.setOnClickListener(view -> {
            // Creo un intent para salir de la actividad actual:
            finish();
        });

        // Configuración al presionar el botón de "New Game":
        newGame.setOnClickListener(view -> {
            // Creo un intent para iniciar el MainActivity
            Intent newGameIntent = new Intent(VistaWin.this, Breakout.class);

            // Arrancar la actividad:
            startActivity(newGameIntent);

            // Cierro la actividad actual:
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}