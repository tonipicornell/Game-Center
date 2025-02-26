package com.example.gamecentertoni;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GlobalStaticGame2048 extends AppCompatActivity {

    private TextView tvHighestScore2048, tvHighestScoreBreakout;
    private DatabaseHelper db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_global_static_game2048);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar variables
        tvHighestScore2048 = findViewById(R.id.tv_highest_score_2048);
        tvHighestScoreBreakout = findViewById(R.id.tv_highest_score_breakout);
        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Obtener ID del usuario logueado
        int userId = sessionManager.getUserId();
        if (userId != -1) {
            // Obtener estadísticas y actualizar la UI
            loadUserStats(userId);
        }
    }

    private void loadUserStats(int userId) {
        int highest2048 = db.getHighest2048Score(userId);
        int highestBreakout = db.getHighestBreakoutScore(userId);

        tvHighestScore2048.setText("Máxima puntuación 2048: " + highest2048);
        tvHighestScoreBreakout.setText("Máxima puntuación Breakout: " + highestBreakout);
    }
}
