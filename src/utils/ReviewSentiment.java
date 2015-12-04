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

/**
 * Created by Maciej on 04.12.15.
 */
public class ReviewSentiment {

    Sentiment sentimentCoef = null;
    int veryPositive = 0;
    int positive = 0;
    int neutral = 0;
    int negative = 0;
    int veryNegative = 0;

    Properties props;
    StanfordCoreNLP pipeline;
    Annotation annotation;

    public ReviewSentiment() {
        this.props = new Properties();
        props.put("sentiment.model","/Users/Maciej/Desktop/IMDb-scraper/lib/stanford-corenlp-full-2015-04-20/models/model-0009-79,68.ser.gz");
        props.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        this.pipeline = new StanfordCoreNLP(props);

    }

    public void getReviewSentiment(ArrayList<Movie> movieArrayList) {
        for (Movie movie : movieArrayList) {
            for (Review review : movie.getReviews()) {
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
        }
    }

    private void resetStats(){
         veryPositive = 0;
         positive = 0;
         neutral = 0;
         negative = 0;
         veryNegative = 0;
    }
}
