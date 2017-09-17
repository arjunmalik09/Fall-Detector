package com.example.rishabh.didufall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math;
//import  android.widget.L

public class MainActivity extends AppCompatActivity {

    //Declaration of Sensor Manager and Sensor Listener
    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;

    //Variables
    boolean buttonClicked = false;
    float[] mAccelerometer,mGyroscope,mGeomagnetic,mGravity;
    float[] alpha=new float[1000005],omega=new float[1000005];
    float theta;
    double tAlpha = 12.5 ,tOmega =2.5, tl=75 , tm=1;
    double maxDAlpha = 0,maxDOmega = 0,maxThetal = 0,minMAlpha = 1000;
    int i=0,j=0;
    //Declaration of Text Views
    TextView tvAccelerometer, tvGyroscope,calibrate;
    TextView message;
//    TextView dAlpha,dOmega,th,mAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAccelerometer = (TextView)findViewById(R.id.accelerometer_value);
        tvGyroscope =(TextView)findViewById(R.id.gyroscope_value);
        //dAlpha = (TextView)findViewById(R.id.dAlp);
        //dOmega = (TextView)findViewById(R.id.dOm);
        //th = (TextView)findViewById(R.id.theta);
        //mAlpha = (TextView)findViewById(R.id.mAlpha);
        //calibrate =(TextView)findViewById(R.id.calibrate);
        message =(TextView)findViewById(R.id.message);
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
                //Nothing to do here :)
            }

            //Runs whenever any sensor data changes
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;               //To get the sensor from the fired event

                //Using sensor's values to populate text views according to the associated sensor type
                //TODO: Crosscheck the values of each text view. Some values are either not populated or partially populated as they may require more than one sensor's readings to calculate proper values
                //Refer to the EXAMPLE from line 100 below.
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    mAccelerometer = event.values;
                    alpha[i] = (float)Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
                    i = (i+1)%1000000;
                    tvAccelerometer.setText(event.values[0]+" "+event.values[1]+" "+event.values[2]);

                }else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
                    mGyroscope = event.values;
                    omega[j] = (float)Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
                    j = (j+1)%1000000;
                    tvGyroscope.setText(event.values[0]+" "+event.values[1]+" "+event.values[2]);
                }

                if(i>40 && j>40){
                    float minAlpha=10000,maxAlpha=-10000;
                    for(int k=i;k>=i-39;k--){
                        minAlpha = Math.min(minAlpha,alpha[k]);
                        maxAlpha = Math.max(maxAlpha,alpha[k]);
                    }
                    float minOmega=10000,maxOmega=-10000;
                    for(int k=j;k>=j-39;k--){
                        minOmega = Math.min(minOmega,omega[k]);
                        maxOmega = Math.max(maxOmega, omega[k]);
                    }
/*                    System.out.println("minAlpha"+minAlpha);
                    System.out.println("maxAlpha"+maxAlpha);
                    System.out.println("minOmega"+minOmega);
                    System.out.println("maxOmega"+maxOmega);*/


                    float deltaAlpha = maxAlpha-minAlpha;
                    float deltaOmega = maxOmega-minOmega;
                    theta = (float)Math.acos(mAccelerometer[1]/9.81);
                    float thetal = (theta*180)/(float)3.14;

                    //dAlpha.setText(deltaAlpha+"");
                    //dOmega.setText(deltaOmega+"");

                    if(buttonClicked){
                        maxDAlpha = Math.max(maxDAlpha,deltaAlpha);
                        maxDOmega = Math.max(maxDOmega,deltaOmega);
                        maxThetal = Math.max(maxThetal,thetal);
                        minMAlpha = Math.min(minMAlpha, minAlpha);
                        tAlpha = maxDAlpha- 12;
                        tOmega= maxDOmega - 5;
                        tl = maxThetal - 15;
                        tm = minMAlpha + 1;
                        //calibrate.setText(maxDAlpha+"  "+maxDOmega+"  "+maxThetal+"  "+minMAlpha);
                    }
                    else{
                        if(deltaAlpha > tAlpha && deltaOmega > tOmega){
                            //th.setText(thetal + "");
                            if( thetal > tl ){
                                //th.setText(thetal + "");
                                //mAlpha.setText(minAlpha+"");
                                if(minAlpha < tm) {
                                    message.setText("YOU FELL!");
                                }
                                else {
                                    message.setText("");
                                }
                            }
                            else {
                                message.setText("");
                            }
                        }
                        else {
                            message.setText("");
                        }
                    }


                }
            }
        };

        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);

    }
    public void onButtonClick(View V){
        TextView message=(TextView)findViewById(R.id.message);
        Button bt=(Button)findViewById(R.id.button);
        buttonClicked = !buttonClicked;
        if(buttonClicked){
            bt.setText("Stop Calibrating");
            message.setText("Make the phone fall multiple times on a soft surface");
        }
        else{
            bt.setText("Calibrate");
            message.setText("");
            i=0;j=0;
        }
    }
}

