package com.example.jeusain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DoctorRegistrationActivity extends AppCompatActivity {

    private TextView goToLogin;
    private Button doctorRegisterButton;
    private EditText doctor_name, university, placeofpractice, medicalregistrationnumber, doctor_email, doctor_password;

    //firebase initialisation

    private FirebaseAuth mAuth;
    private FirebaseAuth mUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);

        doctorRegisterButton = findViewById(R.id.doctorRegisterButton);
        doctor_name = findViewById(R.id.doctorName);
        university = findViewById(R.id.graduated_university);
        placeofpractice = findViewById(R.id.place_of_practice);
        medicalregistrationnumber = findViewById(R.id.medical_registration_number);
        doctor_email = findViewById(R.id.doctorEmail);
        doctor_password = findViewById(R.id.doctorPassword);
        goToLogin = findViewById(R.id.loginFromDoctorRegistration);

        Toast.makeText(this, "Click here", Toast.LENGTH_LONG).show();


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),DoctorProfileActivity.class));
            finish();
        }

        doctorRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performValidations();
                Intent goToLogin = new Intent(DoctorRegistrationActivity.this, LoginActivity.class);
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
        Intent login = new Intent(DoctorRegistrationActivity.this, LoginActivity.class);
        startActivity(login);
    }

    private void performValidations(){

        String getDoctorname = doctor_name.getText().toString();
        String getUniversity = university.getText().toString();
        String getPlaceofPractice = placeofpractice.getText().toString();
        String getMedicalregistrationnumber = medicalregistrationnumber.getText().toString();
        String getDoctoremail = doctor_email.getText().toString();
        String getDoctorpassword = doctor_password.getText().toString();

        if (TextUtils.isEmpty(getDoctorname)){
            doctor_name.setError("Name is required!");
            return;
        }

        if (TextUtils.isEmpty(getUniversity)){
            university.setError("University is required!");
            return;
        }

        if (TextUtils.isEmpty(getPlaceofPractice)){
            placeofpractice.setError("Place of Practice is required!");
            return;
        }

        if (TextUtils.isEmpty(getMedicalregistrationnumber)){
            medicalregistrationnumber.setError("Medical registration number is required!");
            return;
        }

        if (TextUtils.isEmpty(getDoctoremail)){
            doctor_email.setError("Email is required!");
            return;
        }

        if (TextUtils.isEmpty(getDoctorpassword)){
            doctor_password.setError("Password is required!");
            return;
        }

        if (doctor_password.length()<8){
            doctor_password.setError("Password must be 8 or more characters!");
            return;
        }

        //register the user in firebase

        mAuth.createUserWithEmailAndPassword(getDoctoremail, getDoctorpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                            hashMap.put("name", getDoctorname);
                            hashMap.put("place of practice", getPlaceofPractice);
                            hashMap.put("medical registration number", getMedicalregistrationnumber);
                            insertData.updateChildren(hashMap);

                            Intent intent = new Intent(DoctorRegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);

                    Toast.makeText(DoctorRegistrationActivity.this, "User Created!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),DoctorProfileActivity.class));
                }
                else{
                    Toast.makeText(DoctorRegistrationActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}