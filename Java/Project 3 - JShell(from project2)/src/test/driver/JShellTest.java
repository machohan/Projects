package driver;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Stack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JShellTest {

  InputStream simulatedInput;
  OutputStream simulatedOutput;
  PrintStream simulatedPrintStream;
  final static String mostSimpleInput = "exit\n";
  final static String usrLocalBin = "/usr/local/bin";
  final static String devNull = "/dev/null";
  JShell shell;

  @Before
  public void setUp() throws Exception {
    simulatedInput = new ByteArrayInputStream(mostSimpleInput.getBytes());
    simulatedOutput = new ByteArrayOutputStream();
    simulatedPrintStream = new PrintStream(simulatedOutput);
    shell = new JShell(simulatedInput, simulatedPrintStream);
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testStackInit() {
    Stack<String> dirStack = shell.getDirectoryStack();
    dirStack.push(devNull);
    dirStack.push(usrLocalBin);
    assertEquals(usrLocalBin, dirStack.pop());
    assertEquals(devNull, dirStack.pop());

  }

}
