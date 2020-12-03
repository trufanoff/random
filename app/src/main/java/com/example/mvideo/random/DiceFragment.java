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

import java.util.Random;

import static android.content.Context.SENSOR_SERVICE;

public class DiceFragment extends Fragment implements SensorEventListener {

    private ImageView ivDice;
    private Button btnRandom;

    private int millisec = 200;

    Sensor mySensor;
    SensorManager mySensorManager;
    boolean start = false;
    float accel;
    float accelCurrent;
    float accelLast;
    int shakeReset = 2500;
    long timeStamp;

    public DiceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dice, container, false);
        getActivity().setTitle("Dice");

        ivDice = view.findViewById(R.id.ivDice);
        btnRandom = view.findViewById(R.id.btnRandom);

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempDice = randomDice();
                setImage(tempDice);
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

    public static int randomDice(){
        Random random = new Random();

        int minNum = 0;
        int maxNum = 6;
        int randomNum = random.nextInt(maxNum-minNum)+minNum+1;
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
            int tempDice = randomDice();
            setImage(tempDice);
            vibrate(millisec);
        }
    }

    public void setImage(int tempDice){
        switch (tempDice){
            case 1:
                ivDice.setImageResource(R.drawable.dice1);
                break;
            case 2:
                ivDice.setImageResource(R.drawable.dice2);
                break;
            case 3:
                ivDice.setImageResource(R.drawable.dice3);
                break;
            case 4:
                ivDice.setImageResource(R.drawable.dice4);
                break;
            case 5:
                ivDice.setImageResource(R.drawable.dice5);
                break;
            case 6:
                ivDice.setImageResource(R.drawable.dice6);
                break;
            default:
                ivDice.setImageResource(R.drawable.question_mark);
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
        Log.d(TAG, "DiceFragment: onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "DiceFragment: onDestroy()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "DiceFragment: onStop()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "DiceFragment: onDetach()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "DiceFragment: onStart()");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "DiceFragment: onResume()");
        mySensorManager.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "DiceFragment: onPause()");
        mySensorManager.unregisterListener(this);
    }
}