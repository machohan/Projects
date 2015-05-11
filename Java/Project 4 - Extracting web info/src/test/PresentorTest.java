package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ammar
 *
 */
public class PresentorTest {

  String[] args;
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    args = new String[]{"sample1.html","sample2.html"};
  }

  /**
   * Test method for {@link source.Presentor#getScholarInfo(java.lang.String[])}.
   */
  @Test
  public void testGetScholarInfo() {
    assertTrue(args.length>0);
  }

}
