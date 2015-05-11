package source;

public class NumCitationsExtractor{
  
  /**
   * This method gives number of all citations by calling a method of an
   * Extractor class on an instance of an Extractor.
   * @param googleScholarURL
   */
  public static String extractNumCitations(String googleScholarURL) {
      String regex = "<td class=\"cit-borderleft cit-data\">(.*?)</td>";
      return Extractor.extract(googleScholarURL, regex);
  }

}
