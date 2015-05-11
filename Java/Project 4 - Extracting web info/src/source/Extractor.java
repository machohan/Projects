package source;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {
   
  /**
   * An extract method used everytime to extract any information from given
   * local html. This method is used when a single information needs to be
   * extracted from the webpage i.e author's name or i10-index etc. It gives 
   * back only first find using regex.
   * @param googleScholarURL
   * @param regexInput
   */
  public static String extract(String googleScholarURL, String regexInput) {
    String rawHTMLString;
    String regex;
    Pattern patternObject;
    Matcher matcherObject;
    
    try {
      regex = regexInput;
      rawHTMLString = CodeExtractor.getHTML(googleScholarURL);
      patternObject = Pattern.compile(regex);
      matcherObject = patternObject.matcher(rawHTMLString);
      while (matcherObject.find()) {
        return matcherObject.group(1);
      }

    } catch (Exception e) {
      return ("Error: information cannot be extracted. Check "
          + "Extractor");
    }
    return null;
  }
  
}
