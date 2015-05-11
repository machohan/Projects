package test;

import static org.junit.Assert.*;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import driver.MyParser;
import source.TotalCoAuthorsExtractor;

public class CoAuthorsNamesExtractorTest {

  String inputFile;
  String totalCoAuthors;
  Set<String> coAuthorsNames;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample2.html";
    totalCoAuthors = TotalCoAuthorsExtractor.extractTotalCoAuthors(inputFile);
    coAuthorsNames = MyParser.getCoAuthorsNamesSet();
  }

  /**
   * Test method for 
   * {@link source.CoAuthorsNamesExtractor#extractCoAuthorsNames()}.
   */
  @Test
  public void testExtractCoAuthorsNames() {
    assertFalse(totalCoAuthors.contains("Error"));
    assertTrue(Integer.parseInt(totalCoAuthors) == coAuthorsNames.size());
  }

}
