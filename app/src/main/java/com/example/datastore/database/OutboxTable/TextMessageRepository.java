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

package com.example.datastore.database.OutboxTable;
import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.datastore.database.InboxTable.TextMessageForMe;
import com.example.datastore.database.RoomDatabase;

import java.util.List;

/**
 * This class holds the implementation code for the methods that interact with the database.
 * Using a repository allows us to group the implementation methods together,
 * and allows the PairedDeviceViewModel to be a clean interface between the rest of the datastore
 * and the database.
 * <p>
 * For insert, update and delete, and longer-running queries,
 * you must run the database interaction methods in the background.
 * <p>
 * Typically, all you need to do to implement a database method
 * is to call it on the data access object (DAO), in the background if applicable.
 */

public class TextMessageRepository {
    private TextMessageDao mTextMessageDao;
    private LiveData<List<TextMessage>> mAllMessages;


    TextMessageRepository(Application application) {
        RoomDatabase db = RoomDatabase.getDatabase(application);
        mTextMessageDao = db.messageDao();
        mAllMessages = mTextMessageDao.getAllTextMessages();
    }

    LiveData<List<TextMessage>> getAllTextMessages() {
        return mAllMessages;
    }

    public void insert(TextMessage textMessage) {
        new insertAsyncTask(mTextMessageDao).execute(textMessage);
    }

    public void update(TextMessage textMessage)  {
        new updateMessageAsyncTask(mTextMessageDao).execute(textMessage);
    }
    public void deleteAll()  {
        new deleteAllMessagesAsyncTask(mTextMessageDao).execute();
    }
    // Must run off main thread
    public void deleteMessage(TextMessage textMessage) {
        new deleteMessageAsyncTask(mTextMessageDao).execute(textMessage);
    }

    // Static inner classes below here to run database interactions in the background.

    /**
     * Inserts a message into the database.
     */
    private static class insertAsyncTask extends AsyncTask<TextMessage, Void, Void> {

        private TextMessageDao mAsyncTaskTextMessageDao;

        insertAsyncTask(TextMessageDao textMessageDao) {
            mAsyncTaskTextMessageDao = textMessageDao;
        }

        @Override
        protected Void doInBackground(final TextMessage... params) {
            mAsyncTaskTextMessageDao.insert(params[0]);
            return null;
        }
    }

    /**
     * Deletes all messages from the database (does not delete the table).
     */
    private static class deleteAllMessagesAsyncTask extends AsyncTask<Void, Void, Void> {
        private TextMessageDao mAsyncTaskTextMessageDao;

        deleteAllMessagesAsyncTask(TextMessageDao textMessageDao) {
            mAsyncTaskTextMessageDao = textMessageDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskTextMessageDao.deleteAllTextMessages();
            return null;
        }
    }

    /**
     *  Deletes a single message from the database.
     */
    private static class deleteMessageAsyncTask extends AsyncTask<TextMessage, Void, Void> {
        private TextMessageDao mAsyncTaskTextMessageDao;

        deleteMessageAsyncTask(TextMessageDao textMessageDao) {
            mAsyncTaskTextMessageDao = textMessageDao;
        }

        @Override
        protected Void doInBackground(final TextMessage... params) {
            mAsyncTaskTextMessageDao.deleteTextMessage(params[0]);
            return null;
        }
    }

    /**
     *  Updates a message in the database.
     */
    private static class updateMessageAsyncTask extends AsyncTask<TextMessage, Void, Void> {
        private TextMessageDao mAsyncTaskTextMessageDao;
        updateMessageAsyncTask(TextMessageDao textMessageDao) {
            mAsyncTaskTextMessageDao = textMessageDao;
        }

        @Override
        protected Void doInBackground(final TextMessage... params) {
            mAsyncTaskTextMessageDao.update(params[0]);
            return null;
        }
    }
    TextMessage getMyEarliestMessage(){
        return mTextMessageDao.getMyEarliestMessage();
    }
    TextMessage getMyLatestMessage(){
        return mTextMessageDao.getMyLatestMessage();
    }
    public LiveData<List<TextMessage>> searchTimes(String x){
        return mTextMessageDao.searchTimes(x);
    }
    public LiveData<TextMessage> getMessageByTimestamp(String x){
        return mTextMessageDao.getMessageByTimestamp(x);
    }
    public boolean isTableEmpty() {
        return mTextMessageDao.getCount() == 0;
    }

    TextMessage searchForTimestamp(String x){
        return mTextMessageDao.searchForTimestamp(x);
    }
}