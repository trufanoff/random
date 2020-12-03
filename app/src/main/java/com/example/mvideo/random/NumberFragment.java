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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static android.content.Context.SENSOR_SERVICE;

public class NumberFragment extends Fragment implements SensorEventListener {

    private EditText etMin;
    private EditText etMax;
    private TextView tvResult;
    private Button btnRandomize;
    private String minNumber, maxNumber;

    Sensor mySensor;
    SensorManager mySensorManager;
    boolean start = false;
    float accel;
    float accelCurrent;
    float accelLast;
    int shakeReset = 2500;
    long timeStamp;

    private int millisec = 200;

    public NumberFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number, container, false);
        getActivity().setTitle("Random Number");

        etMin = view.findViewById(R.id.etMin);
        etMax = view.findViewById(R.id.etMax);
        tvResult = view.findViewById(R.id.tvResult);
        btnRandomize = view.findViewById(R.id.btnRandom);

        btnRandomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minNumber = etMin.getText().toString().trim();
                maxNumber = etMax.getText().toString().trim();

                if(minNumber.equals("") || maxNumber.equals("")){
                    Toast.makeText(getActivity(), "These fields cannot be empty!!!", Toast.LENGTH_SHORT).show();
                    vibrate(millisec);
                } else {
                    tvResult.setText(randomNumber(minNumber, maxNumber));
                    vibrate(millisec);
                }
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

        // CREATING OUR CLICK LISTENER
        return view;
    }

    private Object getSystemService(String sensorService) {
        return sensorService;
    }

    public static String randomNumber(String minNumber, String maxNumber){
        Random random = new Random();

        int minNum = Integer.parseInt(minNumber)-1;
        int maxNum = Integer.parseInt(maxNumber);
        int randomNum = random.nextInt(maxNum-minNum)+minNum+1;

        return Integer.toString(randomNum);
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

            Log.d("yuriAccel", "accel: "+accel);

            // DID IT SHAKE??
            if (accel > 3) {
                final long timenow = System.currentTimeMillis();
                if(timeStamp + shakeReset  > timenow){
                    return;
                }
                timeStamp = timenow;

                String tmpMax, tmpMin,temp;
                tmpMax = etMax.getText().toString();
                tmpMin = etMin.getText().toString();
                Log.d("tempMinMax", "min and max: "+tmpMin+" "+tmpMax);

                if(tmpMax.equals("") || tmpMin.equals("")){
                    Toast.makeText(getActivity(), "These fields cannot be empty!!!", Toast.LENGTH_SHORT).show();
                    vibrate(millisec);
                } else {
                    temp = randomNumber(tmpMin, tmpMax);
                    vibrate(millisec);
                    tvResult.setText(temp);
                }
            }
    }

    public void vibrate(int millisec){
        Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(millisec);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    final String TAG = "Fragment";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroy();
        Log.d(TAG, "NumberFragment: onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "NumberFragment: onDestroy()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "NumberFragment: onStop()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "NumberFragment: onDetach()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "NumberFragment: onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "NumberFragment: onResume()");
        mySensorManager.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "NumberFragment: onPause()");
        mySensorManager.unregisterListener(this);
    }
}
