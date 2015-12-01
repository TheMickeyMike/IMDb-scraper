package model;

import java.time.LocalDate;

/**
 * Created by Maciej on 27.11.15.
 */
public class Review {

    private String tittle;
    private String date;
    private int vote;
    private String text;

    public Review(String tittle, String date, int vote, String text) {
        this.tittle = tittle;
        this.date = date;
        this.vote = vote;
        this.text = text;
    }

    public int getVote() {
        return vote;
    }
}
