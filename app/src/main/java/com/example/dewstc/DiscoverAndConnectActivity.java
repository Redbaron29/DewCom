package com.example.dewstc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class DiscoverAndConnectActivity extends AppCompatActivity {
    ArrayList<BluetoothDevice> discoveredDevices, pairedDevices;
    int REQUEST_ENABLE_BT;
    BluetoothServer serverThread;
    Boolean connection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_discovery);
        this.discoveredDevices = new ArrayList<>();
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Context context = getApplicationContext();
        //Registering BroadCast receivers (BCR)
        //BCR for Changing state of Bluetooth
        IntentFilter bt_state_filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bt_state_receiver, bt_state_filter);
        //BCR for changing scan mode of Bluetooth
        IntentFilter discovery_state_filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(discovery_state_receiver, discovery_state_filter);
        //BCR for when discovery finishes
        IntentFilter discovery_status_finish = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discovery_ending, discovery_status_finish);
        //BCR for when new device is found
        IntentFilter device_found = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(device_found_receiver, device_found);
        //BCR for changing status of location services
        IntentFilter location_change = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(gpsReceiver, location_change);
        //If location is not on, request
        if(Access.isMyLocationOn(context)) {
            Access.displayLocationSettingsRequest(context, this);
            finish();
        }
        //If Bluetooth is not enabled, request
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            finish();
        }
        startDiscovery();
    }

    void startDiscovery() {
        this.discoveredDevices.clear();
        startLocalDiscovery();
    }

    public void startLocalDiscovery() {
        Context context = getApplicationContext();
        //if location is off, quit discovery and request again
        if(Access.isMyLocationOn(context)) {
            cancelDiscovery();
            Toast toast = Toast.makeText(context, "Please switch on Location and retry", Toast.LENGTH_SHORT);
            toast.show();
            Access.displayLocationSettingsRequest(context, this);
            return;
        }
        //if bluetooth is off, quit discovery and request again
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            cancelDiscovery();
            Toast toast = Toast.makeText(context, "Please switch on Bluetooth and retry", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        //if not discoverable, request for discovery
        if(checkScanMode() != 2) {
            cancelDiscovery();
            Toast toast = Toast.makeText(context, "Please switch on Discovery and retry", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            requestDiscovery();
            return;
        }
        //create list of discovered devices from scratch
        this.discoveredDevices.clear();
        //if already discovering, restart the process
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        boolean flag = bluetoothAdapter.startDiscovery();
        bluetoothAdapter.startDiscovery();
        //if discovery failed to start, show toast message
        if(!flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "Discovery failed: Please make sure Bluetooth and Location are on before retrying.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Access.displayLocationSettingsRequest(getApplicationContext(), this);
        }
    }

    //method to stop discovering if discovery is on
    public void cancelDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    //method to make side phones visible if new devices are found
    //during discovery process
    void setSidePhoneVisibility(String name) {
        int n = this.discoveredDevices.size();
        TextView text;
        switch (n) {
            case 1:
                text = findViewById(R.id.sideImageText1);
                name = n + ": " + name;
                break;
            case 2:
                text = findViewById(R.id.sideImageText2);
                name = n + ": " + name;
                break;
            case 3:
                text = findViewById(R.id.sideImageText3);
                name = n + ": " + name;
                break;
            case 4:
                text = findViewById(R.id.sideImageText4);
                name = n + ": " + name;
                break;
            case 5:
                text = findViewById(R.id.sideImageText5);
                name = n + ": " + name;
                break;
            case 6:
                text = findViewById(R.id.sideImageText6);
                name = n + ": " + name;
                break;
            case 7:
                text = findViewById(R.id.sideImageText7);
                name = n + ": " + name;
                break;
            case 8:
                text = findViewById(R.id.sideImageText8);
                name = n + ": " + name;
                break;
            case 9:
                text = findViewById(R.id.sideImageText9);
                name = n + ": " + name;
                break;
            case 10:
                text = findViewById(R.id.sideImageText10);
                name = n + ": " + name;
                break;
            case 11:
                text = findViewById(R.id.sideImageText11);
                name = n + ": " + name;
                break;
            case 12:
                text = findViewById(R.id.sideImageText12);
                name = n + ": " + name;
                break;
            default:
                return;
        }
        text.setVisibility(View.VISIBLE);
        text.setText(name);
    }
    public void navigateToConnectDevices() {
        Toast toast = Toast.makeText(getApplicationContext(), "Now Pairing to Devices", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        connectDevices();
    }

    //return true if new device already exists in list of found devices
    public boolean checkDuplicate(BluetoothDevice device) {
        int len = this.discoveredDevices.size();
        for(int i = 0; i < len; i++) {
            if(this.discoveredDevices.get(i).equals(device)) {
                return true;
            }
        }
        return false;
    }

    //add new device to list if it is not duplicate
    public void addDeviceToList(BluetoothDevice device) {
        if(!checkDuplicate(device)) {
            this.discoveredDevices.add(device);
            setDeviceCards();
        }
    }

    //method to ask user for making device discoverable
    public void requestDiscovery() {
        Intent discover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discover.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1800);
        startActivity(discover);
    }

    //method to check current scan mode of device
    public int checkScanMode() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        int mode = bluetoothAdapter.getScanMode();
        if(mode == BluetoothAdapter.SCAN_MODE_NONE) {
            return 0;
        }
        else if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
            return 1;
        }
        else if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            return 2;
        }
        else {
            return -1;
        }
    }

    //check if user switches off location, and stop discovery process
    private final BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                cancelDiscovery();
            }
        }
    };

    //If state of bluetooth adapter is changed, stop discovery process
    private final BroadcastReceiver bt_state_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                //stop discovery on change, if ongoing
                cancelDiscovery();
                //if scan mode is connectable, but not discoverable
                //request permission for discovery
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    int mode = checkScanMode();
                    if(mode == 0 || mode == 1) {
                        requestDiscovery();
                    }
                }
            }
        }
    };

    //If scan mode of bluetooth is changed, stop discovery process
    private final BroadcastReceiver discovery_state_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                cancelDiscovery();
            }
        }
    };

    //Once discovery process is complete, switch off animations
    private final BroadcastReceiver discovery_ending = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                cancelDiscovery();
                navigateToConnectDevices();
            }
        }
    };


    //Once new device is found during discovery process, add it to the list
    private final BroadcastReceiver device_found_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null) {
                    addDeviceToList(device);
                    if(device.getName() != null) {
                        setSidePhoneVisibility(device.getName());
                    }
                    else {
                        setSidePhoneVisibility("Null");
                    }
                }
            }
        }
    };


    //This next section of functions pertains to connecting any and all discovered devices
    //and saving them in an arraylist to later be brought back to the MainActivity
    public void connectDevices() {
        //start server for listening to incoming connections
        startServer(Constants.serverChannel++);
        this.pairedDevices = new ArrayList<>();
        //display cards of discovered devices
        setDeviceCards();
        //register Broadcast Receiver for new device found
        IntentFilter deviceConnected = new IntentFilter();
        registerReceiver(deviceConnectedReceiver, deviceConnected);
        TextView pairedDevices;
        pairedDevices = findViewById(R.id.pairedDeviceCount);
        String displayText = "PAIRED DEVICES: ";
        pairedDevices.setText(displayText);
        //Make FAB visible
        FloatingActionButton next = findViewById(R.id.navigateTexting);
        next.setVisibility(View.VISIBLE);
        //Start pairing to devices. Once pairing is complete,
        //automatically return to MainActivity
        startPairing();
    }

    //method to set cards visible & set text
    void setDeviceCards() {
        int len = this.discoveredDevices.size();
        TextView text;
        //Set count for discovered devices
        text = findViewById(R.id.discoveredDeviceCount);
        String displayText = "DISCOVERED DEVICES: " + len;
        text.setText(displayText);
    }

    void handleConnectionSuccess(BluetoothDevice device) {
        if(this.discoveredDevices.contains(device)) {
            setConnectionStatusSuccess();
        }
        else {
            addServerPairedDeviceCard();
        }
    }

    void addServerPairedDeviceCard() {
        TextView text;
        //set count of paired devices
        text = findViewById(R.id.pairedDeviceCount);
        String displayText = "PAIRED DEVICES: " + this.pairedDevices.size();
        text.setText(displayText);
    }

    //method to set paired devices to connected status
    void setConnectionStatusSuccess() {
        //set count of paired devices
        TextView text = findViewById(R.id.pairedDeviceCount);
        String displayText = "PAIRED DEVICES: " + this.pairedDevices.size();
        text.setText(displayText);
        connection = true;
    }

    public void startPairing() {
        int len = this.discoveredDevices.size();
        //loop through ArrayList of discovered devices and attempt to establish paired status
        for(int i = 0; i < len; i++) {
            final BluetoothDevice currentDevice = this.discoveredDevices.get(i);
            //if device has already been paired, move on to next device
            if(this.pairedDevices.contains(currentDevice)) {
                continue;
            }
            //register handler to set connection status in case of successful connection
            Handler clientHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == Constants.CLIENT_DEVICE_INFO) {
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        addDeviceToPairedList(device);
                        setConnectionStatusSuccess();
                        Toast toast = Toast.makeText(getApplicationContext(), "Now paired to " + device.getName(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        goToNextClass();
                    }
                }
            };
            //start thread to establish connection
            BluetoothClient bluetoothClient = new BluetoothClient(currentDevice, clientHandler, Constants.UUID_1);
            bluetoothClient.start();
        }
    }

    //method to start server in background to listen for incoming connections
    public void startServer(final int serverChannel) {
        //register handler to take action
        Handler serverHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {

                    //in case of successful connection, restart server to allow new connections
                    case Constants.SERVER_DEVICE_CONNECTED:
                        startServer(Constants.serverChannel++);
                        break;

                    //set connection status of successfully connected device, and add it to list of paired devices
                    case Constants.SERVER_DEVICE_INFO:
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        addDeviceToPairedList(device);
                        handleConnectionSuccess(device);
                        break;
                }
            }
        };
        //start thread to listen for connections
        BluetoothServer bluetoothServer = new BluetoothServer(serverHandler, serverChannel, Constants.UUID_1);
        bluetoothServer.start();
        this.serverThread = bluetoothServer;
    }

    public void returnToMain(View view) {
        //close server thread to prevent accepting new connections
        this.serverThread.interrupt();
        //navigate to TextInterface and send list of paired devices
        Intent intent = new Intent();
        // Add the ArrayList<BluetoothDevice> to the Intent
        intent.putExtra("deviceList", this.pairedDevices);
        // Set the result to be sent back to the previous activity
        setResult(MainActivity.RESULT_OK, intent);
        finish();
    }

    public void goToNextClass() {
        //close server thread to prevent accepting new connections
        this.serverThread.interrupt();
        //navigate to TextInterface and send list of paired devices
        Intent intent = new Intent();
        // Add the ArrayList<BluetoothDevice> to the Intent
        intent.putExtra("deviceList", this.pairedDevices);
        // Set the result to be sent back to the previous activity
        setResult(MainActivity.RESULT_OK, intent);
        finish();
    }

    //register receiver for new device connection and set connection status to successful
    private final BroadcastReceiver deviceConnectedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                setConnectionStatusSuccess();
            }
        }
    };

    //method to add device to list of paired devices if it does not already exist
    public void addDeviceToPairedList(BluetoothDevice device) {
        if(!this.pairedDevices.contains(device)) {
            this.pairedDevices.add(device);
        }
    }
}
