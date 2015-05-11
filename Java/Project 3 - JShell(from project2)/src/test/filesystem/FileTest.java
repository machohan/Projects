package filesystem;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testSetAndGetContent() {
    String testContent = "foobar";
    File f = new File();
    f.setContent(testContent);
    assertEquals(testContent, f.getContent());
  }

  @Test
  public void testSetAppendGetContent() {
    String testContentA = "rattletrap";
    String testContentB = "the quick brown fox";
    String expectedResult = testContentA + testContentB;
    File f = new File();
    f.setContent(testContentA);
    f.appendToContent(testContentB);
    assertEquals(expectedResult, f.getContent());
  }

  @Test
  public void testEmptyContent() {
    String emptyString = "";
    File f = new File();
    assertEquals(emptyString, f.getContent());
  }

  @Test
  public void testOverwriteContent() {
    String testContentA = "mickeymouse";
    String testContentB = "turkey";
    File f = new File();
    f.setContent(testContentA);
    f.setContent(testContentB);
    assertEquals(testContentB, f.getContent());
  }

  @Test
  public void testAppendToEmptyThenGet() {
    String testContent = "Frankly, my dear";
    File f = new File();
    f.appendToContent(testContent);
    assertEquals(testContent, f.getContent());
  }

}
