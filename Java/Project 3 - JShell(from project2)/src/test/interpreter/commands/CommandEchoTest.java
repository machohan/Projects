package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.Command;
import interpreter.commands.CommandEcho;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.hamcrest.CoreMatchers;
import org.junit.*;

import driver.JShell;

public class CommandEchoTest {

  JShell shell;
  Command echoCommand;
  ArrayList<Token> arguments;
  Result result;
  Environment env;
  OutputStream outCaptureStream;
  PrintStream outPrintStream;
  
  @Before
  public void setUp() throws Exception {
      shell = new JShell(System.in,System.out);
      echoCommand = new CommandEcho();
      outCaptureStream = new ByteArrayOutputStream();
      outPrintStream = new PrintStream(outCaptureStream);
      env=new Environment.Builder()
        .withOut(outPrintStream)
        .build();
  }
  
  @Test
  public void testEcho() {
    String echoArgs="hi";
    arguments = Token.tokenize(echoArgs);
    Environment myEnv = new Environment.Builder()
      .withOutputCapture()
      .build();
    result = echoCommand.execute(shell, myEnv, arguments);
    assertFalse(result.isError());
    assertThat(myEnv.getCapturedOutput(),CoreMatchers.startsWith("hi"));
  }
  
  @Test
  public void testEchoTooManyArgumentsError() {
    String echoArgs="hi there";
    arguments = Token.tokenize(echoArgs);
    Environment myEnv = new Environment.Builder()
      .withOutputCapture()
      .withErrorCapture()
      .build();
    result = echoCommand.execute(shell, myEnv, arguments);
    assertTrue(result.isError());
    assertThat(myEnv.getCapturedError(),CoreMatchers.containsString("arg"));
  }

  @Test
  public void testGetName(){
	  assertEquals("echo",echoCommand.getName());
  }
  
  @Test
  public void testGetDocString(){
	  assertTrue(echoCommand.getDocString().length() > 5);
  }

}
