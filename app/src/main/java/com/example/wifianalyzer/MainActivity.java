package com.example.wifianalyzer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ProgressBar loading;

    @Override
     protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = (ProgressBar)findViewById(R.id.progressBar1);
        loading.setVisibility(View.GONE);

        wifiManager = (WifiManager)
                this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

         if (!wifiManager.isWifiEnabled()) {
             Toast.makeText(this, "Enabling WIFI", Toast.LENGTH_SHORT).show();
             wifiManager.setWifiEnabled(true);
         }

        loading.setVisibility(View.VISIBLE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
             @Override
             public void onReceive(Context c, Intent intent) {
                 boolean success = intent.getBooleanExtra(
                         WifiManager.EXTRA_RESULTS_UPDATED, false);
                 if (success) {
                     scanSuccess();
                 } else {
                     // scan failure handling
                     scanFailure();
                 }
             }
         };

         listView = findViewById(R.id.wifiList);
         adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
         listView.setAdapter(adapter);

        IntentFilter intentFilter = new IntentFilter();
         intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
         this.getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

         boolean success = wifiManager.startScan();
         if (!success) {
             // scan failure handling
             scanFailure();
         }
     }


    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        Toast.makeText(this, "Access Point Scan Succeeded", Toast.LENGTH_SHORT).show();
        for (ScanResult scanResult : results) {
            arrayList.add("SSID: " + scanResult.SSID + "\nBSSID: " + scanResult.BSSID + "\nFrequency: " + scanResult.frequency + "MHz\nSignal Strength: " + scanResult.level + "dB");
            adapter.notifyDataSetChanged();
        }
        loading.setVisibility(View.GONE);
    }

    private void scanFailure() {
        Toast.makeText(this, "Access Point Scan Failed", Toast.LENGTH_SHORT).show();
        loading.setVisibility(View.GONE);

    }
}