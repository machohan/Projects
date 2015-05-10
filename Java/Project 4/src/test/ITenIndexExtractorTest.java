package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import source.ITenIndexExtractor;

/**
 * @author Ammar
 *
 */
public class ITenIndexExtractorTest {

  String inputFile;
  String iTenIndex;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    inputFile =  "sample1.html";
    iTenIndex = ITenIndexExtractor.extractNumITenIndex(inputFile);    
  }

  /**
   * Test method for {@link source.ITenIndexExtractor#extractNumITenIndex(java.lang.String)}.
   */
  @Test
  public void testExtractNumITenIndex() {
    assertFalse(iTenIndex.contains("Error"));
    assertTrue(Integer.parseInt(iTenIndex) >= 0);
  }

}
