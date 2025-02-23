package com.example.gamecentertoni;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private TextView titleGame;
    private ImageView loadingGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        titleGame = findViewById(R.id.title_game);
        loadingGif = findViewById(R.id.gif_loading);
        Glide.with(this).asGif().load(R.drawable.loading).into(loadingGif);

        startAnimation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startAnimation() {
        Animation animationFirst = AnimationUtils.loadAnimation(this, R.anim.animation_1_title);
        Animation animationSecond = AnimationUtils.loadAnimation(this, R.anim.animation_2_title);

        titleGame.startAnimation(animationFirst);

        // Al cabo de 3 segundos, iniciamos la segunda animación.
        // Utilizamos un handler.
        new Handler().postDelayed(() -> titleGame.startAnimation(animationSecond), 3000);
    }
}