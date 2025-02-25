package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameCenter extends AppCompatActivity {

    private TextView welcomeUser;

    private ImageView breakoutImage;
    private ImageView game2048Image;

    private Button activeUsersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_center);

        welcomeUser = findViewById(R.id.title_game_center);

        breakoutImage = findViewById(R.id.breakout_button);
        game2048Image = findViewById(R.id.game2048_button);

        activeUsersButton = findViewById(R.id.see_active_users);

        breakoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(GameCenter.this, InitialBreakout.class);
            startActivity(intent);
        });

        game2048Image.setOnClickListener(v -> {
            Intent intent = new Intent(GameCenter.this, InitialGame2048.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}