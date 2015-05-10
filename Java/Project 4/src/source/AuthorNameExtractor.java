package source;

public class AuthorNameExtractor {

  /**
   * This method gives a name of the author.
   * @param googleScholarURL
   * @return name of the author
   */
  public static String extractAuthorsName(String googleScholarURL) {
      String regex = "<span id=\"cit-name-display\" "
          + "class=\"cit-in-place-nohover\">(.*?)</span>";
      return Extractor.extract(googleScholarURL, regex);
  }
  
}
