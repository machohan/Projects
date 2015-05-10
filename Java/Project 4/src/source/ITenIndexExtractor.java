package source;

public class ITenIndexExtractor {
  
  /**
   * This method gives number of i10-index after 2009.
   * @param googleScholarURL
   */
  public static String extractNumITenIndex(String googleScholarURL) {
      String regex = ".*<td class=\"cit-borderleft cit-data\">(.*?)</td>";
      return Extractor.extract(googleScholarURL, regex);
  }
}
