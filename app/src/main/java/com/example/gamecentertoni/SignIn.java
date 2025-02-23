package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignIn extends AppCompatActivity {

    private EditText enterUser;
    private EditText enterPassword;

    private Button signInButton;

    private TextView createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        enterUser = findViewById(R.id.enter_username);
        enterPassword = findViewById(R.id.enter_password);
        signInButton = findViewById(R.id.sign_in_button);
        createAccount = findViewById(R.id.create_new_account);

        signInButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, GameCenter.class);
            startActivity(intent);
        });

        // Si pulsas encima del texto te manda a la actividad CreateAccount
        createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, CreateAccount.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}