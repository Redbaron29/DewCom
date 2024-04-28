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
import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.datastore.database.InboxTable.TextMessageForMe;

import java.util.List;

/**
 * Data Access Object (DAO) for a message.
 * Each method performs a database operation, such as inserting or deleting a message,
 * running a DB query, or deleting all messages.
 */

@androidx.room.Dao
public interface TextMessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TextMessage textMessage);
    @Query("DELETE FROM message_table")
    void deleteAllTextMessages();
    @Delete
    void deleteTextMessage(TextMessage textMessage);
    @Query("SELECT * from message_table LIMIT 1")
    TextMessage[] getTextMessage();
    @Query("SELECT * from message_table ORDER BY id ASC")
    LiveData<List<TextMessage>> getAllTextMessages();
    @Update
    void update(TextMessage... textMessage);
    @Query("SELECT * FROM message_table ORDER BY id ASC LIMIT 1")
    TextMessage getMyEarliestMessage();
    @Query("SELECT * FROM message_table ORDER BY id DESC LIMIT 1")
    TextMessage getMyLatestMessage();
    @Query("SELECT * FROM message_table WHERE Timestamp LIKE :searchTimestamp")
    LiveData<List<TextMessage>> searchTimes(String searchTimestamp);
    @Query("SELECT * FROM message_table WHERE Timestamp = :thisTimestamp")
    LiveData<TextMessage> getMessageByTimestamp(String thisTimestamp);
    @Query("SELECT * FROM message_table WHERE Timestamp LIKE :searchTimestamp")
    TextMessage searchForTimestamp(String searchTimestamp);
    @Query("SELECT COUNT(*) FROM message_table")
    int getCount();
}