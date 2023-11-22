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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword, fUsername, lUsername, etConfiremPassword;
    Button button;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = findViewById(R.id.reg);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfiremPassword = findViewById(R.id.etConfirmPassword);
        fUsername = findViewById(R.id.first_name);
        lUsername = findViewById(R.id.last_name);
        progressBar = findViewById(R.id.pbar);
        progressBar.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String firstName = fUsername.getText().toString();
                String lastName = lUsername.getText().toString();
                String confirmPassword = etConfiremPassword.getText().toString();
                if(TextUtils.isEmpty(firstName)){
                    fUsername.setError("First Name is required");
                    return;
                }

                if(TextUtils.isEmpty(lastName)){
                    lUsername.setError("Last Name is required");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    etPassword.setError("Password Required");
                    return;
                }
                if(password.length()<6){
                    etPassword.setError("Password must be longer than 6 characters");
                    return;
                }
                if(!(password.equals(confirmPassword))){
                    etPassword.setError("Password do not match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                // Creating a new user
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentreference = fstore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("FirstName",firstName);
                            user.put("LastName",lastName);
                            user.put("email",email);

                            documentreference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Registration Successfull",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this,LoginPage.class);
                                    startActivity(intent);
                                }
                            });

                        }
                        else{
                            Toast.makeText(MainActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                });
            }
        });


    }
}