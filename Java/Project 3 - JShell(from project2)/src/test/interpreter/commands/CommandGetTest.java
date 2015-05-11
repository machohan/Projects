package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.CommandGet;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import interpreter.core.Environment;
import driver.JShell;

public class CommandGetTest {

  JShell shell;
  CommandGet get;
  ArrayList<Token> arguments;
  Environment env;

  @Before
  public void setup() {
    shell = new JShell(System.in, System.out);
    get = new CommandGet();
  }

  @Test
  public void testExecute() {
    String url = "http://www.textfiles.com/100/914bbs.txt";
    arguments = Token.tokenize(url);
    Result result = get.execute(shell,env, arguments);
    assertFalse(result.isError());
  }

  @Test
  public void testGetName() {
    assertEquals("get", get.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(get.getDocString().length() > 5);
  }

}
