package test;

import static org.junit.Assert.*;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import driver.MyParser;
import source.TotalCoAuthorsExtractor;

/**
 * @author Ammar
 *
 */
public class MyParserTest {

  String inputFile;
  Set<String> namesSet;
  String totalCoAuthors;
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample1.html";
    totalCoAuthors = TotalCoAuthorsExtractor.extractTotalCoAuthors(inputFile);
    namesSet = MyParser.getCoAuthorsNamesSet();
  }

  /**
   * Test method for {@link driver.MyParser#getCoAuthorsNamesSet()}.
   */
  @Test
  public void testGetCoAuthorsNamesSet() {
    assertTrue(namesSet.size() >= Integer.parseInt(totalCoAuthors));
  }

}
