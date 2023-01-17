package com.example.jeusain;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link diagnosis_details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class diagnosis_details extends Fragment {

    private final View.OnClickListener deleteOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected ArrayList<String> symptoms_list = new ArrayList<String>();
    protected ArrayList<String> diagnosis_list = new ArrayList<String>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public diagnosis_details() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment diagnosis_details.
     */
    // TODO: Rename and change types and number of parameters
    public static diagnosis_details newInstance(String param1, String param2) {
        diagnosis_details fragment = new diagnosis_details();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_diagnosis_details, container, false);
        String diagnosis_information = getArguments().getString("diagnosis_info");
        String sp_name = getArguments().getString("sp_name");
        String key = getArguments().getString("key");
        setDeleteButton(sp_name, key, rootview);
        setDiagnosisInfo(diagnosis_information, rootview);
        setBackArrow(rootview);
        // Inflate the layout for this fragment
        return rootview;
    }

    private void setDeleteButton(String sp_name, String text, View rootview){
        Button delete_btn =  rootview.findViewById(R.id.dh_frag_delete_btn);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;
                SharedPreferences dh_sp = getActivity().getSharedPreferences(sp_name, MODE_PRIVATE);
                SharedPreferences.Editor dh_sp_edit = dh_sp.edit();
                builder.setTitle(R.string.delete_history_fragment);
                // Add the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Log.i("Diagnosis Fragment", "Confirm Deletion");
                        dh_sp_edit.remove(text);
                        dh_sp_edit.commit();
                        getActivity().recreate();
                        getActivity().onBackPressed();
                        Toast.makeText(rootview.getContext(),rootview.getResources().getString(R.string.dh_frag_toast), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Log.i("Diagnosis Fragment", "Cancelled");
                    }
                });
                // Create the AlertDialog
                dialog = builder.create();
                dialog.show();

            }
        });
    }

    private void setDiagnosisInfo(String text, View rootview){
        ListView diagnosis = (ListView) rootview.findViewById(R.id.dh_frag_diag);
        TextView date =  (TextView) rootview.findViewById(R.id.dh_frag_date);
        diagnosis_details.SymptomsAdapter symptomsAdapter = new diagnosis_details.SymptomsAdapter(getContext());
        diagnosis_details.DiagnosisAdapter diagnosisAdapter = new diagnosis_details.DiagnosisAdapter(getContext());
        ListView symptoms = (ListView) rootview.findViewById(R.id.dh_frag_symptom);

        symptoms.setAdapter(symptomsAdapter);
        diagnosis.setAdapter(diagnosisAdapter);
        try {
            JSONObject diagnosis_info = new JSONObject(text);
            for(String entry : diagnosis_info.getString("diagnosis").split(",")) {
                diagnosis_list.add(entry);
            }
            date.setText(diagnosis_info.getString("date"));
            for(String entry : diagnosis_info.getString("symptoms").split(",")) {
                symptoms_list.add(entry);
            }
        }catch (JSONException e){
            Log.w("fragment", "failed to set info");
        }
    }

    private void setBackArrow(View rootview){
        Button back = (Button)  rootview.findViewById(R.id.dh_back_arrow);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private class SymptomsAdapter extends ArrayAdapter<String> {
        protected static final String ACTIVITY_NAME = "SymptomsAdapter";

        public SymptomsAdapter(Context ctx) {
            super(ctx, 0);
            Log.i(ACTIVITY_NAME, "in constructor");
        }

        public int getCount(){
            Log.i(ACTIVITY_NAME, "in onCount()");
            return symptoms_list.size();
        }

        public String getItem(int position) {
            Log.i(ACTIVITY_NAME, "in getItem()");
            return symptoms_list.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            Log.i(ACTIVITY_NAME, "in getView()");
            LayoutInflater inflater = diagnosis_details.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.symptom_list_item, null); ;
            TextView message = (TextView)result.findViewById(R.id.symptom_text);
            message.setText(   getItem(position)  ); // get the string at position
            return result;
        }
    }

    private class DiagnosisAdapter extends ArrayAdapter<String> {
        protected static final String ACTIVITY_NAME = "DiagnosisAdapter";

        public DiagnosisAdapter(Context ctx) {
            super(ctx, 0);
            Log.i(ACTIVITY_NAME, "in constructor");
        }

        public int getCount(){
            Log.i(ACTIVITY_NAME, "in onCount()");
            return diagnosis_list.size();
        }

        public String getItem(int position) {
            Log.i(ACTIVITY_NAME, "in getItem()");
            return diagnosis_list.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            Log.i(ACTIVITY_NAME, "in getView()");
            LayoutInflater inflater = diagnosis_details.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.symptom_list_item, null); ;
            TextView message = (TextView)result.findViewById(R.id.symptom_text);
            message.setText(   getItem(position)  ); // get the string at position
            return result;
        }
    }
}