package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.Command;
import interpreter.commands.CommandPushd;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import driver.JShell;

public class CommandPushdTest {

  JShell shell;
  Command pushDCommand;
  ArrayList<Token> arguments;
  Environment env;

  @Before
  public void setup() {
    shell = new JShell(System.in, System.out);
    pushDCommand = new CommandPushd();
    env = new Environment.Builder()
    .withErrorCapture()
    .withOutputCapture()
    .build();
  }

  @Test
  public void testExecute() {
    String cdDir = "/";
    arguments = Token.tokenize(cdDir);
    Result result = pushDCommand.execute(shell, env, arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testGetName() {
    assertEquals("pushd", pushDCommand.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(pushDCommand.getDocString().length() > 5);
  }

}
