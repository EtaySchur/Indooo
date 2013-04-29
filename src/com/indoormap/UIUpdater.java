package com.indoormap;

import android.os.Handler;

public class UIUpdater {

	private Handler mHandler = new Handler();

    private Runnable mStatusChecker;
    private static int UPDATE_INTERVAL = 600000;

    /**
     * Creates an UIUpdater object, that can be used to
     * perform UIUpdates on a specified time interval.
     * 
     * @param uiUpdater A runnable containing the update routine.
     */
    public UIUpdater(final Runnable uiUpdater){
        this(uiUpdater,UPDATE_INTERVAL);
    }

    /**
     * The same as the default constructor, but specifying the
     * intended update interval.
     * 
     * @param uiUpdater A runnable containing the update routine.
     * @param interval  The interval over which the routine
     *                  should run (milliseconds).
     */
    public UIUpdater(final Runnable uiUpdater, final int interval){
        UPDATE_INTERVAL = interval;
        mStatusChecker = new Runnable() {
            @Override
            public void run() {
                // Run the passed runnable
                uiUpdater.run();
                // Re-run it after the update interval
                mHandler.postDelayed(this, interval);
            }
        };
    }

    /**
     * Starts the periodical update routine (mStatusChecker 
     * adds the callback to the handler).
     */
    public void startUpdates(){
        mStatusChecker.run();
    }

    /**
     * Stops the periodical update routine from running,
     * by removing the callback.
     */
    public void stopUpdates(){
        mHandler.removeCallbacks(mStatusChecker);
    }
}