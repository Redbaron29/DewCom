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
import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

/**
 * Data Access Object (DAO) for a number.
 * Each method performs a database operation, such as inserting or deleting a number,
 * running a DB query, or deleting all numbers.
 */

@androidx.room.Dao
public interface NumberDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Number number);

    @Query("DELETE FROM my_number")
    void deleteAll();

    @Query("SELECT * from my_number LIMIT 1")
    Number[] getAnyNumber();

    @Query("SELECT * from my_number ORDER BY myNumber ASC")
    LiveData<List<Number>> getAllNumbers();

    @Update
    void update(Number... number);

    @Query("SELECT * FROM my_number ORDER BY id LIMIT 1")
    Number getMyNumber();
}