package com.example.falldetector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EmergencyContact extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "EmPreferences";
    EditText emName, emPhone, emEmail;
    Button save;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        emName = (EditText) findViewById(R.id.emName);
        emPhone = (EditText) findViewById(R.id.emPhone);
        emEmail = (EditText) findViewById(R.id.email_);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                String emeName = emName.getText().toString();
                String emePhone = emPhone.getText().toString();
                String emeEmail = emEmail.getText().toString();
                editor.putString("Name", emeName);
                editor.putString("Phone", emePhone);
                editor.putString("email", emeEmail);
                if (emeName.matches("")) {
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "Name field is empty",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                } else if (!emeEmail.matches(emailPattern)) {
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "Email  is invalid",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                } else if (emePhone.matches("")) {
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "Please enter phone number",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                } else {
                    editor.apply();
                    Toast toast_ = Toast.makeText(getApplicationContext(),
                            "saved",
                            Toast.LENGTH_SHORT);
                    toast_.show();
                    Intent mn = new Intent(EmergencyContact.this, HaveAccount.class);
                    startActivity(mn);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Saved",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    Toast toaste = Toast.makeText(getApplicationContext(),
                            "please enter your email and start monitoring",
                            Toast.LENGTH_SHORT);
                    toaste.show();
                }
            }

        });
    }
}
