package com.example.androidbatterysync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static android.content.ContentValues.TAG;

public class BatteryReceiver extends BroadcastReceiver {
    String prevBattLevel = "Z";
    String prevBattStatus = "Z";

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            int level = intent.getIntExtra("level", 0);
            String BattLevel = String.valueOf(level);

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String BattStatus = "NIL";
            if (status == BatteryManager.BATTERY_STATUS_CHARGING){BattStatus = "C";}
            if (status == BatteryManager.BATTERY_STATUS_DISCHARGING){BattStatus = "D";}
            if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING){BattStatus = "D";}
            if (status == BatteryManager.BATTERY_STATUS_FULL){BattStatus = "F";}
            if (status == BatteryManager.BATTERY_STATUS_UNKNOWN){BattStatus = "NIL";}

//            Log.e("[INFO]", BattLevel+BattStatus);

            if((prevBattLevel != BattLevel) || (prevBattStatus != BattStatus)) {
                if(BattStatus == "NIL") {
                    sendUDPMessage("NIL");
                } else if (BattStatus == "F") {
                    sendUDPMessage("100D");
                } else {
                    String message = BattLevel+BattStatus;
                    sendUDPMessage(message);
                }
                prevBattLevel = BattLevel;
                prevBattStatus = BattStatus;
            }


        } catch (Exception e){
            Log.v(TAG, "Battery Info Error");
        }
    }

    private void sendUDPMessage(final String message) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                DatagramSocket ds = null;
                try {
                    InetAddress ADDR = InetAddress.getByName("192.168.0.100");
                    int PORT = 1737;
                    ds = new DatagramSocket();
                    DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), ADDR, PORT);
                    ds.send(dp);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
            }
        });
        thread.start();
    }
}