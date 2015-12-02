package model;

/**
 * Created by Maciej on 02.12.15.
 */
public class Sentiment {
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
