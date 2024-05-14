
Welcome to DewSTC - a no-frills, Bluetooth mesh messenger Android application based on naishadh14's DisCom(https://github.com/naishadh14/Discom).

DewSTC functions as a prototype disaster-resilient messenger application which holds not only the user's messages, but also any other user's.
Based on the principles of dew computing, DewSTC provides users the chance to send and relay messages when they are connected, and store messages during periods of limited connectivity.
The principles of Independance and Collaboration are key in the proper functioning of this application.

To begin, ensure your Bluetooth and location services are enabled. If not, the app will prompt them. 

![1](https://github.com/Redbaron29/DewSTC/assets/95047781/0a389481-b9e6-40b1-b020-76b4aa4106d6)


![2](https://github.com/Redbaron29/DewSTC/assets/95047781/d95829d8-8b7f-49cf-a5ac-002bd825a611)


**Please note that the maximum allowed discoverable time varies by phone. 
The Moto g6 Play phone that was used in the testing phase had a 30 minute limit. Then it required re-enabling of this feature
If no devices are being detected, click the "Refresh" button or restart app (last resort).

DewSTC initializes immediately upon starting the application and begins advertizing and build an arraylist of paired devices to relay all messages to. The discover and pairing process takes around 2 minutes if in a densely populated area of pairable devices running bluetooth. Once the first device is paired, any pending messages will begin to send off within 2 minutes.
***Please note, after initializing the discovery process, devices may still pair with yours on an ongoing basis.

Before sending your first message, please click on "Enter your phone number" and enter a 10-digit number (no dashes or special characters).

![IMG_4685](https://github.com/Redbaron29/DewSTC/assets/95047781/b0d4a6ef-4f06-45ac-81fc-cb47aa6c0b15)


***Please note, this is just suppose to simulate a user's actual phone number. In the future, we aim to use the device's SIM card to extract the actual phone number.
Also, note that some 10-digit numbers cause sporadic errors to occur. These typically come from 10 consecutive numbers like 1111111111 or 0000000000. Do not use these.

Once you saved your made-up phone number, type in a 10-digit number for your recipient then your message and hit Send.

![6](https://github.com/Redbaron29/DewSTC/assets/95047781/5c980e98-b964-4579-9340-67aaf94fd53c)


It will attempt to send the message.
If the message sends successfully, you will receive notice.

![7](https://github.com/Redbaron29/DewSTC/assets/95047781/6e45d4f1-a00a-453f-9de1-2db32157957e)


If the receiving device is not your intended recipient (by phone number), that device will save a copy to its Outbox and immediately attempt to broadcast your message to their list of paired devices. 
If you receive a message intended for another user, the same process occurs. You will save a copy to your Outbox and immediately attempt to rebroadcast it.

![9](https://github.com/Redbaron29/DewSTC/assets/95047781/e5997141-19bd-4d68-ba7d-9f4709c0aed5)


All successfully received messages intended for yourself will be stored in your Inbox. Upon receiving a message in your Inbox, STC will create an ack message to send back out, with the goal of reaching the initial sender to let them know their message was successfully received. This ack will rest in your Outbox and any other user's Outbox, replacing the original sent message.
***Please note, what shall remain after successfully received messages will be an Outbox filled with ack messages which will auto-delete after 24hrs. This ensures the Outbox does not get congested. This arbitrary timer can be adjusted as necessary.

![11](https://github.com/Redbaron29/DewSTC/assets/95047781/1cf37b51-4093-4d3f-ad10-0ee7e76d0ac8)


Please note, all Outbox messages, regardless of intended recipient, received will be visible to you. In future versions of this app, the Outbox can be divided into the visible user's pending messages and a hidden version for other users' pending messages (for security purposes).

![12](https://github.com/Redbaron29/DewSTC/assets/95047781/a61376f9-3814-426e-95ca-70b9a0326205)

