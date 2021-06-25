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

import com.example.aquaalert.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText Name, Email, Password;
    private Button Register;
    private TextView Banner;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        Name = (EditText) findViewById(R.id.name);
        Password = (EditText) findViewById(R.id.password);
        Email = (EditText) findViewById(R.id.email);
        Banner = (TextView) findViewById(R.id.banner);
        Banner.setOnClickListener(this);
        Register = (Button) findViewById(R.id.register);
        Register.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.register:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = Email.getText().toString().trim();
        String pass = Password.getText().toString().trim();
        String name = Name.getText().toString().trim();

        if (name.isEmpty()) {
            Name.setError("Enter Name!");
            Name.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            Email.setError("Enter Email!");
            Email.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            Password.setError("Enter Password!");
            Password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please Enter Valid Email!");
            Email.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            Password.setError("Min password length should be 6 characters!");
            Password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(name, email, pass, 0, 0, 30, 0, 0, "2");
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.sendEmailVerification();
                                Toast.makeText(getApplicationContext(), "Check your Email to verify your account!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));
                                finish();
                            } else {
                                Toast.makeText(Register.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}