package com.example.falldetector;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class Monitoring extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    Sensor acclerometer;
    double xAcc, yAcc, zAcc;
    String accX, accY, accZ;
    double pitch, azimuth, roll;
    Button minimize;
    DatabaseReference databaseReference;
    Sensor gyro;
    String azimuth_, pitch_, roll_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        databaseReference = FirebaseDatabase.getInstance().getReference("sensorData");
        minimizeApp();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acclerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, acclerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAcc = event.values[0];
            yAcc = event.values[1];
            zAcc = event.values[2];
            double loAccelerationReader = Math.sqrt(Math.pow(xAcc, 2)
                    + Math.pow(yAcc, 2)
                    + Math.pow(zAcc, 2));
            DecimalFormat precision = new DecimalFormat("0.00");
            double ldAccRound = Double.parseDouble(precision.format(loAccelerationReader));
            if (ldAccRound > 0.3d && ldAccRound < 0.5d) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    azimuth = event.values[0];
                    pitch = event.values[1];
                    roll = event.values[2];
                    Log.d("azimuth", "" + azimuth);
                    Log.d("pitch", "" + pitch);
                    Log.d("roll", "" + roll);
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                azimuth_ = Double.toString(azimuth);
                pitch_ = Double.toString(pitch);
                roll_ = Double.toString(roll);
                accX = Double.toString(xAcc);
                accY = Double.toString(yAcc);
                accZ = Double.toString(zAcc);

                String id = databaseReference.push().getKey();
                SensorData sensordata = new SensorData(id, accX, accY, accZ, azimuth_, pitch_, roll_);
                databaseReference.child(id).setValue(sensordata);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
