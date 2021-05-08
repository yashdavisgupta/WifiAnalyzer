package com.example.wifianalyzer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class APFragment extends Fragment {

    private WifiManager wifiManager;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ProgressBar loading;

    public APFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ap, container, false);
        super.onCreate(savedInstanceState);
        loading = (ProgressBar)v.findViewById(R.id.progressBar1);
        loading.setVisibility(View.GONE);

        wifiManager = (WifiManager)
                getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getActivity(), "Enabling WIFI", Toast.LENGTH_SHORT).show();
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

        listView = v.findViewById(R.id.wifiList);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
        return v;
    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        Toast.makeText(getActivity(), "Access Point Scan Succeeded", Toast.LENGTH_SHORT).show();
        for (ScanResult scanResult : results) {
            arrayList.add("SSID: " + scanResult.SSID + "\nCapabilities:" + scanResult.capabilities + "\nBSSID: " + scanResult.BSSID + "\nFrequency: " + scanResult.frequency + "MHz\nSignal Strength: " + scanResult.level + "dB");
            adapter.notifyDataSetChanged();
        }
        loading.setVisibility(View.GONE);
    }

    private void scanFailure() {
        Toast.makeText(getActivity(), "Access Point Scan Failed", Toast.LENGTH_SHORT).show();
        loading.setVisibility(View.GONE);

    }

}
