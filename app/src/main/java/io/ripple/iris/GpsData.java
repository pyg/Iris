package io.ripple.iris;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Keen on 11/10/2015.
 */
public class GpsData extends Datum {
    private LocationManager lm;
    private GpsStatus.NmeaListener nmeaListener;
    public GpsData(Context context) {
        super(context);
    }

    @Override
    public void startup() {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                log("Startup failure: Permissions");
                return;
            }
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v("LL", "0"); // TODO: this is a call to keep GPS alive... find some other way.
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
                log("nmea," + timestamp + "," + nmea);
                out(nmea);
            }
        };
        lm.addNmeaListener(nmeaListener);
    }

    public void close() {
        logListeners.clear();
        outListeners.clear();
        lm.removeNmeaListener(nmeaListener);
    }

    @Override
    protected void log(String text) {
        notifyLogListeners(text);
    }

    @Override
    protected void out(String text) {
        notifyOutListeners(new int[0]);
    }
}
