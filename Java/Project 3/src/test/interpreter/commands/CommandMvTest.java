package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.Command;
import interpreter.commands.CommandMkdir;
import interpreter.commands.CommandMv;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;
import interpreter.core.Environment;
import org.junit.Before;
import org.junit.Test;

import driver.JShell;

public class CommandMvTest {
  JShell shell;
  Command mdCommand;
  Command mvCommand;
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
    mvCommand = new CommandMv();
    
    env = new Environment.Builder()
      .withOutputCapture()
      .withErrorCapture()
      .build();

    // set up directory structure
    String[] mdSequence = 
      {"/usr", "/usr/local", "/usr/share", "/usr/local/bin"};
    for (String mdArgs : mdSequence) {
      arguments = Token.tokenize(mdArgs);
      mdCommand.execute(shell, env,arguments);
    }
  }

  @Test
  public void test1() {
    String mvArgs = "/usr/share /usr/gnu";
    arguments = Token.tokenize(mvArgs);
    result = mvCommand.execute(shell, env,arguments);
    assertFalse(result.isError());
  }

  @Test
  public void test2() {
    String mvArgs = "/usr/bogus /usr/gnu";
    arguments = Token.tokenize(mvArgs);
    result = mvCommand.execute(shell, env, arguments);
    assertTrue(result.isError());
  }

  @Test
  public void testGetName() {
    assertEquals("mv", mvCommand.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(mvCommand.getDocString().length() > 5);
  }
}
