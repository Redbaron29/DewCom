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
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

/**
 * The NumberViewModel provides the interface between the UI and the data layer of the dewstc,
 * represented by the Repository
 */

public class NumberViewModel extends AndroidViewModel {
    private NumberRepository mRepository;
    private LiveData<List<Number>> mAllNumbers;

    public NumberViewModel(Application application) {
        super(application);
        mRepository = new NumberRepository(application);
        mAllNumbers = mRepository.getAllNumbers();
    }
    public LiveData<List<Number>> getAllNumbers() {
        return mAllNumbers;
    }
    public void insert(Number number) {
        mRepository.insert(number);
    }
    public void deleteAll() {
        mRepository.deleteAll();
    }
    public void update(Number number) {
        mRepository.update(number);
    }
    public Number getMyNumber(){
        return mRepository.getMyNumber();
    }
}