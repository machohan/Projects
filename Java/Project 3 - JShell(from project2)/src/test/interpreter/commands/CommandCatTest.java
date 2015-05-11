package interpreter.commands;

import static org.junit.Assert.*;

import org.junit.*;

import interpreter.commands.Command;
import interpreter.commands.CommandCat;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import filesystem.FileSystem;
import driver.JShell;

public class CommandCatTest {

  private JShell shell;
  Command catCommand;
  Environment env;
  ArrayList<Token> arguments;
  String helloPath = "hello_txt";
  String helloContent = "fizzywatermas";
  String bogusPath = "/file/bogus/foobar/nuts";

  @Before
  public void setUp() throws Exception {
	env = new Environment.Builder()
	.withErrorCapture()
	.withOutputCapture()
	.build();
    shell = new JShell(System.in, System.out);
    catCommand = new CommandCat();
    FileSystem fs = shell.getFileSystem();
    fs.mkdir("/usr");
    fs.createFile(helloPath);
    fs.writeFile(helloPath, helloContent);
  }

  @Test
  public void testExecuteSuccess() {
    arguments = Token.tokenize(helloPath);
    Result result = catCommand.execute(shell, env, arguments);
    assertFalse(result.isError());
    assertEquals(helloContent, env.getCapturedOutput());
  }

  @Test
  public void testExecuteMissingFileFailure() {
    arguments = Token.tokenize(bogusPath);
    Result result = catCommand.execute(shell, env, arguments);
    assertTrue(result.isError());
  }

  @Test
  public void testExecuteTestContent() {
    arguments = Token.tokenize(helloPath);
    Result result = catCommand.execute(shell, env, arguments);
    assertFalse(result.isError());
    assertEquals(helloContent, env.getCapturedOutput());
  }

  @Test
  public void testGetName() {
    assertEquals("cat", catCommand.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(catCommand.getDocString().length() > 5);
  }
}
