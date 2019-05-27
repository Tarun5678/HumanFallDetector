package com.example.falldetector;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HaveAccount extends AppCompatActivity {
Button changecontact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_account);
        changecontact=(Button)findViewById(R.id.change);
        changecontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chc=new Intent(HaveAccount.this,EmergencyContact.class);
                startActivity(chc);

            }
        });


    }

}
