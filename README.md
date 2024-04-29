
Welcome to DewSTC - a no-frills, Bluetooth mesh messenger Android application based on naishadh14's DisCom(https://github.com/naishadh14/Discom).

DewSTC functions as a prototype disaster-resilient messenger application which holds not only the user's messages, but also any other user's.
Based on the principles of dew computing, DewSTC provides users the chance to send and relay messages when they are connected, and store messages during periods of limited connectivity.
The principles of Independance and Collaboration are key in the proper functioning of this application.

To begin, ensure your Bluetooth and location services are enabled. If not, the app will prompt them. 

![1](https://github.com/Redbaron29/DewSTC/assets/95047781/0a389481-b9e6-40b1-b020-76b4aa4106d6)


![2](https://github.com/Redbaron29/DewSTC/assets/95047781/d95829d8-8b7f-49cf-a5ac-002bd825a611)


**Please note that the maximum allowed discoverable time varies by phone. 
The Moto g6 Play phone that was used in the testing phase had a 1hr limit. Then it required re-enabling of this feature
If no devices are being detected, close the app and restart it.

![IMG_4686](https://github.com/Redbaron29/DewSTC/assets/95047781/48f664f6-ec4d-4f1e-882a-2546fb149255)


DewSTC initializes immediately upon starting the application and begins advertizing and build an arraylist of paired devices to relay all messages to. The discover and pairing process takes around 2 minutes if in a densely populated area of pairable devices running bluetooth. Once the first device is paired, any pending messages will begin to send off within 2 minutes.
***Please note, even though the DiscoverandConnectActivity has finished, more devices may still pair with yours.

Before sending your first message, please click on "Enter your phone number" and enter a 10-digit number (no dashes or special characters).

![IMG_4685](https://github.com/Redbaron29/DewSTC/assets/95047781/b0d4a6ef-4f06-45ac-81fc-cb47aa6c0b15)


Please note, this is just suppose to simulate a user's actual phone number. In the future, we aim to use the device's SIM card to extract the actual phone number.
Also, note that some 10-digit numbers cause sporadic errors to occur. These typically come from 10 consecutive numbers like 1111111111 or 0000000000. Do not use these.

Once you saved your made-up phone number, type in a 10-digit number for your recipient, and type in a message. Hit send.

![6](https://github.com/Redbaron29/DewSTC/assets/95047781/5c980e98-b964-4579-9340-67aaf94fd53c)


It will attempt to send the message.
If the message sends successfully, you will receive notice.

![7](https://github.com/Redbaron29/DewSTC/assets/95047781/6e45d4f1-a00a-453f-9de1-2db32157957e)


If the recipient is not your intended recipient (by phone number), they will immediately attempt to broadcast the message to their remaining paired devices. 
If you receive a message intended for another user, the same process occurs. If for any reason you run out of paired devices to attempt to re-broadcast to, your device will store the message in its Outbox.

![9](https://github.com/Redbaron29/DewSTC/assets/95047781/e5997141-19bd-4d68-ba7d-9f4709c0aed5)


All successfully received messages intended for yourself will be stored in your Inbox.

![11](https://github.com/Redbaron29/DewSTC/assets/95047781/1cf37b51-4093-4d3f-ad10-0ee7e76d0ac8)


Please note, all Outbox messages, regardless of intended recipient, received will be visible to you. In future versions of this app, the Outbox will remain out of user's sight for security purposes.

![12](https://github.com/Redbaron29/DewSTC/assets/95047781/a61376f9-3814-426e-95ca-70b9a0326205)

