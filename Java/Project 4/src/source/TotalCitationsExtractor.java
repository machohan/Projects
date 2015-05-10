package source;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TotalCitationsExtractor {

  /**
   * This method extracts number of citation for each of first five 
   * publications and calculate their total and outputs it.
   * This method will catch an exception if html file cannot be read.
   * @param googleScholarURL
   */
  public static String extractTotalCitations(String googleScholarURL) {
      String rawHTMLString;
      String regex;
      Pattern patternObject;
      Matcher matcherObject;
      int i=0;
      int totalCitations=0;
      
      try {
        regex = "<a class=\"cit-dark-link\" href=\".*?cites=.*?\">(.*?)</a>";
        rawHTMLString = CodeExtractor.getHTML(googleScholarURL);
        patternObject = Pattern.compile(regex);
        matcherObject = patternObject.matcher(rawHTMLString);
        while (matcherObject.find() && i<5) {
          totalCitations += Integer.parseInt(matcherObject.group(1));
          i++;
        }

      } catch (Exception e) {
        return "Error: Total citation cannot be calulated properly"
            + " at this time.";
      }
      return Integer.toString(totalCitations);
   }
}
