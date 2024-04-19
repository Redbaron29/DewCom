
Welcome to DewSTC - a no-frills, Bluetooth mesh messenger Android application based on naishadh14's DisCom(https://github.com/naishadh14/Discom).

DewSTC functions as a prototype disaster-resilient messenger application which holds not only the user's messages, but also any other user's.
Based on the principles of dew computing, DewSTC provides users the chance to send and relay messages when they are connected, and store messages during periods of limited connectivity.
The principles of Independance and Collaboration are key in the proper functioning of this application.

To begin, ensure your Bluetooth and location services are enabled. If not, the app will prompt them. 

![IMG_4689](https://github.com/Redbaron29/DewSTC/assets/95047781/6f4746a9-ede8-4bf4-a4e3-d9b0dd7fa368)

Click "Dew it" to begin discovering devices and pairing. Please note that the maximum allowed discoverable time varies by phone. 
The Moto g6 Play phone that was used in the testing phase had a 1hr limit. Then it required re-enabling of this feature
If no devices are being detected, close the app and restart it.

![IMG_4686](https://github.com/Redbaron29/DewSTC/assets/95047781/48f664f6-ec4d-4f1e-882a-2546fb149255)

Upon finishing the discover process, the activity will immediately and automatically begin pairing to these devices.

![IMG_4690](https://github.com/Redbaron29/DewSTC/assets/95047781/3309390f-39e3-458e-a66d-138d83b8b2f3)

Once the first device is paired, it should automatically return to the MainActivity with a list of all discovered and paired devices. 

![IMG_4698](https://github.com/Redbaron29/DewSTC/assets/95047781/0e9fd475-1a8b-4bc0-9852-e0d9c54643da)

Please note, even though the DiscoverandConnectActivity has finished, more devices may still pair with yours (as seen below in the phone on the right).

![IMG_4703](https://github.com/Redbaron29/DewSTC/assets/95047781/5b6931a0-6a2b-42b6-a3bb-326f7c5bb623)

If for any reason your device shows the "Paired Devices:" with a count of 1 or greater, simply click the arrow button to return to the MainActivity (as seen below in the phone on the right).

![IMG_4698](https://github.com/Redbaron29/DewSTC/assets/95047781/0e9fd475-1a8b-4bc0-9852-e0d9c54643da)

Before sending your first message, please click on "Enter your phone number" and enter a 10-digit number (no dashes or special characters).

![IMG_4685](https://github.com/Redbaron29/DewSTC/assets/95047781/709785a0-a4ac-480d-b301-6b9f307a8465)

Please note, this is just suppose to simulate a user's actual phone number. In the future, we aim to use the device's SIM card to extract the actual phone number.
Also, note that some 10-digit numbers cause sporadic errors to occur. These typically come from 10 consecutive numbers like 1111111111 or 0000000000. Do not use these.

Once you saved your made-up phone number, type in a 10-digit number for your recipient, and type in a message. Hit send.
This next process may take some time, as the Bluetooth server is being established and is beginning to connect to the paired devices. 
This may take up to a minute.

Assuming a device was paired with yours, you will begin to see the server work. It will attempt to send the message.
If the message sends successfully, you will receive notice.

![IMG_4707](https://github.com/Redbaron29/DewSTC/assets/95047781/23325914-7185-4cec-8b8d-2aa8316065a8)

If the recipient is not your intended recipient (by phone number), they will immediately attempt to broadcast the message to their remaining paired devices. 
If you receive a message intended for another user, the same process occurs. If for any reason you run out of paired devices to attempt to re-broadcast to, your device will store the message in its Outbox.
![IMG_4709](https://github.com/Redbaron29/DewSTC/assets/95047781/509bbe7d-b7ea-4844-9047-3c33ecbc44f1)

All successfully received messages intended for yourself will be stored in your Inbox.
![IMG_4708](https://github.com/Redbaron29/DewSTC/assets/95047781/80240d6b-1d70-472f-9f24-93f94e1cce09)
Please note, all Outbox messages, regardless of intended recipient, received will be visible to you. In future versions of this app, the Outbox will remain out of user's sight for security purposes.
