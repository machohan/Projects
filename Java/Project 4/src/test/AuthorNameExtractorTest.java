package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import source.AuthorNameExtractor;

public class AuthorNameExtractorTest {

  String inputFile;
  String authorName;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile = "sample1.html";
    authorName = AuthorNameExtractor.extractAuthorsName(inputFile);
  }
  
  /**
   * Test method for 
   * {@link source.AuthorNameExtractor#extractAuthorsName(java.lang.String)}.
   */
  @Test
  public void testExtractAuthorsName() {
    assertFalse(authorName == null);
    assertFalse(authorName.contains("Error"));
    assertTrue(authorName.length() > 2);
  }

}