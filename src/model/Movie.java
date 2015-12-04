package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Maciej on 28.11.15.
 */
public class Movie implements Serializable{
    private String Tittle;
    private ArrayList<Review> reviews;

    public Movie(String tittle, ArrayList<Review> reviews) {
        Tittle = tittle;
        this.reviews = reviews;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }
}
