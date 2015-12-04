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
    public static final String DATA_PATH = "data/";
    private String NAME;
    private Movies movies;
    private ArrayList<Movie> movieArrayList;

    public Serialize(String name) {
        this.movieArrayList = new ArrayList<Movie>();
        ReadMovies(name);
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
            FileOutputStream stream = new FileOutputStream(MovieDataDownloader.PATH_BASE + DATA_PATH + "1.ser");
            ObjectOutputStream so = new ObjectOutputStream(stream);
            so.writeObject(movies);
            so.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Object Serialized and saved");
    }

    private void ReadMovies(String name) {
        try {
            FileInputStream inputStream = new FileInputStream(MovieDataDownloader.PATH_BASE + DATA_PATH + name + ".ser");
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
