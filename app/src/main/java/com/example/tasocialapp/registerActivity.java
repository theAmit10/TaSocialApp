package com.example.tasocialapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tasocialapp.Model.UserModel;
import com.example.tasocialapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class registerActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    // for firebase authentication
    FirebaseAuth auth;

    // for database
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.createUserWithEmailAndPassword(binding.emailET.getText().toString(), binding.passwordET.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel user = new UserModel(binding.nameET.getText().toString(),
                                    binding.professionET.getText().toString(),
                                    binding.emailET.getText().toString(),
                                    binding.passwordET.getText().toString());

                            //getting the unquie id for user
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(user);
                            Toast.makeText(registerActivity.this, "User data saved successfully...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(registerActivity.this, MainActivity.class);
                            startActivity(intent);

                        }else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(registerActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(registerActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent are used to go activity to different activity
                Intent intent = new Intent(registerActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}