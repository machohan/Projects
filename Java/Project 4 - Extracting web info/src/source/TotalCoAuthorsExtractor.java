package source;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import driver.MyParser;

public class TotalCoAuthorsExtractor {
  
  /**
   * This method outputs total number of co-authors in a given html file.
   * This method also adds all co-authors names mentioned in a given html file
   * in a set.
   * This method throws an exception if html file cannot be read.
   * @param googleScholarURL
   * @return
   */
  public static String extractTotalCoAuthors(String googleScholarURL) {
      String rawHTMLString;
      String regex;
      Pattern patternObject;
      Matcher matcherObject;
      int totalCoAuthors=0;
      
      try {
        regex = "<a class=\"cit-dark-link\" "
            + "href=\".*?hl=en\" title=\".*?\">(.*?)</a>";
        rawHTMLString = CodeExtractor.getHTML(googleScholarURL);
        patternObject = Pattern.compile(regex);
        matcherObject = patternObject.matcher(rawHTMLString);
        while (matcherObject.find()) {
          MyParser.getCoAuthorsNamesSet().add(matcherObject.group(1));
          totalCoAuthors++;
        }

      } catch (Exception e) {
        return "Error: Total co-authors cannot be calculated"
            + " at this time.";
      }
      return Integer.toString(totalCoAuthors);
   }
  
}
