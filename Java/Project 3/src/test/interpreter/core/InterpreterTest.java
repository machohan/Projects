package interpreter.core;

import static org.junit.Assert.*;
import interpreter.commands.Command;
import interpreter.commands.CommandEcho;
import interpreter.commands.CommandExit;
import interpreter.result.Result;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import driver.JShell;
import filesystem.File;
import filesystem.Path;
import filesystem.PathException;

public class InterpreterTest {
  JShell shell;
  Interpreter interp;

  @Before
  public void setUp() throws Exception {
    shell = new JShell(System.in, System.out);
    interp = new Interpreter(shell);
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testAddAndGetCommand() {
    Command exitCommand = new CommandExit();
    interp.addCommand(exitCommand);
    Command retrievedCommand =
        interp.getCommandByToken(new Token(exitCommand.getName()));
    assertEquals(exitCommand,retrievedCommand);
  }
  
  /**
   * This unit test covers a lot of different packages and classes, oh my.
   * But redirection is such a complex behavior, we must do so. Note that
   * all of the sub-functionality is already tested in other unit tests.
   * 
   * @throws PathException 
   */
  @Test
  public void testBasicRedirection() throws PathException {
    Command echoCommand = new CommandEcho();
    interp.addCommand(echoCommand);
    Result result=interp.doLine("echo \"hi there feller\" > foo.txt");
    assertFalse(result.isError());
    Path fooPath=new Path(shell.getFileSystem(),"foo.txt");
    assertTrue(fooPath.exists());
    assertTrue(fooPath.isFile());
    File fooFile=fooPath.getFile();
    assertThat(fooFile.getContent(),startsWith("hi there feller"));
  }
  
  @Test
  public void testAppendRedirection() throws PathException {
    Command echoCommand = new CommandEcho();
    interp.addCommand(echoCommand);
    Result result=interp.doLine("echo \"hi there feller\" > foo.txt");
    assertFalse(result.isError());
    result = interp.doLine("echo fancyberonomous >> foo.txt");
    assertFalse(result.isError());
    Path fooPath=new Path(shell.getFileSystem(),"foo.txt");
    assertTrue(fooPath.exists());
    assertTrue(fooPath.isFile());
    File fooFile=fooPath.getFile();
    assertThat(fooFile.getContent(),startsWith("hi there feller"));
    assertThat(fooFile.getContent(),containsString("fancyberonomous"));
  }
  
  @Test
  public void testBlankLineNoError() {
    Result result=interp.doLine("");
    assertFalse(result.isError());
  }

}
