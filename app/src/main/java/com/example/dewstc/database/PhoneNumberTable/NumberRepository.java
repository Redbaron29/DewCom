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

package com.example.dewstc.database.PhoneNumberTable;
import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;
import com.example.dewstc.database.RoomDatabase;
import java.util.List;

/**
 * This class holds the implementation code for the methods that interact with the database.
 * Using a repository allows us to group the implementation methods together,
 * and allows the NumberViewModel to be a clean interface between the rest of the dewstc
 * and the database.
 * <p>
 * For insert, update and delete, and longer-running queries,
 * you must run the database interaction methods in the background.
 * <p>
 * Typically, all you need to do to implement a database method
 * is to call it on the data access object (DAO), in the background if applicable.
 */

public class NumberRepository {
    private NumberDao mNumberDao;
    private LiveData<List<Number>> mAllNumbers;

    NumberRepository(Application application) {
        RoomDatabase db = RoomDatabase.getDatabase(application);
        mNumberDao = db.numberDao();
        mAllNumbers = mNumberDao.getAllNumbers();
    }

    LiveData<List<Number>> getAllNumbers() {
        return mAllNumbers;
    }

    public void insert(Number number) {
        new insertAsyncTask(mNumberDao).execute(number);
    }
    public void update(Number number)  {
        new updateNumberAsyncTask(mNumberDao).execute(number);
    }
    public void deleteAll()  {
        new deleteAllNumbersAsyncTask(mNumberDao).execute();
    }

    // Static inner classes below here to run database interactions in the background.

    /**
     * Inserts a number into the database.
     */
    private static class insertAsyncTask extends AsyncTask<Number, Void, Void> {

        private NumberDao mAsyncTaskNumberDao;

        insertAsyncTask(NumberDao numberDao) {
            mAsyncTaskNumberDao = numberDao;
        }

        @Override
        protected Void doInBackground(final Number... params) {
            mAsyncTaskNumberDao.insert(params[0]);
            return null;
        }
    }

    /**
     * Deletes all numbers from the database (does not delete the table).
     */
    private static class deleteAllNumbersAsyncTask extends AsyncTask<Void, Void, Void> {
        private NumberDao mAsyncTaskNumberDao;

        deleteAllNumbersAsyncTask(NumberDao numberDao) {
            mAsyncTaskNumberDao = numberDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskNumberDao.deleteAll();
            return null;
        }
    }

    /**
     *  Updates a number in the database.
     */
    private static class updateNumberAsyncTask extends AsyncTask<Number, Void, Void> {
        private NumberDao mAsyncTaskNumberDao;
        updateNumberAsyncTask(NumberDao numberDao) {
            mAsyncTaskNumberDao = numberDao;
        }

        @Override
        protected Void doInBackground(final Number... params) {
            mAsyncTaskNumberDao.update(params[0]);
            return null;
        }
    }
    Number getMyNumber(){
        return mNumberDao.getMyNumber();
    }
}
