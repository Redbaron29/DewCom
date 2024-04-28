package com.example.dewstc;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_NUMBER_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_NUMBER_ACTIVITY_REQUEST_CODE = 2;
    public static final int RETURN_LIST_OF_DEVICES = 3;
    public static final String EXTRA_DATA_UPDATE_NUMBER = "extra_number_to_be_updated";
    public static final String EXTRA_DATA_ID = "extra_data_id";
    ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
    JSONObject jsonObject;
    long selfNumber;
    public NumberViewModel numberViewModel;
    TextMessageViewModel textMessageViewModel;
    TextMessageForMeViewModel textMessageForMeViewModel;
    ReceivedMessageViewModel receivedMessageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Set up the RecyclerView.
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final NumberListAdapter adapter = new NumberListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        adapter.setOnItemClickListener(new NumberListAdapter.ClickListener()  {
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
        Toast toast = Toast.makeText(getApplicationContext(), "Click on DEW IT to start detecting and pairing to devices", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Timer send = new Timer();
        TimerTask sendMessages = new TimerTask() {
            @Override
            public void run() {
                    sendPendingMessages();
                }
            };
        // Schedule the server to run every 2 minutes (120,000 milliseconds)
        send.schedule(sendMessages, 0, 120000);

        Timer delete = new Timer();
        TimerTask deleteList = new TimerTask() {
            @Override
            public void run() {
                deleteReceivedList();
            }
        };
        // Schedule the server to run every day (86,400,000 milliseconds)
        delete.schedule(deleteList, 0, 86400000);
        startServer();
    }

    public void launchDiscoverAndConnectActivity() {
        //Navigate to DiscoverAndConnectActivity to initialize and return list of pairedDevices
        Intent intent = new Intent(this, DiscoverAndConnectActivity.class);
        startActivityForResult(intent, RETURN_LIST_OF_DEVICES);
    }
    public void makeMessage(View view) {
        this.selfNumber = Long.parseLong(numberViewModel.getMyNumber().getMyNumber());
        TextView text11 = findViewById(R.id.textView11);
        text11.setText("");
        //get values from user input
        EditText text = findViewById(R.id.editText2);
        String number_text = text.getText().toString();
        //check for 10 digits
        if(number_text.length() != 10) {
            Toast toast = Toast.makeText(getApplicationContext(), "Phone Number must be 10 digits", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //check that recipient number is not self number
        String selfNumberText = Long.toString(this.selfNumber);
        if(selfNumberText.equals(number_text)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Recipient Number cannot be your own number", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
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
            jsonObject.put("Timestamp", time); //serves as ID for message
            jsonObject.put("Recipient", number);
            jsonObject.put("Sender", this.selfNumber);
            jsonObject.put("Message", message);
            jsonObject.put("CurrentHops", 0);
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
        }
        else {
            byte[] encodedJSON = android.util.Base64.encode(jsonText.getBytes(), android.util.Base64.DEFAULT);
            byte[] decodedJSON = android.util.Base64.decode(encodedJSON, android.util.Base64.DEFAULT);
        }

        //Save it to database X times for redundancy. This means it will send out the message to X different nodes
        textMessageViewModel.insert(new TextMessage(t, jsonText));
        textMessageViewModel.insert(new TextMessage(t, jsonText));

        //Send message out to devices
        text11.append("Sending message to available paired devices.\n");
        startClient(jsonObject, 0, 0, null);
    }

    public void startServer() {
        final TextView text = findViewById(R.id.textView11);
        BluetoothDevice device;
        Handler serverHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch(msg.what) {
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
                        Handler messageHandler = new Handler(Looper.getMainLooper()){
                            @Override
                            public void handleMessage(Message msg) {
                                if(msg.what == Constants.JSON_OBJECT_RECEIVE) {
                                    JSONObject jsonObject = (JSONObject) msg.obj;
                                    String displayText = "Message received from " + receivedDevice.toString() + "\n";
                                    text.append(displayText);
                                    try {
                                        messageResponse(jsonObject, receivedDevice);
                                    } catch (JSONException e) {
                                        text.append("JSON Exception " + e);
                                    }
                                }
                                else if(msg.what == Constants.JSON_RECEIVE_FAIL) {
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
        final TextView text = findViewById(R.id.textView11);
        String displayText = "Sending to " + this.pairedDevices.get(deviceNumber).toString() + "\n";
        text.append(displayText);
        Handler clientHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //handle cases
                switch(msg.what) {
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
                        Handler messageHandler = new Handler(Looper.getMainLooper()){
                            @Override
                            public void handleMessage(Message msg) {
                                if(msg.what == Constants.JSON_SEND_FAIL) {
                                    text.append("Message sending failed\n");
                                    try {
                                        TimeUnit.SECONDS.sleep(1);
                                    }
                                    catch(InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                    startClient(jsonObject, deviceNumber, attemptNumber + 1, ignoreDevice);
                                }
                                else {
                                    text.append("Message sent successfully\n");

                                    //Delete this message from Outbox
                                    TextMessage sentText = textMessageViewModel.getMyLatestMessage();
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

        //make list of timestamps, if msg already received, drop
        long time = (long) jsonObject.get("Timestamp");
        String t = String.valueOf(time);

        //Check if the timestamp already exists in Received table
        if (receivedMessageViewModel.searchForTimestamp(t) != null) {
            text.append("Already received, dropping\n");
            return;
        }
        receivedMessageViewModel.insert(new ReceivedMessage(t, jsonObject.toString()));
        //if you are intended recipient, do not broadcast further
        if (Long.toString(number).equals(Long.toString(this.selfNumber))) {
            text.append("You are the intended recipient\n");
            text.append((String) jsonObject.get("Message"));
            text.append("\n");
            //add message to Inbox database
            textMessageForMeViewModel.insert(new TextMessageForMe(t, jsonObject.toString()));
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
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
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

    public void clear(View view){
        TextView text = findViewById(R.id.textView11);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText("");
    }

    public void dewMode(View view){
        launchDiscoverAndConnectActivity();
    }

    /**
     * When the user enters a new number in the NewNumberActivity,
     * that activity returns the result to this activity.
     * If the user entered a new number, save it in the database.

     * @param requestCode ID for the request
     * @param resultCode indicates success or failure
     * @param data The Intent sent back from the NewNumberActivity,
     *             which includes the number that the user entered
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_NUMBER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Number number = new Number(data.getStringExtra(NewNumberActivity.EXTRA_REPLY));
            // Save your phone number to the database
            numberViewModel.insert(number);
        }
        else if (requestCode == UPDATE_NUMBER_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            String word_data = data.getStringExtra(NewNumberActivity.EXTRA_REPLY);
            int id = data.getIntExtra(NewNumberActivity.EXTRA_REPLY_ID, -1);

            if (id != -1) {
                numberViewModel.update(new Number(id, word_data));
            }
            else {
                Toast.makeText(this, R.string.unable_to_update,
                        Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == RETURN_LIST_OF_DEVICES && resultCode == RESULT_OK) {
            if (data != null) {
                // Retrieve the ArrayList<BluetoothDevice> from the Intent
                this.pairedDevices = data.getParcelableArrayListExtra("deviceList");
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

            } catch (JSONException err){
                Log.d("Error", err.toString());
            }
        }
    }
    public void deleteReceivedList() {
        if (!receivedMessageViewModel.isTableEmpty()) {
            receivedMessageViewModel.deleteAll();
        }
    }

}