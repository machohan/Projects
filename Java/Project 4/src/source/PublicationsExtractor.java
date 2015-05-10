package source;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PublicationsExtractor {

  /**
   * This mehtod prints out the title of first three publications by author.
   * This method catches a exception if and when html file cannot be read.
   * @param googleScholarURL
   */
  public static StringBuilder extractPublications(String googleScholarURL) {
      String rawHTMLString;
      String regex;
      Pattern patternObject;
      Matcher matcherObject;
      StringBuilder outputSB = new StringBuilder();;
      int i=0;
      
      try {
        regex = "class=\"cit-dark-large-link\">(.*?)</a>";
        rawHTMLString = CodeExtractor.getHTML(googleScholarURL);
        patternObject = Pattern.compile(regex);
        matcherObject = patternObject.matcher(rawHTMLString);
        while (matcherObject.find() && i<3) {
          outputSB.append("\t" + (i+1) + "- " + matcherObject.group(1) + "\n");
          i++;
        }

      } catch (Exception e) {
        outputSB.append("Error: Publications cannot be reported correctly"
            + " at this time");
      }
      return outputSB;
      
   }
}
