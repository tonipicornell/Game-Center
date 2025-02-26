package com.example.gamecentertoni;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignIn extends AppCompatActivity {

    private DatabaseHelper mDatabaseHelper;

    // Shared preferences to store logged-in user
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private EditText enterUser;
    private EditText enterPassword;

    private Button signInButton;

    private TextView createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        mDatabaseHelper = new DatabaseHelper(this);

        enterUser = findViewById(R.id.enter_username);
        enterPassword = findViewById(R.id.enter_password);
        signInButton = findViewById(R.id.sign_in_button);
        createAccount = findViewById(R.id.create_new_account);

        signInButton.setOnClickListener(v -> {
            String username = enterUser.getText().toString().trim();
            String password = enterPassword.getText().toString().trim();

            // Validar inputs:
            if (TextUtils.isEmpty(username)) {
                enterUser.setError("Please enter username");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                enterPassword.setError("Please enter password");
                return;
            }

            // Comprobar si el usuario existe en la base de datos:
            if (mDatabaseHelper.checkUser(username, password)) {
                // Guardar el estado del login
                saveLoginState(username);

                // Ir a la actividad de GameCenter
                navigateToGameCenter();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
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

    private void saveLoginState(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editorPreferences = sharedPreferences.edit();

        editorPreferences.putString(KEY_USERNAME, username);
        editorPreferences.putBoolean(KEY_LOGGED_IN, true);
        editorPreferences.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    private void navigateToGameCenter() {
        Intent intent = new Intent(SignIn.this, GameCenter.class);
        startActivity(intent);

        // Finalizar actividad actual:
        finish();
    }
}