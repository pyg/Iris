package io.ripple.iris;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GpsStatus.NmeaListener {
    FileOutputStream os = null;
    LocationManager lm;
    GpsStatus.NmeaListener nmeaListener;
    SensorManager sm;
    SensorEventListener accelListener;
    Sensor mAccel;
    WifiManager wm;
    TelephonyManager tm;
    MyPSListener myPSListener;
    Boolean going;
    BroadcastReceiver br;
    public class MyPSListener extends PhoneStateListener {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//            Log.d("SSC",signalStrength.toString());
            log(signalStrength.toString());
        }
    }
    protected void log(String txt) {

        String toWrite = System.nanoTime() + "," + txt + "\n";
        try {
            os.write(toWrite.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        going = false;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (going) {
                    lm.removeNmeaListener(nmeaListener);
                    sm.unregisterListener(accelListener);
                    tm.listen(myPSListener, PhoneStateListener.LISTEN_NONE);
                    unregisterReceiver(br);
                    // Log.d("Press", "Test");
                    log("ended");
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("D","ended");
                    going = false;


                } else {
                    File file = new File(Environment.getExternalStorageDirectory(), "iris_log.txt");
                    try {
                        os = new FileOutputStream(file, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.v("LL", "fack");
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    });

                    nmeaListener = new GpsStatus.NmeaListener() {
                        @Override
                        public void onNmeaReceived(long timestamp, String nmea) {
                            log("nmea,"+timestamp+","+nmea);
                            //Log.d("NMEA", System.nanoTime()+ " " + timestamp + " " + nmea);
                        }
                    };

                    lm.addNmeaListener(nmeaListener);


                    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
                    mAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    Sensor mGyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                    Sensor mMag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                    accelListener = new SensorEventListener() {
                        @Override
                        public void onSensorChanged(SensorEvent event) {
                            log(event.sensor.getName()+","+event.timestamp+","+ Arrays.toString(event.values));
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {

                        }
                    };

                    sm.registerListener(accelListener, mAccel, SensorManager.SENSOR_DELAY_FASTEST);
                    sm.registerListener(accelListener, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
                    sm.registerListener(accelListener, mMag, SensorManager.SENSOR_DELAY_FASTEST);

                    wm = (WifiManager) getSystemService(WIFI_SERVICE);

                    br = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            // Log.d("SR", wm.getScanResults().toString());

                            if (going) {
                                log(wm.getScanResults().toString());
                                wm.startScan();
                            }
                        }
                    };
                    registerReceiver(br,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    wm.startScan();
                    tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    myPSListener = new MyPSListener();
                    tm.listen(myPSListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                    going = true;
                    log("started");
                    Log.d("D","done");
                }
                Snackbar.make(view, String.valueOf(going), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.d("Press", "Test");
                log("left");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.d("Press", "Test");
                log("middle");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.d("Press", "Test");
                log("right");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNmeaReceived(long timestamp, String nmea) {
        Log.d("NMEA", nmea);
    }
}
