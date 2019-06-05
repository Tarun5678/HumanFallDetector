package com.example.falldetector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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
    static String timestamp;
    private static final long START_TIME_IN_MILLIS = 3000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    private TextView mTextViewCountDown;
    private FusedLocationProviderClient client;
    public static String geoUri = "";
    String ecPhone,ecName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        Intent intent = getIntent();
        ecPhone = intent.getStringExtra("EmergencyPhone");
        ecName  = intent.getStringExtra("EmergencyName");
        client = LocationServices.getFusedLocationProviderClient(this);
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

                sos();
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
                timestamp = currentTime;
                getLocation();
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
                DatabaseReference fall = databaseReference.child(currentTime).child("fall");
                fall.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("xAcc__", "" + timestamp);
                        String fallen = dataSnapshot.getValue(String.class);
                        Log.d("xAcc", "" + fallen);
                        if (fallen.equals("1")) {
                            Log.d("True", "" + fallen);
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                v.vibrate(500);
                            }
                            getLocation();
                        } else {
                            Log.d("False", "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
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

    public void stopTimer() {
        timer.cancel();
        mTimerRunning = false;
    }

    public void sos() {
        try {
            databaseReference.child(timestamp).child("fall").setValue("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopTimer();
        Intent i = new Intent(Monitoring.this, MainActivity.class);
        startActivity(i);

    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(Monitoring.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                    geoUri = ecName+""+"Your friend is in trouble,Please check out his most recent location:" + "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
                try {
                    Log.d("phone", ecPhone);
                    sendSMS(ecPhone, geoUri);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Invalid mobile number",
                            Toast.LENGTH_LONG).show();
                }

            }

        });

    }
}