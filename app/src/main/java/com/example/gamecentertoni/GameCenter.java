package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        Intent intentOnCreate = getIntent();
        String username = getIntent().getStringExtra("USERNAME");
        int userId = intentOnCreate.getIntExtra("USER_ID", -1);

        if (username != null) {
            welcomeUser.setText("Welcome \n " + username + "!");
        }

        breakoutImage = findViewById(R.id.breakout_button);
        game2048Image = findViewById(R.id.game2048_button);

        activeUsersButton = findViewById(R.id.see_active_users);

        breakoutImage.setOnClickListener(v -> {
            Intent breakoutIntent = new Intent(GameCenter.this, InitialBreakout.class);
            breakoutIntent.putExtra("USERNAME", username); // Pasamos el usuario
            breakoutIntent.putExtra("USER_ID", userId);
            startActivity(breakoutIntent);
        });

        game2048Image.setOnClickListener(v -> {
            Intent game2048Intent = new Intent(GameCenter.this, InitialGame2048.class);
            game2048Intent.putExtra("USERNAME", username);
            game2048Intent.putExtra("USER_ID", userId);
            startActivity(game2048Intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}