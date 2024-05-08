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

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * The PairedDeviceViewModel provides the interface between the UI and the data layer of the dewstc,
 * represented by the Repository
 */

public class TextMessageForMeViewModel extends AndroidViewModel {
    private TextMessageForMeRepository mRepositoryForMe;
    private LiveData<List<TextMessageForMe>> mAllMessagesForMe;

    public TextMessageForMeViewModel(Application application) {
        super(application);
        mRepositoryForMe = new TextMessageForMeRepository(application);
        mAllMessagesForMe = mRepositoryForMe.getAllTextMessagesForMe();
    }

    public LiveData<List<TextMessageForMe>> getAllTextMessagesForMe() {
        return mAllMessagesForMe;
    }

    public void insert(TextMessageForMe textMessageForMe) {
        mRepositoryForMe.insert(textMessageForMe);
    }

    public void deleteAll() {
        mRepositoryForMe.deleteAll();
    }

    public void deleteMessage(TextMessageForMe textMessageForMe) {
        mRepositoryForMe.deleteMessage(textMessageForMe);
    }

    public void update(TextMessageForMe textMessageForMe) {
        mRepositoryForMe.update(textMessageForMe);
    }

    public TextMessageForMe getMyLatestMessageForMe() {
        return mRepositoryForMe.getMyLatestMessageForMe();
    }

    public TextMessageForMe getMyEarliestMessageForMe() {
        return mRepositoryForMe.getMyEarliestMessageForMe();
    }

    public LiveData<List<TextMessageForMe>> searchTimes(String timestamp) {
        return mRepositoryForMe.searchTimes(timestamp);
    }

    public LiveData<TextMessageForMe> getMessageByTimestamp(String timestamp) {
        return mRepositoryForMe.getMessageByTimestamp(timestamp);
    }

    public TextMessageForMe searchForTimestamp(String x) {
        return mRepositoryForMe.searchForTimestamp(x);
    }
}