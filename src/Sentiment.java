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
    String text = "The stunning virtual world of Wall.E shows Pixar at the top of their creative graphic powers. Going over the beautiful CGI in my head, I'm still in awe of how the designers portrayed Wall.E's integrated world and their attention to detail will serve as a benchmark for years. That said, I'm less impressed by the simple and very predictable plot. Pixar/Disney married state-of-the-art visuals with a worn plot line from an early childhood fairy tale. I'm guessing the target audience for this movie is somewhere around 6th graders. Great summer family fare but not a great movie if you want something more substantial in terms of plot and character development. ";


    public void Start() {
        Properties props = new Properties();
        props.put("sentiment.model","E:\\NLP\\stanford-corenlp-full-2015-04-20\\model-0009-79,68.ser.gz");
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