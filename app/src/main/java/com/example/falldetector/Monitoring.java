package com.example.falldetector;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

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
    ArrayList<Double> magnitude = new ArrayList<Double>();
    private FirebaseFirestore db;
    int fall = 0;
    Button safe;
    CountDownTimer timer;
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm");
    String currentTime = format.format(new Date());
    private static final long START_TIME_IN_MILLIS = 60000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    private TextView mTextViewCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("sensorData");
        minimizeApp();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acclerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, acclerometer, SensorManager.SENSOR_DELAY_NORMAL);
        safe = (Button) findViewById(R.id.safe);
        mTextViewCountDown = findViewById(R.id.counter);
        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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
            if (ldAccRound > 0.3d && ldAccRound < 0.9d) {
                sensorManager.unregisterListener(this);
                magnitude.add(ldAccRound);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(500);
                }
                accX = Double.toString(xAcc);
                accY = Double.toString(yAcc);
                accZ = Double.toString(zAcc);
                SensorData sd = new SensorData(accX, accY, accZ, fall);
                databaseReference.child(String.valueOf(currentTime)).setValue(sd);
                startTimer();
                double m = magnitude.get(magnitude.size() - 1);
                Log.d("xAcc", "" + xAcc);
                Log.d("xAcc", "" + yAcc);
                Log.d("xAcc", "" + zAcc);
                Log.d("magnitude", "" + m);
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

    private void startTimer() {
        timer = new CountDownTimer(mTimeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                if(databaseReference.child(currentTime).child("fall").getKey().)
            }
        }.start();
        mTimerRunning = true;
    }

    public void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }
 
    public void sos() {
        try {
            databaseReference.child(currentTime).child("fall").setValue("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}