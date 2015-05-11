package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import source.CodeExtractor;

/**
 * @author Ammar
 *
 */
public class CodeExtractorTest {

  String inputFile;
  String htmlCode;
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample1.html";
    htmlCode = CodeExtractor.getHTML(inputFile);
  }

  /**
   * Test method for {@link source.CodeExtractor#getHTML(java.lang.String)}.
   */
  @Test
  public void testGetHTML() {
    assertTrue(htmlCode.length() > 10);
  }

}
