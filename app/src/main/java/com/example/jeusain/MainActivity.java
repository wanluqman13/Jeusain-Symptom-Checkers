package com.example.jeusain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;

public class MainActivity extends AppCompatActivity {

    Button choose_patient_account, choose_doctor_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        choose_patient_account = (Button) findViewById(R.id.patient_reg);
        choose_doctor_account = (Button) findViewById(R.id.doctor_reg);

        choose_patient_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPatientRegister();
            }
        });

        choose_doctor_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDoctorRegister();
            }
        });
    }
    public void openPatientRegister() {
        Toast.makeText(MainActivity.this, "Click here", Toast.LENGTH_LONG).show();
        Intent goToPatientRegister = new Intent(getApplicationContext(), PatientRegistrationActivity.class);
        startActivity(goToPatientRegister);
    }
    public void openDoctorRegister() {
            Toast.makeText(MainActivity.this, "Click here", Toast.LENGTH_LONG).show();
        Intent goToDoctorRegister = new Intent(getApplicationContext(), DoctorRegistrationActivity.class);
        startActivity(goToDoctorRegister);
    }
}