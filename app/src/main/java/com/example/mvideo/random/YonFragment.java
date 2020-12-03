package com.example.mvideo.random;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import static android.content.Context.SENSOR_SERVICE;

public class YonFragment extends Fragment implements SensorEventListener {
    private TextView tvResult;
    private Button btnRandom;


    Sensor mySensor;
    SensorManager mySensorManager;
    boolean start = false;
    float accel;
    float accelCurrent;
    float accelLast;
    int shakeReset = 2500;
    long timeStamp;
    private int millisec = 200;

    public YonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yon, container, false);
        getActivity().setTitle("YES or NO");
        tvResult = view.findViewById(R.id.tvResult);
        btnRandom = view.findViewById(R.id.btnRandom);

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResult.setText(randomYON());
                vibrate(millisec);
            }
        });

        // CREATE SENSOR MANAGER
        try {
            mySensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        } catch (Exception e){
            Log.d("yuriSensor",e.toString());
        }

        // CREATE ACCELERATION SENSOR
        try {
            mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } catch (Exception e){
            Log.d("yuriAcce", e.toString());
        }

        // REGISTERING OUR SENSOR
        try {
            mySensorManager.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_UI);
        } catch(Exception e){
            Log.d("yuriRegSensor", e.toString());
        }

        // SETTING ACCELERATION VALUES
        accel = 0.00f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;

        return view;
    }

    public static String randomYON(){
        Random random = new Random();

        int minNum = 0;
        int maxNum = 2;
        int randomNum = random.nextInt(maxNum-minNum)+minNum;

        if(randomNum==0){
            return "NO";
        }
        else return "YES";
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // STORING THE VALUES OF THE AXIS
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        // ACCELEROMETER LAST READ EQUAL TO THE CURRENT ONE
        accelLast = accelCurrent;
        // QUICK MAFS TO CALCULATE THE ACCELERATION
        accelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
        // DELTA BETWEEN THE CURRENT AND THE LAST READ OF THE ACCELEROMETER
        float delta = accelCurrent - accelLast;
        // QUICK MAFS TO CALCULATE THE ACCEL THAT WILL DECLARE IF IT SHAKED OR NOT
        accel = accel * 0.9f + delta;

        // DID IT SHAKE??
        if (accel > 3) {
            final long timenow = System.currentTimeMillis();
            if(timeStamp + shakeReset  > timenow){
                return;
            }
            timeStamp = timenow;
            String temp = randomYON();
            vibrate(millisec);
            tvResult.setText(temp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void vibrate(int millisec){
        Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(millisec);
    }

    final String TAG = "Fragment";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroy();
        Log.d(TAG, "YonFragment: onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "YonFragment: onDestroy()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "YonFragment: onStop()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "YonFragment: onDetach()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "YonFragment: onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "YonFragment: onResume()");
        mySensorManager.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "YonFragment: onPause()");
        mySensorManager.unregisterListener(this);
    }
}
