package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import source.Extractor;

/**
 * @author Ammar
 *
 */
public class ExtractorTest {

  String inputFile;
  String regex;
  String output;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample1.html";
    regex = "(.*)";
    output = Extractor.extract(inputFile, regex);
  }

  /**
   * Test method for 
   * {@link source.Extractor#extract(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testExtract() {
    assertFalse(output == null);
    assertFalse(output.contains("Error"));
    assertTrue(output.length() > 0);
  }

}
