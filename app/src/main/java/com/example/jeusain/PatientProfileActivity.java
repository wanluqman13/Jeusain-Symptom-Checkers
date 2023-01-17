package com.example.jeusain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PatientProfileActivity extends AppCompatActivity {

    private Button checkSymptomButton,TimerButton,DiagnosisHistoryButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        checkSymptomButton = findViewById(R.id.check_symptoms_button);
        TimerButton = findViewById(R.id.timer);
        DiagnosisHistoryButton = findViewById(R.id.diagnosis_history);

        checkSymptomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkSymptomButton = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(checkSymptomButton);
            }
        });

        TimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timerButton = new Intent(getApplicationContext(), TimerActivity.class);
                startActivity(timerButton);
            }
        });

        DiagnosisHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent diagnosisHistoryButton = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(diagnosisHistoryButton);
            }
        });
    }
}
