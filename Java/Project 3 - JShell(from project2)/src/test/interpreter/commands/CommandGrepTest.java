/**
 * 
 */
package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import driver.JShell;
import filesystem.File;
import filesystem.FileSystem;
import filesystem.Path;

/**
 * @author mwb
 *
 */
public class CommandGrepTest {
  public Command grepCommand;
  public FileSystem fs;
  public JShell shell;
  public Environment env;

  public final static String poetryFileName = "poetry.txt";
  /**
   * poetry corpus @author mwb
   */
  public final static 
      String poetryCorpus = "For now, upon the grassy high land's heather,\n"
      + "The wild-eyed goats crop short the tawny grasses\n"
      + "And brindled cats cavort between their hooves\n"
      + "And pounce upon the mice, asleep 'til now;\n"
      + "Their burrows' entrances have been laid bare\n"
      + "By happy, sly, capricious, playful goats.\n";
  public final static String simpleGoatRegex = "goats";
  public final static String simpleNotInGoatRegex = "blaskerdimple";
  public final static int poetryRegexLineCount = 2;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    grepCommand = new CommandGrep();
    shell = new JShell(System.in, System.out);
    fs = shell.getFileSystem();
    env = new Environment.Builder()
    .withErrorCapture()
    .withOutputCapture()
    .build();
    Path p = new Path(fs, poetryFileName);
    File poetryFile = p.getOrCreateFile();
    poetryFile.setContent(poetryCorpus);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  @Test
  public void testGetName() {
    assertEquals("getName() must return 'grep'", "grep", grepCommand.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(grepCommand.getDocString().length() > 5);
  }

  @Test
  public void testGrepFindSimplePattern() {
    String argString = simpleGoatRegex + " " + poetryFileName;
    ArrayList<Token> arguments = Token.tokenize(argString);
    Result result = grepCommand.execute(shell, env, arguments);
    String stdOut = env.getCapturedOutput();
    assertFalse("Grep of " + simpleGoatRegex 
        + " should succeed", result.isError());
    assertThat(stdOut, CoreMatchers.containsString("goat"));
  }

  @Test
  public void testGrepDontFindSimplePattern() {
    String argString = simpleNotInGoatRegex + " " + poetryFileName;
    ArrayList<Token> arguments = Token.tokenize(argString);
    Result result = grepCommand.execute(shell, env, arguments);
    String stdOut = env.getCapturedOutput();
    assertFalse("Grep of " + simpleNotInGoatRegex 
        + " shouldn't fail thoug string not found",
        result.isError());
    assertEquals("Grep of " 
        + simpleNotInGoatRegex + " should give empty result", "",
        stdOut);
  }

}
