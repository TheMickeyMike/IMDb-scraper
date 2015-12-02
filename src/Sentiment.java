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
//  very negative, negative, neutral, positive, and very positive.
public class Sentiment {
    String text = "Awesome movie! \n That was good movie. \n My name is Mike. \n Not bad movie. \n This is the worst movie ever!";



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

//"Awesome movie! \n That was good movie. \n My name is Mike. \n Not bad movie. \n This is the worst movie ever!";