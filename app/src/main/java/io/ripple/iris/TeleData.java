package io.ripple.iris;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Keen on 11/17/2015.
 */
public class TeleData extends Datum {
    private TelephonyManager tm;
    private PhoneStateListener ps;
    public TeleData(Context context) {
        super(context);
    }

    @Override
    void startup() {
        tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        ps = new PhoneStateListener() {
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                // Log.d("SSC", signalStrength.toString());
                log(signalStrength.toString());
            }
        };
        tm.listen(ps, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

    }

    @Override
    void close() {
        logListeners.clear();
        outListeners.clear();
        tm.listen(ps, PhoneStateListener.LISTEN_NONE);
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
