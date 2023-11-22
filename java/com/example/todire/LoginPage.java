package com.example.todire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    Button buttonlogin;
    EditText username, password;
    TextView create_account;
    FirebaseAuth fAuth;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);



        create_account = findViewById(R.id.create_account);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        fAuth = FirebaseAuth.getInstance();
        buttonlogin = findViewById(R.id.login);
        progressBar = findViewById(R.id.pbar2);
        progressBar.setVisibility(View.INVISIBLE);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Email = username.getText().toString();
                String Password = password.getText().toString();

                if(TextUtils.isEmpty(Email)){
                    username.setError("Username is required");
                    return;
                }
                if(TextUtils.isEmpty(Password)){
                    password.setError("Password is required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginPage.this,"Login is Successfull ",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginPage.this,HomePage.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(LoginPage.this,"Login Failed",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }
                });

            }
        });
    }
}