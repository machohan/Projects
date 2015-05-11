package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.CommandPwd;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.core.Environment;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import driver.JShell;

public class CommandPwdTest {

  JShell shell;
  ArrayList<Token> arguments;
  CommandPwd pwdCommand;
  Environment env;

  @Before
  public void setup() {
    shell = new JShell(System.in, System.out);
    pwdCommand = new CommandPwd();
    env = new Environment.Builder()
      .withErrorCapture()
      .withOutputCapture()
      .build();
  }

  @Test
  public void testExecute() {
    String input = "";
    arguments = Token.tokenize(input);
    Result result = pwdCommand.execute(shell, env,arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testGetName() {
    assertEquals("pwd", pwdCommand.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(pwdCommand.getDocString().length() > 5);
  }

}
