import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;


import java.util.List;
import java.util.Properties;

/**
 * Created by Maciej on 29.11.15.
 */
public class Sentiment {
    String text = "I'd like to keep my review rather to the point.\n" +
            "\n" +
            "Pros: 1. its theme - dream is a fascinating topic to say the least. There are a lot of unknowns in dreamworld. \n" +
            "\n" +
            "2. its plot - there are several sweet twists and unpredictable turns. \n" +
            "\n" +
            "3. its edgy drive - although you know what's coming next, still you feel jumpy about it when it does.\n" +
            "\n" +
            "4. its rapid storyline - the story moves fast from one scene to another, making the viewers feel like on a roller coaster ride. At times, it's hard to keep up, even after watching it several times. \n" +
            "\n" +
            "5. its sophistication - there is a lot of information to remember and digest. This is the very thing the modern moviegoers are after, I believe. \n" +
            "\n" +
            "6. its realism - okay, pun intended. The movie explains (or at least tries to) the ins and outs of what dream is about and how it functions, some of which are very familiar with and dear to us.\n" +
            "\n" +
            "Cons: 1. its poor character development - although the acting was convincing enough there was not enough of character development. I wonder how many people really felt connected to the main character(s) after watching the movie. Yes, the movie talks about emotional struggles but it was more of an action film than anything else, if you ask me. \n" +
            "\n" +
            "2. too many distractions - I found that the movie had more characters than necessary. They may play certain roles in the plot but they seemed more of distractions than anything else. I wish the movie was more focused. \n" +
            "\n" +
            "3. a bit preachy - I noticed that the characters would explain things about dreamworld and then the exact things happen later in the movie. I'm afraid, Inception overused this trick. \n" +
            "\n" +
            "In conclusion, its theme is fascinating but its delivery is not without room for improvement. \n" +
            "\n" +
            "I highly recommend you to go and read Somewhere carnal over 40 winks, if you dig this kind of flicks. \n" +
            "\n" +
            "Cheers!";




    public void Start() {
        Properties props = new Properties();
        props.put("sentiment.model","/Users/Maciej/Desktop/IMDb-scraper/lib/stanford-corenlp-full-2015-04-20/models/model-0009-79,68.ser.gz");
        props.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println(sentiment + "\t" + sentence);
        }
    }
}
