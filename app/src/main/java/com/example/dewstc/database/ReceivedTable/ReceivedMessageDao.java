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
public interface ReceivedMessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ReceivedMessage receivedMessage);

    @Query("DELETE FROM received_messages")
    void deleteAllReceivedMessages();

    @Delete
    void deleteReceivedMessage(ReceivedMessage receivedMessage);

    @Query("SELECT * from received_messages LIMIT 1")
    ReceivedMessage[] getReceivedMessage();

    @Query("SELECT * from received_messages ORDER BY id ASC")
    LiveData<List<ReceivedMessage>> getAllReceivedMessages();

    @Update
    void update(ReceivedMessage... receivedMessage);

    @Query("SELECT * FROM received_messages WHERE Timestamp LIKE :searchTimestamp")
    LiveData<List<ReceivedMessage>> searchTimes(String searchTimestamp);

    @Query("SELECT * FROM received_messages WHERE Timestamp = :thisTimestamp")
    LiveData<ReceivedMessage> getMessageByTimestamp(String thisTimestamp);

    @Query("SELECT * FROM received_messages WHERE Timestamp LIKE :searchTimestamp")
    ReceivedMessage searchForTimestamp(String searchTimestamp);

    @Query("SELECT COUNT(*) FROM received_messages")
    int getCount();
}