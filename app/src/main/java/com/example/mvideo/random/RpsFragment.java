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
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.content.Context.SENSOR_SERVICE;

public class RpsFragment extends Fragment implements SensorEventListener {

    private TextView tvResult;
    private Button btnRandom;
    private ImageView ivResult;

    private int millisec = 200;

    Sensor mySensor;
    SensorManager mySensorManager;
    boolean start = false;
    float accel;
    float accelCurrent;
    float accelLast;
    int shakeReset = 2500;
    long timeStamp;

    public RpsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rps, container, false);
        getActivity().setTitle("Rock Paper Scissors");

        tvResult = view.findViewById(R.id.tvResult);
        btnRandom = view.findViewById(R.id.btnRandom);
        ivResult = view.findViewById(R.id.ivRPS);

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int res = randomRPS();
                setImage(res);
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

    public void vibrate(int millisec){
        Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(millisec);
    }

    public void setImage(int temp){
        if(temp==0){
            ivResult.setImageResource(R.drawable.ic_rock);
            tvResult.setText("ROCK");
        }
        if(temp==1){
            ivResult.setImageResource(R.drawable.ic_paper);
            tvResult.setText("PAPER");
        }
        if(temp==2){
            ivResult.setImageResource(R.drawable.ic_scissors);
            tvResult.setText("SCISSORS");
        }
    }

    public static int randomRPS(){
        Random random = new Random();

        int minNum = 0;
        int maxNum = 3;
        int randomNum = random.nextInt(maxNum-minNum)+minNum;

        return randomNum;
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
            int temp = randomRPS();
            setImage(temp);
            vibrate(millisec);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    final String TAG = "Fragment";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroy();
        Log.d(TAG, "RpsFragment: onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RpsFragment: onDestroy()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "RpsFragment: onStop()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "RpsFragment: onDetach()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "RpsFragment: onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "RpsFragment: onResume()");
        mySensorManager.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "RpsFragment: onPause()");
        mySensorManager.unregisterListener(this);
    }
}
