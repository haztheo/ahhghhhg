package com.example.harrytrickbook.cec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import java.util.HashMap;
import java.util.Iterator;
import android.content.Context;
import com.example.harrytrickbook.cec.UsbCecConnection;
import android.util.Log;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbDeviceConnection;

import android.app.PendingIntent;

public class MainActivity extends AppCompatActivity {

    UsbDevice chosenDevice;
    UsbCecConnection cecCommands;


    private static String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Log.d("Device List", "Device List " + String.valueOf(deviceList.size()));
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.android.example.USB_PERMISSION"), 0);

        if(deviceList.size() > 0){
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            while(deviceIterator.hasNext()){
                UsbDevice device = deviceIterator.next();
                String name = device.getDeviceName();
                String product = device.getProductName();
                Log.d("Device Name", name);
                Log.d("Device product", product);
            }

            chosenDevice = deviceList.get("/dev/bus/usb/001/004");
            manager.requestPermission(chosenDevice, mPermissionIntent);

            UsbInterface intf = chosenDevice.getInterface(0);
            UsbEndpoint endpoint = intf.getEndpoint(0);
            UsbDeviceConnection connection = manager.openDevice(chosenDevice);

            cecCommands = new UsbCecConnection(chosenDevice, connection);

            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Button Pressed", "0");
                    cecCommands.switchTvOn();
                }
            });

            final Button button2 = (Button) findViewById(R.id.button2);
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Button Pressed", "1");
                    cecCommands.switchTvOff();
                }
            });

            final Button button3 = (Button) findViewById(R.id.button3);
            button3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Button Pressed", "2");
                    cecCommands.volumeUp();
                }
            });

            final Button button4 = (Button) findViewById(R.id.button4);
            button4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Button Pressed", "3");
                    cecCommands.volumeDown();
                }
            });
        }
    }
}
