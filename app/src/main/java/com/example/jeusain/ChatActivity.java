package com.example.jeusain;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.HmacUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpRequest;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.ClientProtocolException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.CloseableHttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClients;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class ChatActivity extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "ChatWindow";
    protected static final ArrayList<String> messages = new ArrayList<>();
    protected static final ArrayList<String> symptoms = new ArrayList<>();
    protected static final ArrayList<String> patientSymptoms = new ArrayList<>();
    protected static final ArrayList<String> symptomIDs = new ArrayList<>();
    protected static final ArrayList<String> diagnosis = new ArrayList<>();
    protected static final ArrayList<String> accuracy = new ArrayList<>();
    String TOKEN;
    boolean finished = false;
    ChatAdapter messageAdapter;

    //create a class for the api token
    static public class AccessToken {

        public String Token;

        public int ValidThrough;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        messageAdapter = new ChatAdapter( this );

        //Declare Design Elements
        Button sendButton = (Button) findViewById(R.id.sendbutton);
        Button back_button = (Button) findViewById(R.id.chatbot_back_arrow);
        EditText chatText = (EditText) findViewById(R.id.chatText);
        ListView list = (ListView) findViewById(R.id.chatList);

        //Assign chat adapter to the chat list
        list.setAdapter (messageAdapter);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Request the token from the api
        new TokenQuery().execute("https://authservice.priaid.ch/login?");
        while (TOKEN == null) {
        }

        //Request a list of all symptoms from the api
        new SymptomQuery().execute("https://healthservice.priaid.ch/symptoms?token=" + TOKEN + "&language=en-gb&format=xml");

        //Add starting message to chat list
        messages.add("r" + getString(R.string.EntryMessage));
        messageAdapter.notifyDataSetChanged();

        //Send button on click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check to make sure there is no async task on in the background, if there is add a message to the listt
                if (finished == true) {
                    //If there is text to send to the chat bot
                    if(! String.valueOf(chatText.getText()).isEmpty()) {
                        //add it to the chat list
                        messages.add("s" + String.valueOf(chatText.getText()));
                        messageAdapter.notifyDataSetChanged();

                        //If the user requests for Symptoms, display the symptoms
                        if (String.valueOf(chatText.getText()).equals("!Symptoms")) {
                            //add the response to the chat list
                            messages.add("r" + String.join(", ", symptoms));
                            messageAdapter.notifyDataSetChanged();
                            //If the user requests diagnosis, request from api if conditions are met
                        } else if (String.valueOf(chatText.getText()).equals("!Done") && !patientSymptoms.isEmpty()) {
                            diagnosis.clear();
                            new DiagnosisQuery().execute("https://healthservice.priaid.ch/diagnosis?token=" + TOKEN + "&language=en-gb&format=xml&symptoms=[" + String.join(", ", patientSymptoms) + "]&gender=male&year_of_birth=1988");
                            //If user submits a valid symptom add it to their list
                        } else if (symptoms.contains(String.valueOf(chatText.getText()).toLowerCase()) && !patientSymptoms.contains(symptomIDs.get(symptoms.indexOf(String.valueOf(chatText.getText()).toLowerCase())))) {
                            patientSymptoms.add(symptomIDs.get(symptoms.indexOf(String.valueOf(chatText.getText()).toLowerCase())));
                        } else {
                            //add the response to the chat list
                            messages.add("r" + getString(R.string.InvalidResponse));
                            messageAdapter.notifyDataSetChanged();
                        }
                        //Empty the message text field
                        chatText.setText("");
                    }
                }else{
                    messages.add("r" + getString(R.string.StillProcessing));
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    public class ChatAdapter extends ArrayAdapter<String> {

        public ChatAdapter(Context ctx) {
            super(ctx, 0);
        }

        //returns the size of messages
        public int getCount(){
            return messages.size();
        }
        //get the string at the position argument
        public String getItem(int position){
            return messages.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            //Declare the layout inflater
            LayoutInflater inflater = ChatActivity.this.getLayoutInflater();

            //set result to null
            View result = null ;

            if(getItem(position).charAt(0) == 's'){
                //if the message is coming form the user setup incoming chat appropriately
                result = inflater.inflate(R.layout.chat_row_incoming, null);
                TextView message = (TextView) result.findViewById(R.id.message_text);
                message.setText(   getItem(position).substring(1)  );
            }else{
                //if the message is coming form the chat bot setup outgoing chat appropriately
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
                TextView message = (TextView) result.findViewById(R.id.message_text);

                message.setText(   getItem(position).substring(1)  );
            }

            return result;

        }
    }
    //Api call for diagnosis
    public class DiagnosisQuery extends AsyncTask<String, Integer, String> {

        //add message to list and clear patientSymptoms after execute
        @Override
        protected void onPostExecute(String s){
            messages.add("r" + getString(R.string.Diagnosis) +" \n" + String.join("\n", diagnosis));
            messageAdapter.notifyDataSetChanged();

            SharedPreferences current_user = getSharedPreferences("current_user_sp", MODE_PRIVATE);
            String current_user_sp = "sp_" + current_user.getString("user_id", "");
            SharedPreferences dh_sp = getSharedPreferences(current_user_sp, MODE_PRIVATE);

            Date currentTime = Calendar.getInstance().getTime();
            JSONObject diagnosis_json = new JSONObject();
            String[] diagnosis_details = diagnosis.toArray(new String[0]);
            String[] symptoms_list = new String[patientSymptoms.size()];

            for (int i = 0; i < patientSymptoms.size(); i++){
                symptoms_list[i] = symptoms.get(Integer.parseInt(String.valueOf(symptomIDs.indexOf(patientSymptoms.get(i)))));
            }

            try {
                diagnosis_json.put("diagnosis", String.join(",", diagnosis_details));
                diagnosis_json.put("symptoms", String.join(",", symptoms_list));
                diagnosis_json.put("date", currentTime);
            } catch (JSONException e){
                Log.i("main", "tough");
            }

            SharedPreferences.Editor dh_sp_edit = dh_sp.edit();

            dh_sp_edit.putString(currentTime.toString(), diagnosis_json.toString());
            dh_sp_edit.commit();

            patientSymptoms.clear();
            finished = true;
        }

        @Override
        protected String doInBackground(String... urlString) {
            //setup connection to url
            URL url = null;
            boolean isIssue = false;
            try {
                url = new URL(urlString[0]);
                Log.i(ACTIVITY_NAME, urlString[0]);
            } catch (MalformedURLException e) {

            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            try {
                conn.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setDoInput(true);
            try {
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Parse the xml response
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(conn.getInputStream(), null);
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "ArrayOfHealthDiagnosisItem");

                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();

                    //If xml tag is accuracy add accuracy to the list
                    if (name.equals("Accuracy")){
                        String text = parser.nextText();
                        accuracy.add(text);
                        //if on the issue name tag add to diagnosis list
                    }else if(name.equals("Name") && isIssue){
                        String text = parser.nextText();
                        diagnosis.add(text);
                        isIssue = false;

                    }else if(name.equals("Issue")){
                        isIssue = true;
                    }
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return "";

        }
    }
    //api call for symptoms
    public class SymptomQuery extends AsyncTask<String, Integer, String> {

        //when finished change finished to true
        @Override
        protected void onPostExecute(String s){
            finished = true;
        }

        @Override
        protected String doInBackground(String... urlString) {
            //setup connection to URL
            URL url = null;

            try {
                url = new URL(urlString[0]);
                Log.i(ACTIVITY_NAME, urlString[0]);
            } catch (MalformedURLException e) {
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            try {
                conn.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setDoInput(true);
            // Starts the query
            try {
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Parse the xml response
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(conn.getInputStream(), null);
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "ArrayOfNameItem");

                while (parser.next() != XmlPullParser.END_DOCUMENT) {

                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();


                    //if xml tag is name, add the text to the symptoms list
                    if (name.equals("Name")) {
                        String text = parser.nextText();
                        symptoms.add(text.toLowerCase());
                        //if xml tag is ID, add the text to the symptomsID list
                    }else if (name.equals("ID")){
                        String text = parser.nextText();
                        symptomIDs.add(text);
                    }
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return "";

        }
    }
    //Api call for access token
    public class TokenQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urlString) {
            //declare variables for api call
            String api_key = "r5L4E_MYLAURIER_CA_AUT";
            String secret_key = "f8WLo3q2QTr7t9SKe";
            SecretKeySpec key = new SecretKeySpec( secret_key.getBytes(),"HmacMD5");
            String hash = "";


            //create the hash for the Oauth2.0 authorization
            try {
                Mac mac = Mac.getInstance("HmacMD5");
                mac.init(key);

                hash = Base64.getEncoder().encodeToString(mac.doFinal("https://authservice.priaid.ch/login".getBytes()));

                Log.i(ACTIVITY_NAME, hash);

            } catch (NoSuchAlgorithmException e) {
                Log.i(ACTIVITY_NAME, "hi13");
                e.printStackTrace();

            } catch (InvalidKeyException e) {
                Log.i(ACTIVITY_NAME, "hi14");
                e.printStackTrace();

            }

            //Initialize httpPost
            HttpPost httpPost = new HttpPost(urlString[0]);

            //set the Authorization header
            httpPost.setHeader("Authorization", "Bearer " + api_key + ":" + hash);

            //Parse the JSON response and get our access token
            try {
                CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);
                ObjectMapper objectMapper = new ObjectMapper();

                AccessToken accessToken = objectMapper.readValue(response.getEntity().getContent(), AccessToken.class);
                TOKEN = accessToken.Token;
                Log.i(ACTIVITY_NAME, TOKEN);
            }
            catch (ClientProtocolException e) {
                Log.i(ACTIVITY_NAME, "hi11");
                e.printStackTrace();

            } catch (IOException e) {
                Log.i(ACTIVITY_NAME, "hi12");
                e.printStackTrace();
            }

            return "";

        }
    }


}