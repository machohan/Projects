package interpreter.commands;

import interpreter.commands.Command;
import interpreter.commands.CommandMan;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import driver.JShell;
import filesystem.*;

/*
 * @author Hanifa Vanthaliwala
 */

public class CommandManTest {

  Command man;
  String input;
  ArrayList<Token> arguments;
  JShell shell;
  FileSystem fs;
  Environment env;
  static final int MIN_DOCSTRING_LENGTH = 5;

  @Before
  public void setUp() throws Exception {
	    man = new CommandMan();
	    shell = new JShell(System.in, System.out);
	    fs = shell.getFileSystem();
	    env = new Environment.Builder()
	    .withErrorCapture()
	    .withOutputCapture()
	    .build();
	   
    
  }

  @Test
  public void testGetName() {
    assertEquals("man", man.getName());
  }

  @Test
  public void testgetDocString() {
    assertTrue(man.getDocString().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute() {
    input = "echo";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell, env, arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute1() {
    input = "pwd";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,  arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute2() {
    input = "mkdir";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,  arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute3() {
    input = "cat";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,  arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute4() {
    input = "cd";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,  arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute5() {
    input = "ls";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,  arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute6() {
    input = "cp";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell, env, arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute7() {
    input = "man";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,  arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute8() {
    input = "man";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,  arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute9() {
    input = "pushd";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,  arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute10() {
    input = "popd";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env,arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

  @Test
  public void testexecute11() {
    input = "rm";
    arguments = Token.tokenize(input);
    Result result = man.execute(shell,env, arguments);
    assertFalse(result.isError());
    assertTrue(env.getCapturedOutput().length() >= MIN_DOCSTRING_LENGTH);
  }

}
