package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.Command;
import interpreter.commands.CommandLs;
import interpreter.commands.CommandMkdir;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import org.junit.*;

import driver.JShell;

public class CommandLsTest {
  JShell shell;
  Command mdCommand;
  Command lsCommand;
  String argumentsString;
  ArrayList<Token> arguments;
  Result result;
  Environment env;

  @Before
  public void setUp() throws Exception {
    shell = new JShell(System.in, System.out);
    env = new Environment.Builder()
    .withErrorCapture()
    .withOutputCapture()
    .build();
    mdCommand = new CommandMkdir();
    lsCommand = new CommandLs();

    // set up directory structure
    String[] mdSequence = 
       {"/usr", "/usr/local", "/usr/share", "/usr/local/bin"};
    for (String mdArgs : mdSequence) {
      arguments = Token.tokenize(mdArgs);
      mdCommand.execute(shell, env, arguments);
    }
  }

  @Test
  public void testLs() {
    String lsArgs = "";
    arguments = Token.tokenize(lsArgs);
    result = lsCommand.execute(shell,env, arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testLsPath1() {
    String lsArgs = "/usr";
    arguments = Token.tokenize(lsArgs);
    result = lsCommand.execute(shell,env, arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testLsPath2() {
    String lsArgs = "/usr/local";
    arguments = Token.tokenize(lsArgs);
    result = lsCommand.execute(shell,env,arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testLsPath3() {
    String lsArgs = "/usr/share";
    arguments = Token.tokenize(lsArgs);
    result = lsCommand.execute(shell,env, arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testLsPath4() {
    String lsArgs = "/usr/local/bin";
    arguments = Token.tokenize(lsArgs);
    result = lsCommand.execute(shell, env,arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testLsRecursive() {
    String lsArgs = "-R /usr";
    arguments = Token.tokenize(lsArgs);
    result = lsCommand.execute(shell,env, arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testGetName() {
    assertEquals("ls", lsCommand.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(lsCommand.getDocString().length() > 5);
  }
}
