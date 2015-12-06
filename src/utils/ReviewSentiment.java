package utils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import model.Movie;
import model.Review;
import model.Sentiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maciej on 04.12.15.
 */

//http://nlp.stanford.edu/software/corenlp-faq.shtml#memory
public class ReviewSentiment {

    Sentiment sentimentCoef = null;
    int veryPositive = 0;
    int positive = 0;
    int neutral = 0;
    int negative = 0;
    int veryNegative = 0;

    private Properties props;
    private StanfordCoreNLP pipeline;
    private Annotation annotation;
    private ExecutorService pool;

    private static final int THREADS_NUM = 1;

    public ReviewSentiment() {
        this.props = new Properties();
        props.put("sentiment.model", "E:\\NLP\\stanford-corenlp-full-2015-04-20\\model-0009-79,68.ser.gz");
        props.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        this.pipeline = new StanfordCoreNLP(props);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.pool = Executors.newFixedThreadPool(THREADS_NUM);

    }

    public void getReviewSentiment(ArrayList<Movie> movieArrayList) {
        for (Movie movie : movieArrayList) {
            for (Review review : movie.getReviews()) {
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        resetStats(); // Reset Stats
                        Annotation annotation = pipeline.process(review.getText());
                        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
                        for (CoreMap sentence : sentences) {
                            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                            switch (sentiment) {
                                case "Very positive":
                                    veryPositive++;
                                    break;
                                case "Positive":
                                    positive++;
                                    break;
                                case "Neutral":
                                    neutral++;
                                    break;
                                case "Negative":
                                    negative++;
                                    break;
                                case "Very negative":
                                    veryNegative++;
                                    break;
                                default:
                                    break;
                            }
                        }
                        review.setSentiments(new Sentiment(veryPositive, positive, neutral, negative, veryNegative));
                        System.out.println(veryPositive + " " + positive + " " + neutral + " " + negative + " " + veryNegative);
                    }
                });
            }
        }
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Sentiment getReviewSentiment(String userReview) {
        Annotation annotation = pipeline.process(userReview);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            switch (sentiment) {
                case "Very positive":
                    veryPositive++;
                    break;
                case "Positive":
                    positive++;
                    break;
                case "Neutral":
                    neutral++;
                    break;
                case "Negative":
                    negative++;
                    break;
                case "Very negative":
                    veryNegative++;
                    break;
                default:
                    break;
            }
        }
        System.out.println("\n##### User review stats ##### " +
                "\nVery negative: " +veryNegative +
                "\nNegative: " + negative +
                "\nNeutral: " + neutral +
                "\nPositive: " + positive +
                "\nVery positive: " + veryPositive);
        return new Sentiment(veryPositive, positive, neutral, negative, veryNegative);
    }

private void resetStats(){
        veryPositive=0;
        positive=0;
        neutral=0;
        negative=0;
        veryNegative=0;
        }
        }
