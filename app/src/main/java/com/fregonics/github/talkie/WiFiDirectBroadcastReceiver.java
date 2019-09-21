package com.fregonics.github.talkie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity mainActivity;
    private WifiP2pManager.PeerListListener mPeerListListener;


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.mainActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(WiFiDirectBroadcastReceiver.class.getSimpleName(), "ACTION: " + action);
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int wifiP2pState = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
            if(wifiP2pState != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(WiFiDirectBroadcastReceiver.class.getSimpleName(), "WIFI P2P DISABLED");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            manager.requestPeers(channel,mPeerListListener);
            Log.d(WiFiDirectBroadcastReceiver.class.getSimpleName(), mPeerListListener.toString());
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

    }
}
