package com.example.mvideo.random;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.SENSOR_SERVICE;

public class OwnItemsFragment extends Fragment implements SensorEventListener {

    private EditText etOwnItem;
    private Button btnAddItem;
    private Button btnRandom;
    private ListView lvOwnItems;
    private ArrayList<String> tempItems=new ArrayList<>();
    private ArrayAdapter adapter;
    private String tempItem;

    Sensor mySensor;
    SensorManager mySensorManager;
    boolean start = false;
    float accel;
    float accelCurrent;
    float accelLast;
    int shakeReset = 2500;
    long timeStamp;
    private int millisec = 200;

    private String empty = "List is empty!!!";
    private String atLeast = "At least must be 2 items!!!";


    public OwnItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_own_items, container, false);
        getActivity().setTitle("Own items");

        etOwnItem = view.findViewById(R.id.etOwnItem);
        lvOwnItems = view.findViewById(R.id.lvOwnItems);
        btnAddItem = view.findViewById(R.id.btnAddItem);
        btnRandom = view.findViewById(R.id.btnRandom);

        adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, tempItems);
        lvOwnItems.setAdapter(adapter);

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempItem = etOwnItem.getText().toString().trim();
                if(tempItem.equals("")){
                    Toast.makeText(getActivity(), "Field is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.add(tempItem);
                    adapter.notifyDataSetChanged();
                    etOwnItem.setText("");
                }
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempCount = adapter.getCount();
                Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                String tempValue="";
                if(tempCount>1){
                    int tempIndex = randomItem(tempCount);
                    Log.d("yuriTempValue","count: "+adapter.getCount());
                    Log.d("yuriTempValue","temp index: "+tempIndex);
                    try{
                        tempValue = adapter.getItem(tempIndex).toString();
                    } catch (Exception e){
                        Log.d("yuriTempValue",":::: "+e.toString());
                    }
                    //Toast.makeText(getActivity(), ""+tempValue, Toast.LENGTH_SHORT).show();
                    showAlertDialog(tempValue);
                    vibrator.vibrate(millisec);
                    Log.d("yuriTempValue","item: "+tempValue);
                } else if(tempCount==0){
                   // Toast.makeText(getActivity(), "List is empty!!!", Toast.LENGTH_SHORT).show();
                    showAlertDialog(empty);
                } else {
                    //Toast.makeText(getActivity(), "At least must be 2 items!!!", Toast.LENGTH_SHORT).show();
                    showAlertDialog(atLeast);
                }

            }
        });

        lvOwnItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("yuriTempValue", "temp long position: "+position);
                Toast.makeText(getActivity(), "'"+adapter.getItem(position)+"' was removed", Toast.LENGTH_SHORT).show();
                tempItems.remove(position);
                adapter.notifyDataSetChanged();
                return true;
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

    private int randomItem(int tempCount){
        Random random = new Random();
        int minValue = 0;
        int maxValue = tempCount;
        int randomIndex = random.nextInt(maxValue-minValue)+minValue;
        return randomIndex;
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

            int tempCount = adapter.getCount();
            String tempValue="";

            if(tempCount>1){
                int tempIndex = randomItem(tempCount);
                Log.d("yuriTempValue","count: "+adapter.getCount());
                Log.d("yuriTempValue","temp index: "+tempIndex);
                try{
                    tempValue = adapter.getItem(tempIndex).toString();
                } catch (Exception e){
                    Log.d("yuriTempValue",":::: "+e.toString());
                }
                //Toast.makeText(getActivity(), ""+tempValue, Toast.LENGTH_SHORT).show();
                showAlertDialog(tempValue);
                vibrate(millisec);
                Log.d("yuriTempValue","item: "+tempValue);
            } else if(tempCount==0){
                //Toast.makeText(getActivity(), "List is empty!!!", Toast.LENGTH_SHORT).show();
                showAlertDialog(empty);
            } else {
               // Toast.makeText(getActivity(), "At least must be 2 items!!!", Toast.LENGTH_SHORT).show();
                showAlertDialog(atLeast);
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

    private void showAlertDialog(String msg){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create();
        dialog.show();
    }

    final String TAG = "Fragment";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroy();
        Log.d(TAG, "OwnItemsFragment: onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OwnItemsFragment: onDestroy()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "OwnItemsFragment: onStop()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "OwnItemsFragment: onDetach()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "OwnItemsFragment: onStart()");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OwnItemsFragment: onResume()");
        mySensorManager.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "OwnItemsFragment: onPause()");
        mySensorManager.unregisterListener(this);
    }
}
