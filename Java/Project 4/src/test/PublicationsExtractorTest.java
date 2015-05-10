package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import source.PublicationsExtractor;

/**
 * @author Ammar
 *
 */
public class PublicationsExtractorTest {

  String inputFile;
  String output;
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample2.html";
    output = PublicationsExtractor.extractPublications(inputFile).toString();
  }

  /**
   * Test method for 
   * {@link source.PublicationsExtractor#extractPublications(java.lang.String)}.
   */
  @Test
  public void testExtractPublications() {
    assertFalse(output.contains("Error"));
  }

}
