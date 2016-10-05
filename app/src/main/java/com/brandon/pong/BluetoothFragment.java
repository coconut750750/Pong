package com.brandon.pong;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BluetoothFragment extends Fragment {
    private BluetoothAdapter bluetooth;
    public static BluetoothSocket socket;


    private ListView list;
    private Button searchButton;
    private Button listenButton;


    private ArrayList<BluetoothDevice> foundDevices;
    private List<String> deviceNames;


    private UUID uuid = UUID.fromString("9abf0e00-8a39-11e6-bdf4-0800200c9a66");
    private static int DISCOVERY_REQUEST = 1;
    public static Handler handler = new Handler();
    private ArrayAdapter<String> aa;
    int REQUEST_ENABLE_BT = 1;

    public final String name = "bluetoothserver";

    BroadcastReceiver discoveryResult;


    public BluetoothFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        // Get the Bluetooth Adapter
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        foundDevices = new ArrayList<>();
        if (!bluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        // Setup the ListView of discovered devices
        deviceNames = new ArrayList<>();
        for(BluetoothDevice bluetoothDevice:foundDevices){
            deviceNames.add(bluetoothDevice.getName());
        }
        aa = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, deviceNames);
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
            }
        });


        // Setup search button
        searchButton = (Button)view.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view.getContext().registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                if (!bluetooth.isDiscovering()) {
                    foundDevices.clear();
                    bluetooth.startDiscovery();
                }
            }
        });
        discoveryResult = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice remoteDevice;
                remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bluetooth.getBondedDevices().contains(remoteDevice)) {
                    foundDevices.add(remoteDevice);
                    deviceNames.add(remoteDevice.getName());
                    aa.notifyDataSetChanged();
                }
            }
        };


        // Setup listen button
        listenButton = (Button)view.findViewById(R.id.button_listen);
        listenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent disc = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(disc, DISCOVERY_REQUEST);
            }
        });

        return view;

    }


    private void startGame(int player) {
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
