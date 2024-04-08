Welcome to DewSTC - a no-frills, Bluetooth mesh messenger Android application based on naishadh14's DisCom(https://github.com/naishadh14/Discom).

DewSTC functions as a prototype disaster-resilient messenger application which holds not only the user's messages, but also any other user's.
Based on the principles of dew computing, DewSTC provides users the chance to send and relay messages when they are connected, and store messages during periods of limited connectivity.
The principles of Independance and Collaboration are key in the proper functioning of this application.

To begin, ensure your Bluetooth and location services are enabled. If not, the app will prompt them. 
Click "Dew it" to begin discovering devices and pairing. Please note that the maximum allowed discoverable time varies by phone. 
The Moto g6 Play phone that was used in the testing phase had a 1hr limit. Then it required re-enabling of this feature
If no devices are being detected, close the app and restart it.

Upon finishing the discover process, the activity will immediately and automatically begin pairing to these devices.
Once the first device is paired, it should automatically return to the MainActivity with a list of all discovered and paired devices. 
Please note, even though the DiscoverandConnectActivity has finished, more devices may still pair with yours.
If for any reason your device shows the "Paired Devices:" with a count of 1 or greater, simply click the arrow button to return to the MainActivity.

Before sending your first message, please click on "Enter your phone number" and enter a 10-digit number (no dashes or special characters).
Please note, this is just suppose to simulate a user's actual phone number. In the future, we aim to use the device's SIM card to extract the actual phone number.
Also, note that some 10-digit numbers cause sporadic errors to occur. These typically come from 10 consecutive numbers like 1111111111 or 0000000000. Do not use these.

Once you saved your made-up phone number, type in a 10-digit number for your recipient, and type in a message. Hit send.
This next process may take some time, as the Bluetooth server is being established and is beginning to connect to the paired devices. 
This may take up to a minute.

Assuming a device was paired with yours, you will begin to see the server work. It will attempt to send the message.
If the message sends successfully, you will receive notice.
If the recipient is not your intended recipient (by phone number), they will immediately attempt to broadcast the message to their remaining paired devices. 
If you receive a message intended for another user, the same process occurs. If for any reason you run out of paired devices to attempt to re-broadcast to, your device will store the message in its Outbox.

All successfully received messages intended for yourself will be stored in your Inbox.
Please note, all Outbox messages, regardless of intended recipient, received will be visible to you. In future versions of this app, the Outbox will remain out of user's sight for security purposes.
