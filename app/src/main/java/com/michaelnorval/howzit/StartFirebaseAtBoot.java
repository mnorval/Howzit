package com.michaelnorval.howzit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Start the service when the device boots.
 *
 * @author vikrum
 *
 */
public class StartFirebaseAtBoot extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context,FirebaseBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);

        }
        Log.i("Autostart", "started");




        /*
            startService(new Intent(this, FirebaseBackgroundService.class));
            Intent intent = new Intent(MainActivity.this, NavDrawerActivity.class);
            startActivity(intent);
        }*/
    }
}