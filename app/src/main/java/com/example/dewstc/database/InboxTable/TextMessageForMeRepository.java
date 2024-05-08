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

package com.example.dewstc.database.InboxTable;

import android.app.Application;

import androidx.lifecycle.LiveData;

import android.os.AsyncTask;

import com.example.dewstc.database.RoomDatabase;

import java.util.List;

/**
 * This class holds the implementation code for the methods that interact with the database.
 * Using a repository allows us to group the implementation methods together,
 * and allows the PairedDeviceViewModel to be a clean interface between the rest of the dewstc
 * and the database.
 * <p>
 * For insert, update and delete, and longer-running queries,
 * you must run the database interaction methods in the background.
 * <p>
 * Typically, all you need to do to implement a database method
 * is to call it on the data access object (DAO), in the background if applicable.
 */

public class TextMessageForMeRepository {
    private TextMessageForMeDao mTextMessageForMeDao;
    private LiveData<List<TextMessageForMe>> mAllMessages;

    TextMessageForMeRepository(Application application) {
        RoomDatabase db = RoomDatabase.getDatabase(application);
        mTextMessageForMeDao = db.messageForMeDao();
        mAllMessages = mTextMessageForMeDao.getAllTextMessagesForMe();
    }

    LiveData<List<TextMessageForMe>> getAllTextMessagesForMe() {
        return mAllMessages;
    }

    public void insert(TextMessageForMe textMessageForMe) {
        new insertAsyncTask(mTextMessageForMeDao).execute(textMessageForMe);
    }

    public void update(TextMessageForMe textMessageForMe) {
        new updateMessageAsyncTask(mTextMessageForMeDao).execute(textMessageForMe);
    }

    public void deleteAll() {
        new deleteAllMessagesAsyncTask(mTextMessageForMeDao).execute();
    }

    // Must run off main thread
    public void deleteMessage(TextMessageForMe textMessageForMe) {
        new deleteMessageAsyncTask(mTextMessageForMeDao).execute(textMessageForMe);
    }

    // Static inner classes below here to run database interactions in the background.

    /**
     * Inserts a message into the database.
     */
    private static class insertAsyncTask extends AsyncTask<TextMessageForMe, Void, Void> {

        private TextMessageForMeDao mAsyncTaskTextMessageDao;

        insertAsyncTask(TextMessageForMeDao textMessageForMeDao) {
            mAsyncTaskTextMessageDao = textMessageForMeDao;
        }

        @Override
        protected Void doInBackground(final TextMessageForMe... params) {
            mAsyncTaskTextMessageDao.insert(params[0]);
            return null;
        }
    }

    /**
     * Deletes all messages from the database (does not delete the table).
     */
    private static class deleteAllMessagesAsyncTask extends AsyncTask<Void, Void, Void> {
        private TextMessageForMeDao mAsyncTaskTextMessageDao;

        deleteAllMessagesAsyncTask(TextMessageForMeDao textMessageForMeDao) {
            mAsyncTaskTextMessageDao = textMessageForMeDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskTextMessageDao.deleteAllTextMessagesForMe();
            return null;
        }
    }

    /**
     * Deletes a single message from the database.
     */
    private static class deleteMessageAsyncTask extends AsyncTask<TextMessageForMe, Void, Void> {
        private TextMessageForMeDao mAsyncTaskTextMessageDao;

        deleteMessageAsyncTask(TextMessageForMeDao textMessageForMeDao) {
            mAsyncTaskTextMessageDao = textMessageForMeDao;
        }

        @Override
        protected Void doInBackground(final TextMessageForMe... params) {
            mAsyncTaskTextMessageDao.deleteTextMessageForMe(params[0]);
            return null;
        }
    }

    /**
     * Updates a message in the database.
     */
    private static class updateMessageAsyncTask extends AsyncTask<TextMessageForMe, Void, Void> {
        private TextMessageForMeDao mAsyncTaskTextMessageDao;

        updateMessageAsyncTask(TextMessageForMeDao textMessageForMeDao) {
            mAsyncTaskTextMessageDao = textMessageForMeDao;
        }

        @Override
        protected Void doInBackground(final TextMessageForMe... params) {
            mAsyncTaskTextMessageDao.update(params[0]);
            return null;
        }
    }

    TextMessageForMe getMyEarliestMessageForMe() {
        return mTextMessageForMeDao.getMyEarliestMessageForMe();
    }

    TextMessageForMe getMyLatestMessageForMe() {
        return mTextMessageForMeDao.getMyLatestMessageForMe();
    }

    public LiveData<List<TextMessageForMe>> searchTimes(String x) {
        return mTextMessageForMeDao.searchTimes(x);
    }

    public LiveData<TextMessageForMe> getMessageByTimestamp(String x) {
        return mTextMessageForMeDao.getMessageByTimestamp(x);
    }

    TextMessageForMe searchForTimestamp(String x) {
        return mTextMessageForMeDao.searchForTimestamp(x);
    }
}