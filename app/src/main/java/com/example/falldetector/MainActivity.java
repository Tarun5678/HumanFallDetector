package com.example.falldetector;

import android.content.Intent;
import android.content.SharedPreferences;
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
    EditText name, email ,phone;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    //DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //databaseReference = FirebaseDatabase.getInstance().getReference("sensorData");
        start = (Button) findViewById(R.id.submit);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                String cName = name.getText().toString();
                String email_ = email.getText().toString();
                String mNumber = phone.getText().toString();
                editor.putString("Name", cName);
                editor.putString("email", email_);
                editor.putString("phone",mNumber);
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
                else {
                    editor.apply();
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "saved",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                    Intent mon = new Intent(MainActivity.this, Monitoring.class);
                    startActivity(mon);
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
                startActivity(mon);
            }
        });
    }
}
