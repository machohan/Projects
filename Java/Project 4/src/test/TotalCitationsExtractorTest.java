package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import source.TotalCitationsExtractor;

/**
 * @author Ammar
 *
 */
public class TotalCitationsExtractorTest {
  
  String inputFile;
  String totalCitations;
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample1.html";
    totalCitations = TotalCitationsExtractor.extractTotalCitations(inputFile);
  }

  /**
   * Test method for 
   * {@link source.TotalCitations
   *                Extractor#extractTotalCitations(java.lang.String)}.
   */
  @Test
  public void testExtractTotalCitations() {
    assertFalse(totalCitations.contains("Error"));
    assertTrue(Integer.parseInt(totalCitations) >=0);
  }

}
