package com.example.jeusain;

import android.os.Handler;
import android.service.controls.templates.TemperatureControlTemplate;
import android.view.animation.PathInterpolator;
import android.widget.Button;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PatientRegistrationActivity extends AppCompatActivity {

    private TextView firstname, lastname, enterEmail, enterPassword, age;
    private Button button;
    private Spinner gender;
    private TextView goToLogin;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        enterEmail = findViewById(R.id.enterEmail);
        enterPassword = findViewById(R.id.enterPassword);
        button = findViewById(R.id.button);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.spinner);
        goToLogin = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performValidations();
                Intent goToLogin = new Intent(PatientRegistrationActivity.this, LoginActivity.class);
                startActivity(goToLogin);
            }
        });
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });
    }
    public void openLogin(){
        Intent login = new Intent(PatientRegistrationActivity.this, LoginActivity.class);
        startActivity(login);
    }


    private void performValidations() {
        final String email = enterEmail.getText().toString();
        String password = enterPassword.getText().toString();
        String first_name = firstname.getText().toString();
        String last_name = lastname.getText().toString();
        String patient_age = age.getText().toString();

        if (TextUtils.isEmpty(email)){
            enterEmail.setError("Email is required sign!");
            return;
        }
        if (TextUtils.isEmpty(password)){
            enterPassword.setError("Password is required sign!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
                    reference.setValue(true);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            DatabaseReference insertData = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("email", mAuth.getCurrentUser().getEmail());
                            hashMap.put("firstname", first_name);
                            hashMap.put("lastname", last_name);
                            hashMap.put("age", patient_age);
                            insertData.updateChildren(hashMap);

                            Intent intent = new Intent(PatientRegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);

                }
                else {
                    Toast.makeText(PatientRegistrationActivity.this, "Sign up process failed, please try again "+task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

