package com.example.jeusain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "HistoryActivity";
    protected ArrayList<String> diagnosis_title = new ArrayList<String>();
    protected ArrayList<String> diagnosis_info = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_new);

        // initializing items
        HistoryAdapter diagnosisAdapter = new HistoryAdapter(this);
        ListView history_listView = findViewById(R.id.history_listView);
        Button clear_hist = findViewById(R.id.clear_hist_btn);
        Button back = findViewById(R.id.dh_back_arrow);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set adapter
        history_listView.setAdapter(diagnosisAdapter);

        //initialize sharedpreferences
        SharedPreferences current_user = getSharedPreferences("current_user_sp", MODE_PRIVATE);
        String current_user_sp = "sp_" + current_user.getString("user_id", "");
        SharedPreferences dh_sp = getSharedPreferences(current_user_sp, MODE_PRIVATE);

        // map containing all shared preference keys
        Map<String, ?> keys = dh_sp.getAll();

        // if no keys (no saved data) then set text to "No Previous Diagnosis",
        // else, loop through keys
        if (keys.size() == 0) {
            diagnosis_title.add("No Previous Diagnosis");
            diagnosisAdapter.notifyDataSetChanged(); //this restarts the process of getCount()/
        } else {
            for (Map.Entry<String, ?> entry : keys.entrySet()) {

                diagnosis_title.add(entry.getKey());
                diagnosis_info.add(entry.getValue().toString());
                diagnosisAdapter.notifyDataSetChanged(); //this restarts the process of getCount()/

                history_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        keys.entrySet();
                        Log.i("this", diagnosis_info.get(i));
                        Bundle bundle = new Bundle();
                        bundle.putString("diagnosis_info", diagnosis_info.get(i));
                        bundle.putString("sp_name", current_user_sp);
                        bundle.putString("key", diagnosis_title.get(i));
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                                .beginTransaction();
                        Fragment profileFragment = new diagnosis_details();//the fragment you want to show
                        profileFragment.setArguments(bundle);
                        fragmentTransaction
                                .replace(R.id.layoutToBeReplacedWithFragmentInMenu, profileFragment);//R.id.content_frame is the layout you want to replace
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }
        }

        clear_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;
                SharedPreferences.Editor dh_sp_edit = dh_sp.edit();
                builder.setTitle("Are you sure you want to clear your diagnosis history?");
                // Add the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Log.i("Diagnosis History", "Confirm Deletion");
                        dh_sp_edit.clear();
                        dh_sp_edit.commit();
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(getApplicationContext(), "History Cleared", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Log.i("Diagnosis History", "Cancelled");
                    }
                });
                // Create the AlertDialog
                dialog = builder.create();
                dialog.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    private class HistoryAdapter extends ArrayAdapter<String> {
        protected static final String ACTIVITY_NAME = "HistoryAdapter";

        public HistoryAdapter(Context ctx) {
            super(ctx, 0);
            Log.i(ACTIVITY_NAME, "in constructor");
        }

        public int getCount(){
            Log.i(ACTIVITY_NAME, "in onCount()");
            return diagnosis_title.size();
        }

        public String getItem(int position) {
            Log.i(ACTIVITY_NAME, "in getItem()");
            return diagnosis_title.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            Log.i(ACTIVITY_NAME, "in getView()");
            LayoutInflater inflater = HistoryActivity.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.history_diagnosis_item, null); ;
            TextView message = (TextView)result.findViewById(R.id.diagnosis_text);
            message.setText(   getItem(position)  ); // get the string at position
            return result;
        }
    }
}