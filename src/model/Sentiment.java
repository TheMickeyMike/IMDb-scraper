package model;

import java.io.Serializable;

/**
 * Created by Maciej on 02.12.15.
 */
public class Sentiment implements Serializable{
    private int veryPositive = 0;
    private int positive = 0;
    private int neutral = 0;
    private int negative = 0;
    private int veryNegative = 0;

    public Sentiment(int veryGood, int good, int neutral, int bad, int veryBad) {
        this.veryPositive = veryGood;
        this.positive = good;
        this.neutral = neutral;
        this.negative = bad;
        this.veryNegative = veryBad;
    }
}
