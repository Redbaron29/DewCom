package com.example.dewstc;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dewstc.database.InboxTable.TextMessageForMe;
import com.example.dewstc.database.InboxTable.TextMessageForMeListAdapter;
import com.example.dewstc.database.InboxTable.TextMessageForMeViewModel;

import java.util.List;

public class InboxActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextMessageForMeViewModel textMessageForMeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
        final TextMessageForMeListAdapter adapter = new TextMessageForMeListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the TextMessageForMeViewModel
        textMessageForMeViewModel = ViewModelProviders.of(this).get(TextMessageForMeViewModel.class);
        // Get all the messages from the database and associate them to the adapter.
        textMessageForMeViewModel.getAllTextMessagesForMe().observe(this, new Observer<List<TextMessageForMe>>() {
            @Override
            public void onChanged(@Nullable final List<TextMessageForMe> messages) {
                // Update the cached copy of the words in the adapter.
                adapter.setTextMessagesForMe(messages);
            }
        });

        // Add the functionality to swipe items in the RecyclerView to delete the swiped item.
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    // We are not implementing onMove() in this dewstc.
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    // When the use swipes a word,
                    // delete that word from the database.
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        TextMessageForMe textMessageForMe = adapter.getMessageForMeAtPosition(position);
                        textMessageForMeViewModel.deleteMessage(textMessageForMe);
                    }
                });
        // Attach the item touch helper to the recycler view.
        helper.attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new TextMessageForMeListAdapter.ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                TextMessageForMe textMessageForMe = adapter.getMessageForMeAtPosition(position);
                Toast.makeText(InboxActivity.this, "Position: " + position + "\nId: " + textMessageForMe.getId()
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // The options menu has a single item "Clear all data now"
    // that deletes all the entries in the database.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_data) {
            // Add a toast just for confirmation
            Toast.makeText(this, R.string.clear_data_toast_text, Toast.LENGTH_LONG).show();

            // Delete the existing data.
            textMessageForMeViewModel.deleteAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
