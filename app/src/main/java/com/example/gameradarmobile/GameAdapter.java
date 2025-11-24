package com.example.gameradarmobile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private final ArrayList<JSONObject> games = new ArrayList<>();

    public void addGames(JSONArray newGames) {
        for (int i = 0; i < newGames.length(); i++) {

            JSONArray arr = newGames.optJSONArray(i);
            if (arr == null) {
                Log.e("GameAdapter", "Element at index " + i + " is NOT an array");
                continue;
            }

            // Convert array → object compatible with adapter
            try {
                JSONObject obj = new JSONObject();
                obj.put("NAME", arr.optString(0, "Unknown"));
                obj.put("DEV", arr.optString(1, "Unknown"));
                obj.put("HEADER_IMAGE", arr.optString(2, ""));
                obj.put("SCORE", arr.optInt(3, 0));

                games.add(obj);  // now it's a real JSONObject

            } catch (Exception e) {
                Log.e("GameAdapter", "Error converting array to object", e);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject game = games.get(position);

        if (game == null) {
            holder.txtName.setText("Unknown");
            holder.img.setImageResource(R.drawable.placeholder); // use placeholder
            return;
        }

        String name = game.optString("NAME", "Unknown");
        String img = game.optString("HEADER_IMAGE", "");

        holder.txtName.setText(name);

        // Load image with Glide, use placeholder if URL is empty
        Glide.with(holder.itemView.getContext())
                .load(img.isEmpty() ? R.drawable.placeholder : img)
                .placeholder(R.drawable.placeholder)  // while loading
                .error(R.drawable.placeholder)        // if failed
                .into(holder.img);
    }

    @Override
    public int getItemCount() { return games.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtName;
        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgGame);
            txtName = itemView.findViewById(R.id.txtGameName);
        }
    }
}
