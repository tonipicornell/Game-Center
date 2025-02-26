package com.example.gamecentertoni;

import android.content.Intent;
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

public class CreateAccount extends AppCompatActivity {

    private DatabaseHelper mDatabaseHelper;

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

        // Iniciar la base de datos:
        mDatabaseHelper = new DatabaseHelper(this);

        personalNameUser = findViewById(R.id.enter_personal_name);
        surnamesUser = findViewById(R.id.enter_surnames);
        dateOfBirthUser = findViewById(R.id.enter_date_of_birth);
        newUsername = findViewById(R.id.create_username);
        newPassword = findViewById(R.id.create_password);
        reEnterPassword = findViewById(R.id.enter_same_password);

        createAccountButton = findViewById(R.id.new_account_button);

        haveAccountText = findViewById(R.id.create_new_account);

        createAccountButton.setOnClickListener(v -> {
            // Validar los inputs:
            if (validarInputs()) {
                // Añadir el usuario a la base de datos:
                String name = personalNameUser.getText().toString().trim();
                String surnames = surnamesUser.getText().toString().trim();
                String dateOfBirth = dateOfBirthUser.getText().toString().trim();
                String username = newUsername.getText().toString().trim();
                String password = newPassword.getText().toString().trim();

                long userId = mDatabaseHelper.addUser(name, surnames, dateOfBirth, username, password);

                if (userId != -1) {
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                    // Cuando se haya creado mandarlo a la actividad de SignIn:
                    Intent intent = new Intent(CreateAccount.this, SignIn.class);
                    startActivity(intent);

                    // Cerrar la actividad actual:
                    finish();
                } else {
                    Toast.makeText(this, "Username already exits, pleasy try another one", Toast.LENGTH_LONG).show();
                }
            }
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

    private boolean validarInputs() {
        // Obtener los valores:
        String name = personalNameUser.getText().toString().trim();
        String surnames = surnamesUser.getText().toString().trim();
        String dateOfBirth = dateOfBirthUser.getText().toString().trim();

        String username = newUsername.getText().toString().trim();
        String password = newPassword.getText().toString().trim();
        String confirmPassword = reEnterPassword.getText().toString().trim();

        // Comprobar que ningun EditText este vacio:
        if (TextUtils.isEmpty(name)) {
            personalNameUser.setError("Please enter your name");
            return false;
        }

        if (TextUtils.isEmpty(surnames)) {
            surnamesUser.setError("Please enter your surnames");
            return false;
        }

        if (TextUtils.isEmpty(dateOfBirth)) {
            dateOfBirthUser.setError("Please enter your date of birth");
            return false;
        }

        if (TextUtils.isEmpty(username)) {
            newUsername.setError("Please enter a username");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            newPassword.setError("Please enter a password");
            return false;
        }

        // All validations passed:
        return true;
    }
}