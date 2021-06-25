package com.example.aquaalert.Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaalert.MainActivity;
import com.example.aquaalert.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText Email, password;
    private TextView forgot, register;
    private Button SignIn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progress);

        Email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        SignIn = (Button) findViewById(R.id.signin);
        SignIn.setOnClickListener(this);

        forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.signin:
                signIn();
                break;
            case R.id.forgot:
                startActivity(new Intent(this, ForgotPassword.class));
        }
    }

    private void signIn() {
        String email = Email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (email.isEmpty()) {
            Email.setError("Enter Email!");
            Email.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            password.setError("Enter Password!");
            password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please Enter Valid Email!");
            Email.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            password.setError("Min password length should be 6 characters!");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Remember Me
                            //Email Verification
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()) {
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Check your Email to verify your account!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        // ...
                    }
                });
    }
}