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

    public int getVeryNegative() {
        return veryNegative;
    }

    public void setVeryNegative(int veryNegative) {
        this.veryNegative = veryNegative;
    }

    public int getVeryPositive() {
        return veryPositive;
    }

    public void setVeryPositive(int veryPositive) {
        this.veryPositive = veryPositive;
    }

    public int getPositive() {
        return positive;
    }

    public void setPositive(int positive) {
        this.positive = positive;
    }

    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }

    public int getNegative() {
        return negative;
    }

    public void setNegative(int negative) {
        this.negative = negative;
    }
}
