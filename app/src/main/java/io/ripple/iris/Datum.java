package io.ripple.iris;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keen on 11/10/2015.
 */
public abstract class Datum {
    Context context;
    public Datum(Context context) {
        this.context = context;
        logListeners.add((DatumLogListener)context);
        outListeners.add((DatumOutListener)context);
        startup();
    }
    List<DatumLogListener> logListeners = new ArrayList<DatumLogListener>();
    List<DatumOutListener> outListeners = new ArrayList<DatumOutListener>();
    abstract void startup();
    abstract void close();
    abstract protected void log(String text);
    abstract protected void out(String text);
    public void addLogListener(DatumLogListener listener) {
        logListeners.add(listener);
    }
    public void removeLogListener(DatumLogListener listener) {
        logListeners.remove(listener);
    }
    public void addOutListener(DatumOutListener listener) {
        outListeners.add(listener);
    }
    public void removeOutListener(DatumOutListener listener) {
        outListeners.remove(listener);
    }
    protected void notifyLogListeners(String text) {
        for (DatumLogListener listener : logListeners) {
            listener.logReceived(text);
        }
    }
    protected void notifyOutListeners(int[] nums) {
        for (DatumOutListener listener : outListeners) {
            listener.outReceived(nums);
        }
    }
}