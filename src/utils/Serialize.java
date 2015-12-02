package utils;

import model.Movie;
import model.Movies;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Maciej on 02.12.15.
 */
public class Serialize {
    private static final String PATH = "/Users/Maciej/Desktop/IMDb-scraper/data/";
    private String NAME;
    private Movies movies;
    private ArrayList<Movie> movieArrayList;

    public Serialize(String location) {
        this.movieArrayList = new ArrayList<Movie>();
        ReadMovies(location);
    }

    public Serialize(Movies movies) {
        this.movies = movies;
        SaveToFile();
    }


    private String GetTimeStamp() {
        long unixTime = System.currentTimeMillis() / 1000L;
        String s = String.valueOf(unixTime);
        return s;
    }
    private void SaveToFile() {
        this.NAME = GetTimeStamp() + ".ser";
        try {
            FileOutputStream stream = new FileOutputStream(PATH + "1.ser");
            ObjectOutputStream so = new ObjectOutputStream(stream);
            so.writeObject(movies);
            so.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ReadMovies(String location) {
        try {
            FileInputStream inputStream = new FileInputStream(location);
            ObjectInputStream os = new ObjectInputStream(inputStream);
            Object obj = os.readObject();
            movies = (Movies) obj;
            movieArrayList = movies.getMovies();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Movie> getMovieArrayList() {
        return movieArrayList;
    }
}
