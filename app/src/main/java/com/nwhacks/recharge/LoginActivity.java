package com.nwhacks.recharge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // declare stuff
    private Button loginBtn;
    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // sets header title of activity
        setTitle("Login");

        // create objects
        loginBtn = findViewById(R.id.loginBtn);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        firebaseAuth = firebaseAuth.getInstance();
        progressDialog = new ProgressDialog (this);

        // onclick listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // test if user exists or not
        /*signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open login activity
            }
        });*/
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(email.equals("")) {
            // email is empty
            // error message
            return;
        }

        if(password.equals("")) {
            // password is empty
            // error message
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // when user registration completed
                        if(task.isSuccessful()) {
                            // user successfully registered and logged in
                            // start profile activity

                            progressDialog.dismiss();

                            Intent accountSettingsIntent = new Intent(getApplicationContext(), AccountSettingsActivity.class);

                            startActivity(accountSettingsIntent);
                        } else {
                            // not successful

                            progressDialog.dismiss();
                        }
                    }
                });

    }
}
