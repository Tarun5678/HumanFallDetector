package com.example.falldetector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.example.falldetector.Monitoring.geoUri;

public class OfflineMode extends AppCompatActivity {

    String xAcc, yAcc, zAcc;
    Button safe;
    private static final long START_TIME_IN_MILLIS = 30000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    CountDownTimer timer;
    private TextView mTextViewCountDown;
    private FusedLocationProviderClient client;
    public static String geoUri = "";
    String ecPhone, ecName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_mode);
        client = LocationServices.getFusedLocationProviderClient(this);
        mTextViewCountDown = (TextView) findViewById(R.id.timer);
        Intent intent = getIntent();
        xAcc = intent.getStringExtra("xAcc");
        yAcc = intent.getStringExtra("yAcc");
        zAcc = intent.getStringExtra("zAcc");
        ecName = intent.getStringExtra("Emergency_name");
        ecPhone = intent.getStringExtra("Emergency_phone");
        double[] readings = new double[3];
        readings[0] = Double.parseDouble(xAcc);
        readings[1] = Double.parseDouble(yAcc);
        readings[2] = Double.parseDouble(zAcc);
        int predicted = RandomForestClassifier.predict(readings);
        if (predicted == 0) {
            Log.d("Predicted", "Backward-Fall");
            startTimer();

        } else if (predicted == 1) {
            Log.d("Predicted", "Fall-on-Knee");
            startTimer();

        } else if (predicted == 2) {
            Log.d("Predicted", "Normal-Fall");
            startTimer();

        } else if (predicted == 3) {
            Log.d("Predicted", "Side-Fall");
            startTimer();
        }
        safe = (Button) findViewById(R.id.safe_button);
        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sos();
            }
        });
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
                getLocation();
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

        stopTimer();
        Intent i = new Intent(OfflineMode.this, MainActivity.class);
        startActivity(i);
        Toast.makeText(getApplicationContext(), "Thank you,Hope you are alright.",
                Toast.LENGTH_LONG).show();

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
        client.getLastLocation().addOnSuccessListener(OfflineMode.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                    geoUri = ecName + "" + "Your friend is in trouble,Please check out his most recent location:" + "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
                try {
                    Log.d("phone", ecPhone);
                    sendSMS(ecPhone, geoUri);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Invalid mobile number",
                            Toast.LENGTH_LONG).show();
                }

            }

        });

    }
}