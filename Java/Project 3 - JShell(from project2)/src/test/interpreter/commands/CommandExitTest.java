package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.CommandExit;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import driver.JShell;

public class CommandExitTest {

  JShell shell;
  ArrayList<Token> arguments;
  CommandExit commandExit;
  Environment env;

  @Before
  public void setup() {
    shell = new JShell(System.in, System.out);
    commandExit = new CommandExit();
    env = new Environment.Builder()
        .withErrorCapture()
        .withOutputCapture()
        .build();
  }

  @Test
  public void testExecute() {
    String input = "";
    arguments = Token.tokenize(input);
    assertTrue(shell.isRunning());
    Result result = commandExit.execute(shell, env, arguments);
    assertFalse(result.isError());
    String expected = "";
    String actual = env.getCapturedOutput();
    assertTrue(expected.equals(actual));
    assertFalse(shell.isRunning());
  }

  @Test
  public void testGetName() {
    assertEquals("exit", commandExit.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(commandExit.getDocString().length() > 5);
  }

}
