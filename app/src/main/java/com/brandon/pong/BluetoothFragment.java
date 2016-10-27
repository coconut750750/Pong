package com.brandon.pong;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BluetoothFragment extends Fragment {
    private BluetoothAdapter bluetooth;
    public static BluetoothSocket socket;

    private ListView list;
    private Button searchButton;
    private Button listenButton;
    private Button menuButton;


    private ArrayList<BluetoothDevice> foundDevices;
    private List<String> deviceNames;


    private UUID uuid = UUID.fromString("79fb2000-8bd3-11e6-bdf4-0800200c9a66");
    private static int DISCOVERY_REQUEST = 1;
    private ArrayAdapter<String> aa;
    int REQUEST_ENABLE_BT = 1;

    public final String name = "bluetoothserver";

    BroadcastReceiver discoveryResult;

    public static ProgressDialog progressDialog;


    public BluetoothFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        // Get views
        menuButton = (Button)view.findViewById(R.id.button_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(BluetoothFragment.this).commit();
            }
        });

        // Get the Bluetooth Adapter
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        foundDevices = new ArrayList<>();
        deviceNames = new ArrayList<>();
        aa = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, deviceNames);
        if (!bluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        /*Set<BluetoothDevice> pairedDevice = bluetooth.getBondedDevices();
        if(pairedDevice.size()>0)
        {
            for(BluetoothDevice device : pairedDevice)
            {
                foundDevices.add(device);
                deviceNames.add(device.getName());
            }
        }*/
        aa.notifyDataSetChanged();

        // Request location access
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


        // Setup the ListView of discovered devices
        list = (ListView)view.findViewById(R.id.list_discovered);
        list.setAdapter(aa);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                AsyncTask<Integer, Void, Void> connectTask = new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer ... params) {
                        try {
                            BluetoothDevice device = foundDevices.get(params[0]);
                            socket = device.createRfcommSocketToServiceRecord(uuid);
                            socket.connect();
                        } catch (IOException e) {
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {
                        startGame(2);
                    }
                };
                connectTask.execute(index);
                progressDialog.show();
                progressDialog.setMessage("Connecting...");
            }
        });

        // Setup search button
        discoveryResult = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice remoteDevice;
                remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!foundDevices.contains(remoteDevice) && bluetooth.getBondedDevices().contains(remoteDevice)) {
                    foundDevices.add(remoteDevice);
                    deviceNames.add(remoteDevice.getName());
                    aa.notifyDataSetChanged();
                }

            }
        };
        searchButton = (Button)view.findViewById(R.id.button_search);
        view.getContext().registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!bluetooth.isDiscovering()) {
                    bluetooth.startDiscovery();
                    foundDevices.clear();
                    deviceNames.clear();
                    aa.notifyDataSetChanged();
                }
            }
        });

        // Setup listen button
        listenButton = (Button)view.findViewById(R.id.button_listen);
        listenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent disc = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(disc, DISCOVERY_REQUEST);
                //progressDialog.show();
                //progressDialog.setMessage("Waiting for Opponent...");
            }
        });

        return view;
    }

    private void startGame(int player) {
        progressDialog.dismiss();
        Intent intent = new Intent(getView().getContext(), MainActivity.class);
        intent.putExtra(MainActivity.GAME_TYPE, MainActivity.DOUBLE_PLAYER);
        intent.putExtra(MainActivity.PLAYER_NUM, player);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DISCOVERY_REQUEST) {
            boolean isDiscoverable = resultCode > 0;
            if (isDiscoverable) {
                try {
                    final BluetoothServerSocket btserver = bluetooth.listenUsingRfcommWithServiceRecord(name, uuid);
                    AsyncTask<Integer, Void, BluetoothSocket> acceptThread =
                            new AsyncTask<Integer, Void, BluetoothSocket>() {
                                @Override
                                protected BluetoothSocket doInBackground(Integer ... params) {

                                    try {
                                        socket = btserver.accept(params[0]*1000);
                                        return socket;
                                    } catch (IOException e) {
                                    }

                                    return null;
                                }


                                @Override
                                protected void onPostExecute(BluetoothSocket result) {
                                    if (result != null)
                                        startGame(1);
                                }
                            };
                    acceptThread.execute(resultCode);
                } catch (IOException e) {
                }
            }
        }
    }

}
