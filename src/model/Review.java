package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Maciej on 27.11.15.
 */
public class Review implements Serializable{

    private String tittle;
    private String date;
    private int vote;
    private String text;
    private ArrayList<Sentiment> sentiments;

    public Review(String tittle, String date, int vote, String text) {
        this.tittle = tittle;
        this.date = date;
        this.vote = vote;
        this.text = text;
//        this.sentiments = sentiments; //TODO
    }

    public int getVote() {
        return vote;
    }

    public String getTittle() {
        return tittle;
    }
}
