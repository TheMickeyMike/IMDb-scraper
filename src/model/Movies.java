package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Maciej on 02.12.15.
 */
public class Movies implements Serializable{
    private ArrayList<Movie> movies;

    public Movies() {
        this.movies = new ArrayList<Movie>();
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }
}
