# IMDb-scraper
Simple review and rating scraper for IMDb

### Libraries

This program uses a number of open source projects to work properly:

* [Stanford NLP](https://github.com/stanfordnlp/CoreNLP) - The Stanford Natural Language Processing
* [Gson](https://github.com/google/gson) - A Java serialization library that can convert Java Objects into JSON and back
* [Apache Commons Math](https://github.com/apache/commons-math) - Library of lightweight, self-contained mathematics and statistics component

### Launching

* Running from the command line, you **need** to supply a flag: `-Xmx2g`
* Running from IDE, follow [these instructions](http://stackoverflow.com/questions/4175188/setting-memory-of-java-programs-that-runs-from-eclipse) to increase the memory given to a program being run from inside


### Processing power
**This operation can cause** [***runs out of memor***](http://nlp.stanford.edu/software/corenlp-faq.shtml#memory)
* You can change processing power by **incraseing** threads number in:
  * ```class ReviewSentiment()```
  * ``` class MovieDataDownloader()```
  

### Solutions
* [Stanford CoreNLP FAQ](http://nlp.stanford.edu/software/corenlp-faq.shtml#memory)

### Common Problems
* **Runs out of memory?**. [Solution](http://nlp.stanford.edu/software/corenlp-faq.shtml#memory)



