/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dewstc.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.dewstc.database.InboxTable.TextMessageForMe;
import com.example.dewstc.database.InboxTable.TextMessageForMeDao;
import com.example.dewstc.database.PhoneNumberTable.Number;
import com.example.dewstc.database.PhoneNumberTable.NumberDao;
import com.example.dewstc.database.OutboxTable.TextMessage;
import com.example.dewstc.database.OutboxTable.TextMessageDao;
import com.example.dewstc.database.ReceivedTable.ReceivedMessage;
import com.example.dewstc.database.ReceivedTable.ReceivedMessageDao;

/**
 * RoomDatabase. Includes code to create the database.
 * After the dewstc creates the database, all further interactions
 * with it happen through the ViewModels.
 */

@Database(entities = {Number.class, TextMessage.class, TextMessageForMe.class, ReceivedMessage.class}, version = 2, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    public abstract NumberDao numberDao();

    public abstract TextMessageDao messageDao();

    public abstract TextMessageForMeDao messageForMeDao();

    public abstract ReceivedMessageDao receivedMessageDao();

    private static RoomDatabase INSTANCE;

    public static RoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    //RoomDatabase.class, "my_database")
                                    RoomDatabase.class, "my_database").allowMainThreadQueries()
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // This callback is called when the database has opened.
    // In this case, use PopulateDbAsync to populate the database
    // with the initial data set if the database has no entries.
    private static androidx.room.RoomDatabase.Callback sRoomDatabaseCallback =
            new androidx.room.RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    // Populate the database with the initial data sets
    // only if the database has no entries.
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final NumberDao mNumberDao;
        private final TextMessageDao mTextMessageDao;
        private final TextMessageForMeDao mTextMessageForMeDao;
        private final ReceivedMessageDao mReceivedMessageDao;


        // Initial data set
        private static final String[] numbers = {"Enter your number"};
        private static final String[] messages = {};
        private static final String[] timestamps = {};
        private static final String[] messagesForMe = {};
        private static final String[] receivedMessages = {};

        PopulateDbAsync(RoomDatabase db) {
            mNumberDao = db.numberDao();
            mTextMessageDao = db.messageDao();
            mTextMessageForMeDao = db.messageForMeDao();
            mReceivedMessageDao = db.receivedMessageDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // If we have no numbers, then create the initial list of numbers.
            if (mNumberDao.getAnyNumber().length < 1) {
                for (int i = 0; i <= numbers.length - 1; i++) {
                    Number number = new Number(numbers[i]);
                    mNumberDao.insert(number);
                }
            }
            if (mTextMessageDao.getTextMessage().length < 1) {
                for (int i = 0; i <= messages.length - 1; i++) {
                    TextMessage textMessage = new TextMessage(timestamps[i], messages[i]);
                    mTextMessageDao.insert(textMessage);
                }
            }
            if (mTextMessageForMeDao.getTextMessageForMe().length < 1) {
                for (int i = 0; i <= messagesForMe.length - 1; i++) {
                    TextMessageForMe textMessageForMe = new TextMessageForMe(timestamps[i], messagesForMe[i]);
                    mTextMessageForMeDao.insert(textMessageForMe);
                }
            }
            if (mReceivedMessageDao.getReceivedMessage().length < 1) {
                for (int i = 0; i <= receivedMessages.length - 1; i++) {
                    ReceivedMessage receivedMessage = new ReceivedMessage(timestamps[i], receivedMessages[i]);
                    mReceivedMessageDao.insert(receivedMessage);
                }
            }

            return null;
        }
    }
}

