package com.example.falldetector;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class OfflineMode extends AppCompatActivity {

    String xAcc, yAcc, zAcc;
    Button safe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_mode);
        Intent intent = getIntent();
        xAcc = intent.getStringExtra("xAcc");
        yAcc = intent.getStringExtra("yAcc");
        zAcc = intent.getStringExtra("zAcc");
        double[] readings = new double[3];
        readings[0] = Double.parseDouble(xAcc);
        readings[1] = Double.parseDouble(yAcc);
        readings[2] = Double.parseDouble(zAcc);
        int predicted = RandomForestClassifier.predict(readings);
        if (predicted == 0) {
            Log.d("Predicted", "Backward-Fall");
        } else if (predicted == 1) {
            Log.d("Predicted", "Fall-on-Knee");
        } else if (predicted == 2) {
            Log.d("Predicted", "Normal-Fall");
        } else if (predicted == 3) {
            Log.d("Predicted", "Side-Fall");

        }
    }
}