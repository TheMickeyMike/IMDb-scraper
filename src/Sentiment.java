//import edu.stanford.nlp.ling.CoreAnnotation;
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
//import edu.stanford.nlp.util.CoreMap;
//
//
//import java.util.List;
//import java.util.Properties;
//
///**
// * Created by Maciej on 29.11.15.
// */
//public class Sentiment {
//    public void Start() {
////        String text = "I am feeling very sad and frustrated.";
//        String text = "";
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//        Annotation annotation = pipeline.process(text);
//        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
//        for (CoreMap sentence : sentences) {
//            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
//            System.out.println(sentiment + "\t" + sentence);
//        }
//    }
//}
