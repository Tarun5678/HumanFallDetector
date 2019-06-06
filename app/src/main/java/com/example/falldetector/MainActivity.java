package com.example.falldetector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    Button start, offlineMode;
    public static final String MY_PREFS_NAME = "myPreferences";
    EditText name, email ,phone ,ecName,ecPhone,ecEmail;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    boolean connected;
    //DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.submit);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        ecName = (EditText)findViewById(R.id.ecName);
        ecPhone = (EditText) findViewById(R.id.ecPhone);
        ecEmail = (EditText) findViewById(R.id.ecEmail);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;

                }
                else{

                    connected = false;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No Network,please proceed with offline mode",
                        Toast.LENGTH_SHORT);
                toast.show();
                }
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                String cName = name.getText().toString();
                String email_ = email.getText().toString();
                String mNumber = phone.getText().toString();
                String eName = ecName.getText().toString();
                String ePhone = ecPhone.getText().toString();
                String eEmail = ecEmail.getText().toString();
                editor.putString("Name", cName);
                editor.putString("email", email_);
                editor.putString("phone",mNumber);
                editor.putString("E_name", eName);
                editor.putString("E_email", eEmail);
                editor.putString("E_phone",ePhone);
                if (cName.matches("")) {
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "Please enter your name",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                } else if (email_ == "" || !email_.matches(emailPattern)) {
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "Your email  is invalid",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                }else if(mNumber.matches("")){
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "please enter your mobile number",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                }
                else if (eName.matches("")) {
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "Please enter emergency contact your name",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                } else if (eEmail == "" || !email_.matches(emailPattern)) {
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "Enter valid email id",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                }else if(ePhone.matches("")){
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "please provide emergency contact phone number",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                }
                else {
                    editor.apply();
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "saved",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                    Intent mon = new Intent(MainActivity.this, Monitoring.class);
                    mon.putExtra("EmergencyPhone", ePhone);
                    mon.putExtra("EmergencyName", eName);
                    startActivity(mon);
                    Toast.makeText(getApplicationContext(), ePhone,
                            Toast.LENGTH_LONG).show();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "please place your phone in your pocket",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        offlineMode = (Button) findViewById(R.id.offlineMode);
        offlineMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mon = new Intent(MainActivity.this, Monitoring.class);
//                Toast.makeText(getApplicationContext(),  ePhone,
//                        Toast.LENGTH_LONG).show();
//                mon.putExtra("Emergency", ePhone);
                startActivity(mon);
            }
        });
    }
}
