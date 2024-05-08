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

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) for a message.
 * Each method performs a database operation, such as inserting or deleting a message,
 * running a DB query, or deleting all messages.
 */

@androidx.room.Dao
public interface TextMessageForMeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TextMessageForMe textMessageForMe);

    @Query("DELETE FROM messages_for_me")
    void deleteAllTextMessagesForMe();

    @Delete
    void deleteTextMessageForMe(TextMessageForMe textMessageForMe);

    @Query("SELECT * from messages_for_me LIMIT 1")
    TextMessageForMe[] getTextMessageForMe();

    @Query("SELECT * from messages_for_me ORDER BY id ASC")
    LiveData<List<TextMessageForMe>> getAllTextMessagesForMe();

    @Update
    void update(TextMessageForMe... textMessageForMe);

    @Query("SELECT * FROM messages_for_me ORDER BY id ASC LIMIT 1")
    TextMessageForMe getMyEarliestMessageForMe();

    @Query("SELECT * FROM messages_for_me ORDER BY id DESC LIMIT 1")
    TextMessageForMe getMyLatestMessageForMe();

    @Query("SELECT * FROM messages_for_me WHERE Timestamp LIKE :searchTimestamp")
    LiveData<List<TextMessageForMe>> searchTimes(String searchTimestamp);

    @Query("SELECT * FROM messages_for_me WHERE Timestamp = :thisTimestamp")
    LiveData<TextMessageForMe> getMessageByTimestamp(String thisTimestamp);

    @Query("SELECT * FROM messages_for_me WHERE Timestamp LIKE :searchTimestamp")
    TextMessageForMe searchForTimestamp(String searchTimestamp);
}