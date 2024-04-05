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
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Entity class that represents a text message's text in the database
 */
@Entity(tableName = "message_table")
public class TextMessage {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    @ColumnInfo(name = "Timestamp")
    private String mTimestamp;
    @NonNull
    @ColumnInfo(name = "Message")
    private String mTextMessage;

    public TextMessage(@NonNull String mTimestamp, @NonNull String mTextMessage){
        this.mTimestamp = mTimestamp;
        this.mTextMessage = mTextMessage;
    }

    /*
     * This constructor is annotated using @Ignore, because Room expects only
     * one constructor by default in an entity class.
     */
    @Ignore
    public TextMessage(int id, @NonNull String mTimestamp, @NonNull String mTextMessage) {
        this.id = id;
        this.mTimestamp = mTimestamp;
        this.mTextMessage = mTextMessage;
    }
    public String getTextMessage() {return this.mTextMessage;}
    public String getTimestamp() {return this.mTimestamp;}
    public int getId() {return id;}
    public void setId(int id) {
        this.id = id;
    }
}