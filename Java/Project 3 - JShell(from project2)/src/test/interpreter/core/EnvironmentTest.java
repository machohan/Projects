/**
 * 
 */
package interpreter.core;

import static org.junit.Assert.*;

import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mwb
 *
 */
public class EnvironmentTest {
  Environment env;
  Environment envOutputCaptured;
  Environment envErrorCaptured;

  final String testString = "The quick red fox jumped over the lazy brown dog";

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    // test harness starts with simplest Environment
    env = new Environment.Builder().build();
    envOutputCaptured = new Environment.Builder().withOutputCapture().build();
    envErrorCaptured = new Environment.Builder().withErrorCapture().build();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for {@link interpreter.core.Environment#isOutputCaptured()}.
   */
  @Test
  public void testIsOutputCapturedFalse() {
    assertFalse(env.isOutputCaptured());
  }

  /**
   * Test method for {@link interpreter.core.Environment#isOutputCaptured()}.
   */
  @Test
  public void testIsOutputCapturedTrue() {
    assertTrue(envOutputCaptured.isOutputCaptured());
  }

  /**
   * Test method for {@link interpreter.core.Environment#getCapturedOutput()}.
   */
  @Test
  public void testGetCapturedOutput() {
    envOutputCaptured.out.print(testString);
    assertEquals(testString, envOutputCaptured.getCapturedOutput());
  }

  /**
   * Test method for {@link interpreter.core.Environment#getCapturedOutput()}.
   */
  @Test
  public void testGetCapturedOutputEmpty() {
    assertEquals("", envOutputCaptured.getCapturedOutput());
  }

  /**
   * Test method for {@link interpreter.core.Environment#isErrorCaptured()}.
   */
  @Test
  public void testIsErrorCapturedFalse() {
    assertFalse(env.isErrorCaptured());
  }

  /**
   * Test method for {@link interpreter.core.Environment#isErrorCaptured()}.
   */
  @Test
  public void testIsErrorCapturedTrue() {
    assertTrue(envErrorCaptured.isErrorCaptured());
  }

  /**
   * Test method for {@link interpreter.core.Environment#getCapturedError()}.
   */
  @Test
  public void testGetCapturedErrorEmpty() {
    assertEquals("", envErrorCaptured.getCapturedError());
  }

  /**
   * Test method for {@link interpreter.core.Environment#getCapturedError()}.
   */
  @Test
  public void testGetCapturedError() {
    envErrorCaptured.err.print(testString);
    assertEquals(testString, envErrorCaptured.getCapturedError());
  }

  /**
   * ============= 
   * Some methods to test Environment.Builder and special features of Env 
   * 
   */

  @Test
  public void testBuilderWithErr() {
    PrintStream testPrinter = new PrintStream(System.err);
    Environment myEnv = new Environment.Builder().withErr(testPrinter).build();
    assertEquals(testPrinter, myEnv.err);
  }

  @Test
  public void testBuilderWithOut() {
    PrintStream testPrinter = new PrintStream(System.err);
    Environment myEnv = new Environment.Builder().withOut(testPrinter).build();
    assertEquals(testPrinter, myEnv.out);
  }

  @Test
  public void testBuilderWithCannedInput() {
    Environment myEnv =
        new Environment.Builder().withCannedInput(testString).build();
    Scanner scanner = new Scanner(myEnv.in);
    assertTrue(scanner.hasNext());
    String scannedInput = scanner.nextLine();
    assertEquals(testString,scannedInput);
    scanner.close();
  }


}
