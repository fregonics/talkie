package com.fregonics.github.talkie;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mWifiP2pIntentFilter;
    Collection<WifiP2pDevice> mDeviceCollection;

    LinearLayout mDevicesLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUiComponents();
        setWfiP2p();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mWifiP2pIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onUpdateWifiP2pDevicesList(WifiP2pDeviceList deviceList) {
        Log.d(MainActivity.class.getSimpleName(), "UPDATE WIFI P2P DEVICE LIST");
        mDeviceCollection = deviceList.getDeviceList();
        for(WifiP2pDevice device: mDeviceCollection) {
            final TextView textView = new TextView(this);
            textView.setText(device.deviceAddress);
            textView.setTextSize(20);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WifiP2pConfig config = new WifiP2pConfig();
                    final TextView tv = (TextView) view;
                    config.deviceAddress = tv.getText().toString();
                    wifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(MainActivity.class.getSimpleName(),"CONNECTED TO: " + tv.getText().toString());
                            Toast.makeText(getApplicationContext(),"CONNECTION SUCCESS",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int i) {
                            Log.d(MainActivity.class.getSimpleName(),"FAIL TO: " + tv.getText().toString());
                            Toast.makeText(getApplicationContext(),"CONNECTION FAIL",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            mDevicesLinearLayout.addView(textView);
        }
    }

    void setUiComponents() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDevicesLinearLayout = findViewById(R.id.ll_devices);
    }

    void setWfiP2p() {
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel = wifiP2pManager.initialize(this,getMainLooper(),null);
        mReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager,mChannel,this);

        mWifiP2pIntentFilter = new IntentFilter();
        mWifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mWifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mWifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mWifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        wifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(MainActivity.class.getSimpleName(), "DISCOVERING PEERS");
            }

            @Override
            public void onFailure(int i) {
                Log.d(MainActivity.class.getSimpleName(), "NOT DISCOVERING PEERS");
            }
        });
    }
}
