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
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity class that represents a number in the database
 */

@Entity(tableName = "my_number")
public class Number {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    @ColumnInfo(name = "myNumber")
    private String mMyNumber;
    public Number(@NonNull String myNumber) {
        this.mMyNumber = myNumber;
    }
    /*
    * This constructor is annotated using @Ignore, because Room expects only
    * one constructor by default in an entity class.
    */
    @Ignore
    public Number(int id, @NonNull String myNumber) {
        this.id = id;
        this.mMyNumber = myNumber;
    }
    public String getMyNumber() {
        return this.mMyNumber;
    }
    public int getId() {return id;}
    public void setId(int id) {
        this.id = id;
    }
}
