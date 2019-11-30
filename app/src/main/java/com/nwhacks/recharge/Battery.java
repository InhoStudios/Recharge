package com.nwhacks.recharge;

import android.app.IntentService;
import android.content.Intent;
import android.os.BatteryManager;

public class Battery extends IntentService {
   public Battery() {
       super("battery");
   }
    @Override
    public void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        int level = workIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = workIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        System.out.println("battery level: " + batteryPct);
    }
}
