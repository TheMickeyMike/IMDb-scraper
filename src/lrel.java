import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Maciej on 01.12.15.
 */
public class lrel {
    public void startBitch() {
        String URL = "http://www.filmweb.pl/Skazani.Na.Shawshank";
        Document doc = null;
         try {
             doc = Jsoup.connect(URL).userAgent("Mozilla").get();
             Elements total = doc.select("ul.inline.sep-comma:has(li)");
             Elements lel = total.select("a[rel]");
             System.out.println(lel);

         } catch (IOException e) {
             e.printStackTrace();
         }

    }
}
