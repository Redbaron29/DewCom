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

package com.example.dewstc.database.ReceivedTable;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.dewstc.database.RoomDatabase;

import java.util.List;

/**
 * This class holds the implementation code for the methods that interact with the database.
 * Using a repository allows us to group the implementation methods together,
 * and allows the PairedDeviceViewModel to be a clean interface between the rest of the DewSTC
 * and the database.
 * <p>
 * For insert, update and delete, and longer-running queries,
 * you must run the database interaction methods in the background.
 * <p>
 * Typically, all you need to do to implement a database method
 * is to call it on the data access object (DAO), in the background if applicable.
 */

public class ReceivedMessageRepository {
    private ReceivedMessageDao mReceivedMessageDao;
    private LiveData<List<ReceivedMessage>> mAllMessages;

    ReceivedMessageRepository(Application application) {
        RoomDatabase db = RoomDatabase.getDatabase(application);
        mReceivedMessageDao = db.receivedMessageDao();
        mAllMessages = mReceivedMessageDao.getAllReceivedMessages();
    }

    LiveData<List<ReceivedMessage>> getAllReceivedMessages() {
        return mAllMessages;
    }

    public void insert(ReceivedMessage receivedMessage) {
        new insertAsyncTask(mReceivedMessageDao).execute(receivedMessage);
    }

    public void update(ReceivedMessage receivedMessage) {
        new updateMessageAsyncTask(mReceivedMessageDao).execute(receivedMessage);
    }

    public void deleteAll() {
        new deleteAllMessagesAsyncTask(mReceivedMessageDao).execute();
    }
    // Must run off main thread

    public boolean isTableEmpty() {
        return mReceivedMessageDao.getCount() == 0;
    }

    // Static inner classes below here to run database interactions in the background.

    /**
     * Inserts a message into the database.
     */
    private static class insertAsyncTask extends AsyncTask<ReceivedMessage, Void, Void> {

        private ReceivedMessageDao mAsyncTaskReceivedMessageDao;

        insertAsyncTask(ReceivedMessageDao receivedMessageDao) {
            mAsyncTaskReceivedMessageDao = receivedMessageDao;
        }

        @Override
        protected Void doInBackground(final ReceivedMessage... params) {
            mAsyncTaskReceivedMessageDao.insert(params[0]);
            return null;
        }
    }

    /**
     * Deletes all messages from the database (does not delete the table).
     */
    private static class deleteAllMessagesAsyncTask extends AsyncTask<Void, Void, Void> {
        private ReceivedMessageDao mAsyncTaskReceivedMessageDao;

        deleteAllMessagesAsyncTask(ReceivedMessageDao receivedMessageDao) {
            mAsyncTaskReceivedMessageDao = receivedMessageDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskReceivedMessageDao.deleteAllReceivedMessages();
            return null;
        }
    }

    /**
     * Deletes a single message from the database.
     */
    private static class deleteMessageAsyncTask extends AsyncTask<ReceivedMessage, Void, Void> {
        private ReceivedMessageDao mAsyncTaskReceivedMessageDao;

        deleteMessageAsyncTask(ReceivedMessageDao receivedMessageDao) {
            mAsyncTaskReceivedMessageDao = receivedMessageDao;
        }

        @Override
        protected Void doInBackground(final ReceivedMessage... params) {
            mAsyncTaskReceivedMessageDao.deleteReceivedMessage(params[0]);
            return null;
        }
    }

    /**
     * Updates a message in the database.
     */
    private static class updateMessageAsyncTask extends AsyncTask<ReceivedMessage, Void, Void> {
        private ReceivedMessageDao mAsyncTaskReceivedMessageDao;

        updateMessageAsyncTask(ReceivedMessageDao receivedMessageDao) {
            mAsyncTaskReceivedMessageDao = receivedMessageDao;
        }

        @Override
        protected Void doInBackground(final ReceivedMessage... params) {
            mAsyncTaskReceivedMessageDao.update(params[0]);
            return null;
        }
    }

    public LiveData<List<ReceivedMessage>> searchTimes(String x) {
        return mReceivedMessageDao.searchTimes(x);
    }

    ReceivedMessage searchForTimestamp(String x) {
        return mReceivedMessageDao.searchForTimestamp(x);
    }
}