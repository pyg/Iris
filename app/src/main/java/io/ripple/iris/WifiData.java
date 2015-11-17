package io.ripple.iris;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

/**
 * Created by Keen on 11/17/2015.
 */
public class WifiData extends Datum {
    private BroadcastReceiver br;
    public WifiData(Context context) {
        super(context);
    }
    @Override
    void startup() {
        final WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                log(wm.getScanResults().toString());
                wm.startScan();
            }
        };
        context.registerReceiver(br, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wm.startScan();
    }

    @Override
    void close() {
        logListeners.clear();
        outListeners.clear();
        context.unregisterReceiver(br);
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
