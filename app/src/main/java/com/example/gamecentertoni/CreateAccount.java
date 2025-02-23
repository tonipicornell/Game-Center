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

public class CreateAccount extends AppCompatActivity {

    private EditText personalNameUser;
    private EditText surnamesUser;
    private EditText dateOfBirthUser;
    private EditText newUsername;
    private EditText newPassword;
    private EditText reEnterPassword;

    private Button createAccountButton;

    private TextView haveAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

        personalNameUser = findViewById(R.id.enter_personal_name);
        surnamesUser = findViewById(R.id.enter_surnames);
        dateOfBirthUser = findViewById(R.id.enter_date_of_birth);
        newUsername = findViewById(R.id.create_username);
        newPassword = findViewById(R.id.create_password);
        reEnterPassword = findViewById(R.id.enter_same_password);

        createAccountButton = findViewById(R.id.new_account_button);

        haveAccountText = findViewById(R.id.create_new_account);

        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccount.this, SignIn.class);
            startActivity(intent);
        });

        haveAccountText.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccount.this, SignIn.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}