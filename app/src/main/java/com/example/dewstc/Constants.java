package com.example.dewstc;


final class Constants {

    //Application's UUID
    static final String UUID_1 = "48ccdb6c-7bab-45f6-bd68-f7e5fb8d06fe";
    static final String UUID_2 = "527be453-f25c-4d12-8cf7-a66001ad9f25";


    //TAG for log messages
    static final String TAG = "DJ@H#BD";


    //STC constants
    static int serverChannel = 0;
    static int MAX_HOPS = 10;
    static int MAX_MESSAGE_SIZE = 4096;
    final static int MAX_ATTEMPTS = 5;


    //ints and Strings for server communication across threads
    static final int SERVER_GETTING_ADAPTER = 8500;
    static final int SERVER_CREATING_CHANNEL = 8501;
    static final int SERVER_CREATING_CHANNEL_FAIL = 8502;
    static final String SERVER_CREATING_CHANNEL_FAIL_TEXT = "Server: Could not create RFCOMM channel";
    static final int SERVER_WAITING_DEVICE = 8503;
    static final String SERVER_WAITING_DEVICE_TEXT = "Server: Waiting for device";
    static final int SERVER_ACCEPT_FAIL = 8504;
    static final String SERVER_ACCEPT_FAIL_TEXT = "Server: Socket's accept() method failed";
    static final int SERVER_DEVICE_CONNECTED = 8505;
    static final int SERVER_SOCKET_CLOSE_FAIL = 8507;
    static final String SERVER_SOCKET_CLOSE_FAIL_TEXT = "Server: Could not close socket";


    //ints and Strings for client communication across threads
    static final int CLIENT_CREATING_CHANNEL = 8508;
    static final int CLIENT_CREATING_CHANNEL_FAIL = 8509;
    static final int CLIENT_ATTEMPTING_CONNECTION = 8510;
    static final int CLIENT_CONNECTED = 8511;
    static final int CLIENT_CONNECTION_FAIL = 8512;
    static final String CLIENT_CONNECTION_FAIL_TEXT = "Client: Could not connect";
    static final int CLIENT_SOCKET_CLOSE_FAIL = 8513;
    static final int CLIENT_CLOSING_SOCKET = 8514;
    static final int SERVER_DEVICE_INFO = 8515;
    static final int CLIENT_DEVICE_INFO = 8516;
    static final int SOCKET = 8517;
    static final int JSON_OBJECT_RECEIVE = 8518;
    static final int JSON_SEND_FAIL = 8519;
    static final int JSON_SENT = 8520;
    static final int JSON_RECEIVE_FAIL = 8521;
}