package com.example.dewstc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dewstc.database.InboxTable.TextMessageForMe;
import com.example.dewstc.database.InboxTable.TextMessageForMeViewModel;
import com.example.dewstc.database.OutboxTable.TextMessage;
import com.example.dewstc.database.OutboxTable.TextMessageViewModel;
import com.example.dewstc.database.PhoneNumberTable.Number;
import com.example.dewstc.database.PhoneNumberTable.NumberListAdapter;
import com.example.dewstc.database.PhoneNumberTable.NumberViewModel;
import com.example.dewstc.database.ReceivedTable.ReceivedMessage;
import com.example.dewstc.database.ReceivedTable.ReceivedMessageViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_NUMBER_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_NUMBER_ACTIVITY_REQUEST_CODE = 2;
    public static final String EXTRA_DATA_UPDATE_NUMBER = "extra_number_to_be_updated";
    public static final String EXTRA_DATA_ID = "extra_data_id";
    ArrayList<BluetoothDevice> discoveredDevices;
    ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
    JSONObject jsonObject, jsonObjectACK;
    long selfNumber;
    public NumberViewModel numberViewModel;
    TextMessageViewModel textMessageViewModel;
    TextMessageForMeViewModel textMessageForMeViewModel;
    ReceivedMessageViewModel receivedMessageViewModel;
    int REQUEST_ENABLE_BT;
    BluetoothServer serverThread;
    Boolean connection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Set up the RecyclerView.
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final NumberListAdapter adapter = new NumberListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextView text11 = findViewById(R.id.textView11);
        text11.setMovementMethod(new ScrollingMovementMethod());

        //Set up arraylist
        this.discoveredDevices = new ArrayList<>();

        // Set up the NumberViewModel
        numberViewModel = ViewModelProviders.of(this).get(NumberViewModel.class);
        // Get all the numbers from the database and associate them to the adapter.
        numberViewModel.getAllNumbers().observe(this, new Observer<List<Number>>() {
            @Override
            public void onChanged(@Nullable final List<Number> numbers) {
                // Update the cached copy of the numbers in the adapter.
                adapter.setNumbers(numbers);
            }
        });

        adapter.setOnItemClickListener(new NumberListAdapter.ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Number number = adapter.getNumberAtPosition(position);
                launchUpdateNumberActivity(number);
            }
        });
        textMessageViewModel = ViewModelProviders.of(this).get(TextMessageViewModel.class);
        textMessageForMeViewModel = ViewModelProviders.of(this).get(TextMessageForMeViewModel.class);
        receivedMessageViewModel = ViewModelProviders.of(this).get(ReceivedMessageViewModel.class);
        //check if location permission enabled, if not, ask for permission
        Access.checkPermissions(getApplicationContext(), this);

        // Schedule the server to run every 2 minutes (120,000 milliseconds)
        Timer send = new Timer();
        send.schedule(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        sendPendingMessages();
                    }
                });
            }
        }, 0, 120000);

        // Schedule the server to run every day (86,400,000 milliseconds)
        Timer delete = new Timer();
        delete.schedule(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        deleteReceivedList();
                    }
                });
            }
        }, 0, 86400000);


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
        if (Access.isMyLocationOn(context)) {
            Access.displayLocationSettingsRequest(context, this);
            finish();
        }
        //If Bluetooth is not enabled, request
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            finish();
        }
        startServer();
        startDiscovery();
    }

    public void makeMessage(View view) {
        if (numberViewModel.getMyNumber().getMyNumber().length() != 10) {
            Toast toast = Toast.makeText(getApplicationContext(), "You must enter your own number", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        TextView text11 = findViewById(R.id.textView11);
        //get values from user input
        EditText text = findViewById(R.id.editText2);
        String number_text = text.getText().toString();
        EditText copies = findViewById(R.id.editText22);
        String copy = copies.getText().toString();
        //check for 10 digits
        if (number_text.length() != 10) {
            Toast toast = Toast.makeText(getApplicationContext(), "Phone Number must be 10 digits", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //check that recipient number is not self number
        this.selfNumber = Long.parseLong(numberViewModel.getMyNumber().getMyNumber());
        String selfNumberText = Long.toString(this.selfNumber);
        if (selfNumberText.equals(number_text)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Recipient Number cannot be your own number", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (copy.equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter the number of copies", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        int copy_number = Integer.parseInt(copy);
        long number = Long.parseLong(number_text);
        text = findViewById(R.id.editText3);
        String message = text.getText().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long time = timestamp.getTime();
        String t = String.valueOf(time);

        /*
        Make JSONObject consisting of
        1. Timestamp
        2. Recipient Number
        3. Sender Number
        4. Message
        5. List of BluetoothDevices msg has hopped to (JSONArray)
        6. Max number of Hops
        */
        JSONObject jsonObject = new JSONObject();
        this.jsonObject = jsonObject;
        try {
            jsonObject.put("Timestamp", time); //serves as message ID
            jsonObject.put("Recipient", number);
            jsonObject.put("Sender", this.selfNumber);
            jsonObject.put("Message", message);
            jsonObject.put("CurrentHops", 1);
            //make list of bluetooth device for hops list in JSONArray

        } catch (JSONException e) {
            Log.e(Constants.TAG, "JSON object construction error");
        }
        String jsonText = jsonObject.toString();
        text11.append(jsonText);
        text11.append("\n");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byte[] encodedJSON = Base64.getEncoder().encode(jsonText.getBytes());
            byte[] decodedJSON = Base64.getDecoder().decode(encodedJSON);
        } else {
            byte[] encodedJSON = android.util.Base64.encode(jsonText.getBytes(), android.util.Base64.DEFAULT);
            byte[] decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
        }
        //Save timestamp to receivedMessages table so it does not return
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String macAddress = bluetoothAdapter.getAddress();
        receivedMessageViewModel.insert(new ReceivedMessage(t, macAddress));

        //Save to Outbox X times for redundancy. This means it will send out the message to every paired nodes X times.
        for (int i = 0; i < copy_number; ++i) {
            textMessageViewModel.insert(new TextMessage(t, jsonText));
        }

        //Send message out to devices
        text11.append("Sending message to available paired devices\n");
        startClient(jsonObject, 0, 0, null);
    }

    public void startServer() {
        final TextView text = findViewById(R.id.textView11);
        BluetoothDevice device;
        Handler serverHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch (msg.what) {
                    case Constants.SERVER_CREATING_CHANNEL_FAIL:
                        text.append(Constants.SERVER_CREATING_CHANNEL_FAIL_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_WAITING_DEVICE:
                        text.append(Constants.SERVER_WAITING_DEVICE_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_ACCEPT_FAIL:
                        text.append(Constants.SERVER_ACCEPT_FAIL_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_DEVICE_CONNECTED:
                        startServer();
                        break;
                    case Constants.SERVER_SOCKET_CLOSE_FAIL:
                        text.append(Constants.SERVER_SOCKET_CLOSE_FAIL_TEXT);
                        text.append("\n");
                        break;
                    case Constants.SERVER_DEVICE_INFO:
                        BluetoothDevice tmp = (BluetoothDevice) msg.obj;
                        text.append("Server: Connected to " + tmp.getName());
                        text.append("\n");
                        break;
                    case Constants.SOCKET:
                        BluetoothSocket socket = (BluetoothSocket) msg.obj;
                        final BluetoothDevice receivedDevice = socket.getRemoteDevice();
                        Handler messageHandler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == Constants.JSON_OBJECT_RECEIVE) {
                                    JSONObject jsonObject = (JSONObject) msg.obj;
                                    String displayText = "Message received from " + receivedDevice.toString() + "\n";
                                    text.append(displayText);
                                    try {
                                        messageResponse(jsonObject, receivedDevice);
                                    } catch (JSONException e) {
                                        text.append("JSON Exception " + e);
                                    }
                                } else if (msg.what == Constants.JSON_RECEIVE_FAIL) {
                                    text.append("Error receiving message.\n");
                                }
                            }
                        };
                        MessageServer messageServer = new MessageServer(socket, messageHandler);
                        messageServer.start();
                        break;
                    default:
                        break;
                }
            }
        };
        BluetoothServer bluetoothServer = new BluetoothServer(serverHandler, 0, Constants.UUID_2);
        bluetoothServer.start();
    }

    public void startClient(final JSONObject jsonObject, final int deviceNumber, final int attemptNumber, final BluetoothDevice ignoreDevice) {
        int len = this.pairedDevices.size();
        if (deviceNumber >= len)
            return;
        if (attemptNumber >= Constants.MAX_ATTEMPTS)
            return;
        if (ignoreDevice != null && this.pairedDevices.get(deviceNumber).getAddress().equals(ignoreDevice.getAddress())) {
            startClient(jsonObject, deviceNumber + 1, 0, ignoreDevice);
            return;
        }
        //if this message's original sender = the same sender from a message we received earlier,
        //skip sending it back to them and instead send it to next paired devices.
        try {
            TextView text = findViewById(R.id.textView11);
            long time = (long) jsonObject.get("Timestamp");
            String t = String.valueOf(time);
            //insert ack message deletion method here

            if (receivedMessageViewModel.searchForTimestamp(t) != null) {
                if (receivedMessageViewModel.searchForTimestamp(t).getAddress().equals(this.pairedDevices.get(deviceNumber).getAddress())) {
                    if (ignoreDevice != null) {
                        text.append("Already sent/received this message to/from: " + ignoreDevice.getAddress() + "\nSending to next paired device\n");
                    }
                    startClient(jsonObject, deviceNumber + 1, 0, ignoreDevice);
                    return;
                }
            } else {
                text.append("Timestamp does not exist in received_messages table\nSending message\n");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        final TextView text = findViewById(R.id.textView11);
        String displayText = "Sending to " + this.pairedDevices.get(deviceNumber).toString() + "\n";
        text.append(displayText);
        Handler clientHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch (msg.what) {
                    case Constants.CLIENT_CONNECTION_FAIL:
                        text.append(Constants.CLIENT_CONNECTION_FAIL_TEXT);
                        text.append("\n");
                        startClient(jsonObject, deviceNumber, attemptNumber + 1, ignoreDevice);
                        break;
                    case Constants.CLIENT_DEVICE_INFO:
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        text.append("Client: Connected to " + device.getName());
                        text.append("\n");
                        break;
                    case Constants.SOCKET:
                        BluetoothSocket socket = (BluetoothSocket) msg.obj;
                        Handler messageHandler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == Constants.JSON_SEND_FAIL) {
                                    text.append("Message sending failed\n");
                                    startClient(jsonObject, deviceNumber, attemptNumber + 1, ignoreDevice);
                                } else {
                                    text.append("Message sent successfully\n");
                                    TextMessage sentText = textMessageViewModel.getMyLatestMessage();
                                    String g = sentText.getTimestamp();
                                    //Add sent message to ReceivedMessages to ensure copies of messages are not resent to same address
                                    if (ignoreDevice != null) {
                                        receivedMessageViewModel.insert(new ReceivedMessage(g, ignoreDevice.getAddress()));
                                    }
                                    //Delete this message from Outbox
                                    textMessageViewModel.deleteMessage(sentText);
                                    startClient(jsonObject, deviceNumber + 1, 0, ignoreDevice);
                                }
                            }
                        };
                        MessageClient messageClient = new MessageClient(socket, jsonObject, messageHandler);
                        messageClient.start();
                        break;
                    default:
                        break;
                }
            }
        };
        BluetoothClient bluetoothClient = new BluetoothClient(this.pairedDevices.get(deviceNumber), clientHandler, Constants.UUID_2);
        bluetoothClient.start();
    }

    void messageResponse(JSONObject jsonObject, BluetoothDevice receivedDevice) throws JSONException {
        this.selfNumber = Long.parseLong(numberViewModel.getMyNumber().getMyNumber());
        final TextView text = findViewById(R.id.textView11);
        long number = (long) jsonObject.get("Recipient");
        long senderNumber = (long) jsonObject.get("Sender");
        String ackTime = (String) jsonObject.get("Message");

        //make list of timestamps, if msg already received, drop
        long time = (long) jsonObject.get("Timestamp");
        String t = String.valueOf(time);

        //Check if the timestamp already exists in Received table
        if (receivedMessageViewModel.searchForTimestamp(t) != null) {
            text.append("Already received, dropping\n");
            return;
        }
        //Check if the ack timestamp already exists in Received table
        if (receivedMessageViewModel.searchForTimestamp(ackTime) != null) {
            text.append("ACK received. Deleting Outbox message copies\n");
            if (textMessageViewModel.searchForTimestamp(ackTime) != null) {
                TextMessage ackMessage = textMessageViewModel.searchForTimestamp(ackTime);
                textMessageViewModel.deleteMessage(ackMessage);
                text.append("Message copies DELETED\n");
            }
            return;
        }
        String senderAddress = receivedDevice.getAddress();
        receivedMessageViewModel.insert(new ReceivedMessage(t, senderAddress));
        receivedMessageViewModel.insert(new ReceivedMessage(ackTime, senderAddress));
        text.append("Added new message from " + senderAddress + " to received_messages table\n");
        //if you are intended recipient, do not broadcast further
        if (Long.toString(number).equals(Long.toString(this.selfNumber))) {
            text.append("You are the intended recipient\n");
            text.append((String) jsonObject.get("Message"));
            text.append("\n");
            //add message to Inbox database
            textMessageForMeViewModel.insert(new TextMessageForMe(t, jsonObject.toString()));
            //send ack back to originating sender. This will delete all pending Outbox copies that may exist
            text.append("Sending out ACK to " + senderNumber + "\n");
            Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
            long newTime = timestamp1.getTime();
            String newTimestamp = String.valueOf(newTime);
            JSONObject jsonObjectACK = new JSONObject();
            this.jsonObjectACK = jsonObjectACK;
            try {
                jsonObjectACK.put("Timestamp", newTime);
                jsonObjectACK.put("Recipient", senderNumber);
                jsonObjectACK.put("Sender", number);
                jsonObjectACK.put("Message", t);
                jsonObjectACK.put("CurrentHops", 1);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String jsonACKText = jsonObjectACK.toString();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                byte[] encodedJSON = Base64.getEncoder().encode(jsonACKText.getBytes());
                byte[] decodedJSON = Base64.getDecoder().decode(encodedJSON);
            } else {
                byte[] encodedJSON = android.util.Base64.encode(jsonACKText.getBytes(), android.util.Base64.DEFAULT);
                byte[] decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
            }
            textMessageViewModel.insert(new TextMessage(newTimestamp, jsonACKText));
            return;
        }
        //if max hops are reached, then drop packet
        int hops = (int) jsonObject.get("CurrentHops");
        if (Constants.MAX_HOPS == hops + 1) {
            return;
        }
        //otherwise increment current hops by 1 and add to Outbox
        jsonObject.put("CurrentHops", hops + 1);
        textMessageViewModel.insert(new TextMessage(t, jsonObject.toString()));
        //broadcast message back to network, but not back to the "receivedDevice"
        text.append("Broadcasting message back to network\n");
        startClient(jsonObject, 0, 0, receivedDevice);
    }

    public void showMessages(View view) {
        Intent intent = new Intent(MainActivity.this, InboxActivity.class);
        startActivity(intent);
    }

    public void showSentMessages(View view) {
        Intent intent = new Intent(MainActivity.this, OutboxActivity.class);
        startActivity(intent);
    }

    public static void triggerRestart(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
        System.exit(0);
    }

    public void clear(View view) {
        triggerRestart(MainActivity.this);
    }


    /**
     * When the user enters a new number in the NewNumberActivity,
     * that activity returns the result to this activity.
     * If the user entered a new number, save it in the database.
     *
     * @param requestCode ID for the request
     * @param resultCode  indicates success or failure
     * @param data        The Intent sent back from the NewNumberActivity,
     *                    which includes the number that the user entered
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_NUMBER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Number number = new Number(data.getStringExtra(NewNumberActivity.EXTRA_REPLY));
            // Save your phone number to the database
            numberViewModel.insert(number);
        } else if (requestCode == UPDATE_NUMBER_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            String word_data = data.getStringExtra(NewNumberActivity.EXTRA_REPLY);
            int id = data.getIntExtra(NewNumberActivity.EXTRA_REPLY_ID, -1);

            if (id != -1) {
                numberViewModel.update(new Number(id, word_data));
            } else {
                Toast.makeText(this, R.string.unable_to_update,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void launchUpdateNumberActivity(Number number) {
        Intent intent = new Intent(this, NewNumberActivity.class);
        intent.putExtra(EXTRA_DATA_UPDATE_NUMBER, number.getMyNumber());
        intent.putExtra(EXTRA_DATA_ID, number.getId());
        startActivityForResult(intent, UPDATE_NUMBER_ACTIVITY_REQUEST_CODE);
    }

    public void sendPendingMessages() {
        if (!textMessageViewModel.isTableEmpty()) {
            try {
                JSONObject jsonObject1 = new JSONObject(textMessageViewModel.getMyLatestMessage().getTextMessage());
                startClient(jsonObject1, 0, 0, null);

            } catch (JSONException err) {
                Log.d("Error", err.toString());
            }
        }
    }

    public void deleteReceivedList() {
        if (!receivedMessageViewModel.isTableEmpty()) {
            receivedMessageViewModel.deleteAll();
        }
    }

    void startDiscovery() {
        this.discoveredDevices.clear();
        TextView text = findViewById(R.id.textView11);
        text.append("Starting Discovery. Please wait...");
        text.append("\n");
        startLocalDiscovery();
    }

    public void startLocalDiscovery() {
        Context context = getApplicationContext();
        //if location is off, quit discovery and request again
        if (Access.isMyLocationOn(context)) {
            cancelDiscovery();
            Toast toast = Toast.makeText(context, "Please switch on Location and retry", Toast.LENGTH_SHORT);
            toast.show();
            Access.displayLocationSettingsRequest(context, this);
            return;
        }
        //if bluetooth is off, quit discovery and request again
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            cancelDiscovery();
            Toast toast = Toast.makeText(context, "Please switch on Bluetooth and retry", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        //if not discoverable, request for discovery
        if (checkScanMode() != 2) {
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
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        boolean flag = bluetoothAdapter.startDiscovery();
        //bluetoothAdapter.startDiscovery();
        //if discovery failed to start, show toast message
        if (!flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "Discovery failed: Please make sure Bluetooth and Location are on before retrying.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Access.displayLocationSettingsRequest(getApplicationContext(), this);
        }
    }

    //method to stop discovering if discovery is on
    public void cancelDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public void navigateToConnectDevices() {
        TextView text = findViewById(R.id.textView11);
        text.append("Discovery complete. " + discoveredDevices.size() + " devices found.");
        text.append("\n");
        text.append("Starting Pairing. Please wait...");
        text.append("\n");
        connectDevices();
    }

    //return true if new device already exists in list of found devices
    public boolean checkDuplicate(BluetoothDevice device) {
        int len = this.discoveredDevices.size();
        for (int i = 0; i < len; i++) {
            if (this.discoveredDevices.get(i).equals(device)) {
                return true;
            }
        }
        return false;
    }

    //add new device to list if it is not duplicate
    public void addDeviceToList(BluetoothDevice device) {
        if (!checkDuplicate(device)) {
            this.discoveredDevices.add(device);
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
        if (mode == BluetoothAdapter.SCAN_MODE_NONE) {
            return 0;
        } else if (mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
            return 1;
        } else if (mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            return 2;
        } else {
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
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    int mode = checkScanMode();
                    if (mode == 0 || mode == 1) {
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

    //Once discovery process is complete, end search
    private final BroadcastReceiver discovery_ending = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                cancelDiscovery();
                navigateToConnectDevices();
                TextView text = findViewById(R.id.textView11);
                text.append("Attempting to pair to discovered devices...");
                text.append("\n");
            }
        }
    };


    //Once new device is found during discovery process, add it to the list
    private final BroadcastReceiver device_found_receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    addDeviceToList(device);
                }
            }
        }
    };


    //This next section of functions pertains to connecting any and all discovered devices
    //and saving them in an arraylist to later be brought back to the MainActivity
    public void connectDevices() {
        //start server for listening to incoming connections
        startServer(Constants.serverChannel++);
        //register Broadcast Receiver for new device found
        IntentFilter deviceConnected = new IntentFilter();
        registerReceiver(deviceConnectedReceiver, deviceConnected);
        //Start pairing to devices. Once pairing is complete, return
        startPairing();
    }

    void handleConnectionSuccess(BluetoothDevice device) {
        if (this.discoveredDevices.contains(device)) {
            setConnectionStatusSuccess();
            TextView text = findViewById(R.id.textView11);
            text.append("Connection Success!");
            text.append("\n");
        }
    }

    //method to set paired devices to connected status
    void setConnectionStatusSuccess() {
        connection = true;
    }

    public void startPairing() {
        int len = this.discoveredDevices.size();
        //loop through ArrayList of discovered devices and attempt to establish paired status
        for (int i = 0; i < len; i++) {
            final BluetoothDevice currentDevice = this.discoveredDevices.get(i);
            //if device has already been paired, move on to next device
            if (this.pairedDevices.contains(currentDevice)) {
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
                        TextView text = findViewById(R.id.textView11);
                        text.append("Now paired to " + device.getName());
                        text.append("\n");
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
                switch (msg.what) {

                    //in case of successful connection, restart server to allow new connections
                    case Constants.SERVER_DEVICE_CONNECTED:
                        startServer(Constants.serverChannel++);
                        TextView text = findViewById(R.id.textView11);
                        text.append("Allowing for additional connections...");
                        text.append("\n");
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

    //register receiver for new device connection and set connection status to successful
    private final BroadcastReceiver deviceConnectedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                setConnectionStatusSuccess();
            }
        }
    };

    //method to add device to list of paired devices if it does not already exist
    public void addDeviceToPairedList(BluetoothDevice device) {
        if (!this.pairedDevices.contains(device)) {
            this.pairedDevices.add(device);
            TextView text = findViewById(R.id.textView11);
            text.append("Paired device added to list...");
            text.append("\n");
        }
    }

}