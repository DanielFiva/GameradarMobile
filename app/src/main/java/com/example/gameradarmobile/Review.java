package com.example.gameradarmobile;

public class Review {

    public int reviewId;
    public String username;
    public int gameId;
    public int rating;
    public String comment;
    public String date;
    public String type;

    public Review(int reviewId, String username, int gameId, int rating, String comment, String date, String type) {
        this.reviewId = reviewId;
        this.username = username;
        this.gameId = gameId;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.type = type;
    }
}
