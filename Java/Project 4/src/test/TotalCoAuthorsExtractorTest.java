package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import source.TotalCoAuthorsExtractor;

/**
 * @author Ammar
 *
 */
public class TotalCoAuthorsExtractorTest {

  String inputFile;
  String totalCoAuthors;
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample1.html";
    totalCoAuthors = TotalCoAuthorsExtractor.extractTotalCoAuthors(inputFile);
    
  }

  /**
   * Test method for 
   * {@link source.TotalCoAuthorsExtractor
   *                #extractTotalCoAuthors(java.lang.String)}.
   */
  @Test
  public void testExtractTotalCoAuthors() {
    assertFalse(totalCoAuthors.contains("Error"));
    assertTrue(Integer.parseInt(totalCoAuthors) >=0);
  }

}
