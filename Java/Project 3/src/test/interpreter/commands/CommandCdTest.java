package interpreter.commands;

import static org.junit.Assert.*;

import org.junit.*;

import interpreter.commands.Command;
import interpreter.commands.CommandCd;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import filesystem.FileSystem;
import driver.JShell;

public class CommandCdTest {

  JShell shell;
  String input;
  FileSystem fs;
  Command command;
  ArrayList<Token> arguments;
  String usr = "/usr";
  String usrLocal = usr + "/local";
  String usrLocalBin = usrLocal + "/bin";
  String usrLocalShare = usrLocal + "/share";
  Environment env;

  @Before
  public void setup() {
    shell = new JShell(System.in, System.out);
    fs = shell.getFileSystem();
    command = new CommandCd();
    fs.mkdir(usr);
    fs.mkdir(usrLocal);
    fs.mkdir(usrLocalBin);
    fs.mkdir(usrLocalShare);
    env = new Environment.Builder()
    .withErrorCapture()
    .withOutputCapture()
    .build();
  }

  /**
   * Test method for
   * {@link interpreter.commands.CommandCd#execute(driver.JShell, java.util.ArrayList)} .
   */
  @Test
  public void testExecuteSimpleCdAbsolutePath() {
    arguments = Token.tokenize(usr);
    Result result = command.execute(shell, env, arguments);
    assertFalse(result.isError());
    assertEquals(usr, shell.getFileSystem().getCwd());
  }

  @Test
  public void testExecuteSimpleCdRelativePath() {
    String relative = "usr"; // where we're going
    String absolute = "/usr"; // what cwd will report
    arguments = Token.tokenize(relative);
    Result result = command.execute(shell, env, arguments);
    assertFalse(result.isError());
    assertEquals(absolute, shell.getFileSystem().getCwd());
  }

  @Test
  public void testExecuteSimpleCdFail() {
    String target = "/bogus-bogus-bogus";
    arguments = Token.tokenize(target);
    Result result = command.execute(shell, env, arguments);
    assertTrue(result.isError());

  }

  @Test
  public void testExecuteComplexCd() {
    Result result = command.execute(shell, env,Token.tokenize(usrLocalBin));
    assertFalse(result.isError());
    assertEquals(usrLocalBin, fs.getCwd());
    result = command.execute(shell, env, Token.tokenize(".."));
    assertFalse(result.isError());
    assertEquals(usrLocal, fs.getCwd());
    result = command.execute(shell, env, Token.tokenize(".."));
    assertFalse(result.isError());
    assertEquals(usr, fs.getCwd());
    result = command.execute(shell, env, Token.tokenize(usrLocalShare));
    assertFalse(result.isError());
    assertEquals(usrLocalShare, fs.getCwd());
  }

  @Test
  public void testExecuteCdDotInRoot() {
    Result result = command.execute(shell, env, Token.tokenize("."));
    assertFalse(result.isError());
    assertEquals("/", fs.getCwd());
  }

  @Test
  public void testExecuteCdDotDotInRoot() {
    Result result = command.execute(shell, env,Token.tokenize(".."));
    assertFalse(result.isError());
    assertEquals("/", fs.getCwd());
  }

  /**
   * Test method for {@link interpreter.commands.CommandCd#getName()}.
   */
  @Test
  public void testGetName() {
    assertEquals("cd", command.getName());
  }

  /**
   * Test method for {@link interpreter.commands.CommandCd#getDocString()}.
   */
  @Test
  public void testGetDocString() {
    assertTrue(command.getDocString().length() > 5);
  }

}
