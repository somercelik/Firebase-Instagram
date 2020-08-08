package com.somercelik.firebaseinstagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.somercelik.firebaseinstagram.R;

public class SignInUpActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);

        firebaseAuth = FirebaseAuth.getInstance();          //Authentication objesi initialize edildi.
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent signInIntent = new Intent(SignInUpActivity.this, FeedActivity.class);
            startActivity(signInIntent);
            finish();
        }
    }

    public void signInClicked(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.contains("@") && email.contains(".") && !password.equals("")) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent signInIntent = new Intent(SignInUpActivity.this, FeedActivity.class);
                    startActivity(signInIntent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignInUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();

                }
            });
        } else if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(SignInUpActivity.this, "Please enter a valid e-mail address!", Toast.LENGTH_LONG).show();
        } else if (password.equals("")) {
            Toast.makeText(SignInUpActivity.this, "Please enter enter a password!", Toast.LENGTH_LONG).show();
        }
    }

    public void signUpClicked(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.contains("@") && email.contains(".") && !password.equals("")) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {      //Giriş başarılıysa
                    Toast.makeText(SignInUpActivity.this, "User created succesfully!", Toast.LENGTH_LONG).show();
                    Intent signUpIntent = new Intent(SignInUpActivity.this, FeedActivity.class);
                    startActivity(signUpIntent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {       //Giriş başarısızsa
                    Toast.makeText(SignInUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(SignInUpActivity.this, "Please enter a valid e-mail address!", Toast.LENGTH_LONG).show();
        } else if (password.equals("")) {
            Toast.makeText(SignInUpActivity.this, "Please enter enter a password!", Toast.LENGTH_LONG).show();
        }

    }
}