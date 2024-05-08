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

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * The PairedDeviceViewModel provides the interface between the UI and the data layer of the DewSTC,
 * represented by the Repository
 */

public class ReceivedMessageViewModel extends AndroidViewModel {
    private ReceivedMessageRepository mRepositoryReceivedMessage;
    private LiveData<List<ReceivedMessage>> mAllReceivedMessages;

    public ReceivedMessageViewModel(Application application) {
        super(application);
        mRepositoryReceivedMessage = new ReceivedMessageRepository(application);
        mAllReceivedMessages = mRepositoryReceivedMessage.getAllReceivedMessages();
    }

    public void insert(ReceivedMessage receivedMessage) {
        mRepositoryReceivedMessage.insert(receivedMessage);
    }

    public void deleteAll() {
        mRepositoryReceivedMessage.deleteAll();
    }

    public void update(ReceivedMessage receivedMessage) {
        mRepositoryReceivedMessage.update(receivedMessage);
    }

    public LiveData<List<ReceivedMessage>> searchTimes(String timestamp) {
        return mRepositoryReceivedMessage.searchTimes(timestamp);
    }

    public ReceivedMessage searchForTimestamp(String x) {
        return mRepositoryReceivedMessage.searchForTimestamp(x);
    }

    public boolean isTableEmpty() {
        return mRepositoryReceivedMessage.isTableEmpty();
    }
}