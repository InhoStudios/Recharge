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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // declare stuff
    private Button loginBtn;
    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private boolean hasLogin = false;
    private boolean hasLogin2 = true;

    //private FirebaseUser user;

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
        //user = FirebaseAuth.getInstance().getCurrentUser();

        // if user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {

            Toast toast = Toast.makeText(getApplicationContext(), "You are logged in", Toast.LENGTH_LONG);
            toast.show();

            // start profile activity
            Intent accountSettingsIntent = new Intent(getApplicationContext(), AccountSettingsActivity.class);
            startActivity(accountSettingsIntent);
        }

        // onclick listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if user exists
                loginUser();

                // if user doesnt exist
                //if (hasLogin == false) {
                    registerUser();
                //}

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

    private void loginUser() {
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

        progressDialog.setMessage("Logging in user...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            hasLogin = true;

                            Toast toast = Toast.makeText(getApplicationContext(), "Logged in user", Toast.LENGTH_LONG);
                            toast.show();

                            progressDialog.dismiss();

                            // start profile activity
                            Intent accountSettingsIntent = new Intent(getApplicationContext(), AccountSettingsActivity.class);
                            startActivity(accountSettingsIntent);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "No login found, registering user", Toast.LENGTH_LONG);
                            toast.show();

                            //hasLogin2 = false;
                            //hasLogin = false;

                            //return;
                            progressDialog.dismiss();
                        }
                    }
                });
        /*if (hasLogin2 == true) {
            hasLogin = true;
        }*/
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

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // when user registration completed
                        if(task.isSuccessful()) {
                            // user successfully registered and logged in

                            progressDialog.dismiss();

                            Toast toast = Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_LONG);
                            toast.show();

                            // start profile activity
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
