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

package com.example.dewstc;
import static com.example.dewstc.MainActivity.EXTRA_DATA_ID;
import static com.example.dewstc.MainActivity.EXTRA_DATA_UPDATE_NUMBER;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class displays a screen where the user enters a new number.
 * The NewNumberActivity returns the entered number to the calling activity
 * (MainActivity), which then stores the new number
 */
public class NewNumberActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.dewstc.database.REPLY";
    public static final String EXTRA_REPLY_ID = "com.example.dewstc.database.REPLY_ID";
    private EditText mEditNumberView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_number);

        mEditNumberView = findViewById(R.id.edit_number);
        int id = -1 ;

        final Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            String number = extras.getString(EXTRA_DATA_UPDATE_NUMBER, "");
            if (!number.isEmpty()) {
                mEditNumberView.setText(number);
                mEditNumberView.setSelection(number.length());
                mEditNumberView.requestFocus();
            }
        } // Otherwise, start with empty fields.


        final Button button = findViewById(R.id.button_save);

        // When the user presses the Save button, create a new Intent for the reply.
        // The reply Intent will be sent back to the calling activity (in this case, MainActivity).
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Create a new Intent for the reply.
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditNumberView.getText())) {
                    // No number was entered, set the result accordingly.
                    setResult(RESULT_CANCELED, replyIntent);
                }
                else if (mEditNumberView.getText().length() != 10) {
                    Toast.makeText(NewNumberActivity.this, "Phone Number must be 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    // Get the new number that the user entered.
                    String number = mEditNumberView.getText().toString();

                    // Put the new number in the extras for the reply Intent.
                    replyIntent.putExtra(EXTRA_REPLY, number);
                    if (extras != null && extras.containsKey(EXTRA_DATA_ID)) {
                        int id = extras.getInt(EXTRA_DATA_ID, -1);
                        if (id != -1) {
                            replyIntent.putExtra(EXTRA_REPLY_ID, id);
                        }
                    }
                    // Set the result status to indicate success.
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}
