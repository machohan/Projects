package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.CommandMkdir;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.core.Environment;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import driver.JShell;

public class CommandMkdirTest {

  JShell shell;
  ArrayList<Token> arguments;
  CommandMkdir mkdirCommand;
  String input;
  Environment env;

  @Before
  public void setup() {
    shell = new JShell(System.in, System.out);
    mkdirCommand = new CommandMkdir();
  }

  @Test
  public void testExecute() {
    input = "myfolder";
    arguments = Token.tokenize(input);
    Result result = mkdirCommand.execute(shell, env,arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testGetName() {
    assertEquals("mkdir", mkdirCommand.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(mkdirCommand.getDocString().length() > 5);
  }

}
