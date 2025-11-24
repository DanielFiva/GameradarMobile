package com.example.gameradarmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Simple empty adapter that shows a placeholder message when empty.
// You will replace this with real data later.
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    // For now no data source. Add a List<Game> later.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // No data: show placeholder text if you want, but since getItemCount is 0 this won't be called.
        holder.text.setText("Item " + position);
    }

    @Override
    public int getItemCount() {
        // Return 0 now (empty, infinite area placeholder).
        // When you add data, return the list size here.
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
        }
    }
}
