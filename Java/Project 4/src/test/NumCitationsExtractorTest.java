package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import source.NumCitationsExtractor;

/**
 * @author Ammar
 *
 */
public class NumCitationsExtractorTest {

  String inputFile;
  String numCitations;
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample2.html";
    numCitations = NumCitationsExtractor.extractNumCitations(inputFile);
  }

  /**
   * Test method for 
   * {@link source.NumCitationsExtractor#extractNumCitations(java.lang.String)}.
   */
  @Test
  public void testExtractNumCitations() {
    assertFalse(numCitations.contains("Error"));
    assertTrue(Integer.parseInt(numCitations) >= 0);
  }

}
