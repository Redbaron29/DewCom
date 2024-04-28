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
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.dewstc.R;
import java.util.List;
/**
 * Adapter for the RecyclerView that displays a list of numbers.
 */

public class NumberListAdapter extends RecyclerView.Adapter<NumberListAdapter.RoomViewHolder> {

    private final LayoutInflater mInflater;
    private List<Number> mNumbers; // Cached copy of numbers
    private static ClickListener clickListener;
    public NumberListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new RoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        if (mNumbers != null) {
            Number current = mNumbers.get(position);
            holder.roomItemView.setText(current.getMyNumber());
        } else {
            // Covers the case of data not being ready yet.
            holder.roomItemView.setText("No numbers");
        }
    }

    /**
     * Associates a list of numbers with this adapter
     */
    public void setNumbers(List<Number> numbers) {
        mNumbers = numbers;
        notifyDataSetChanged();
    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * mNumbers has not been updated (means initially, it's null, and we can't return null).
     */
    @Override
    public int getItemCount() {
        if (mNumbers != null)
            return mNumbers.size();
        else return 0;
    }

    /**
     * Gets the number at a given position.
     * This method is useful for identifying which number
     * was clicked or swiped in methods that handle user events.
     *
     * @param position The position of the number in the RecyclerView
     * @return The number at the given position
     */
    public Number getNumberAtPosition(int position) {
        return mNumbers.get(position);
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomItemView;

        private RoomViewHolder(View itemView) {
            super(itemView);
            roomItemView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        NumberListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

}
