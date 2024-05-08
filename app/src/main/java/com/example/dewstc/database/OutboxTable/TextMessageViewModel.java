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

package com.example.dewstc.database.OutboxTable;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * The PairedDeviceViewModel provides the interface between the UI and the data layer of the dewstc,
 * represented by the Repository
 */

public class TextMessageViewModel extends AndroidViewModel {
    private TextMessageRepository mRepository;
    private LiveData<List<TextMessage>> mAllMessages;

    public TextMessageViewModel(Application application) {
        super(application);
        mRepository = new TextMessageRepository(application);
        mAllMessages = mRepository.getAllTextMessages();
    }

    public LiveData<List<TextMessage>> getAllTextMessages() {
        return mAllMessages;
    }

    public void insert(TextMessage textMessage) {
        mRepository.insert(textMessage);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteMessage(TextMessage textMessage) {
        mRepository.deleteMessage(textMessage);
    }

    public void update(TextMessage textMessage) {
        mRepository.update(textMessage);
    }

    public TextMessage getMyLatestMessage() {
        return mRepository.getMyLatestMessage();
    }

    public TextMessage getMyEarliestMessage() {
        return mRepository.getMyEarliestMessage();
    }

    public LiveData<List<TextMessage>> searchTimes(String timestamp) {
        return mRepository.searchTimes(timestamp);
    }

    public LiveData<TextMessage> getMessageByTimestamp(String timestamp) {
        return mRepository.getMessageByTimestamp(timestamp);
    }

    public boolean isTableEmpty() {
        return mRepository.isTableEmpty();
    }

    public TextMessage searchForTimestamp(String x) {
        return mRepository.searchForTimestamp(x);
    }
}