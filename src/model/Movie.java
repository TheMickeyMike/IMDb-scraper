package model;

import java.util.ArrayList;

/**
 * Created by Maciej on 28.11.15.
 */
public class Movie {
    private String Tittle;
    private ArrayList<Review> reviews;

    public Movie(String tittle, ArrayList<Review> reviews) {
        Tittle = tittle;
        this.reviews = reviews;
    }
}
