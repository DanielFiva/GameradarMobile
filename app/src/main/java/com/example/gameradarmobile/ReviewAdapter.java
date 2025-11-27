package com.example.gameradarmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VH> {

    ArrayList<Review> reviews;

    public ReviewAdapter(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Review r = reviews.get(pos);

        h.txtUser.setText("Usuario: " + r.username);
        h.txtScore.setText("Puntuación: " + r.rating);
        h.txtDate.setText(r.date);
        h.txtComment.setText(r.comment);

        // Set thumbs up/down based on review type
        if ("positiva".equalsIgnoreCase(r.type)) {
            h.imgType.setImageResource(R.drawable.thumb_up);
        } else if ("negativa".equalsIgnoreCase(r.type)) {
            h.imgType.setImageResource(R.drawable.thumb_down);
        } else {
            h.imgType.setImageDrawable(null); // no icon
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class VH extends RecyclerView.ViewHolder {

        TextView txtUser, txtScore, txtDate, txtComment;
        ImageView imgType;

        public VH(@NonNull View v) {
            super(v);
            txtUser = v.findViewById(R.id.txtReviewUser);
            txtScore = v.findViewById(R.id.txtReviewScore);
            txtDate = v.findViewById(R.id.txtReviewDate);
            txtComment = v.findViewById(R.id.txtReviewComment);
            imgType = v.findViewById(R.id.imgReviewType);
        }
    }
}
