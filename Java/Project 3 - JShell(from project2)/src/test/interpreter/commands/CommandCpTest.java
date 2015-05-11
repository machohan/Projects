package interpreter.commands;

import static org.junit.Assert.*;
import interpreter.commands.Command;
import interpreter.commands.CommandCp;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import filesystem.*;
import driver.JShell;

public class CommandCpTest {

  JShell shell;
  Environment env;
  Command cpCommand;
  ArrayList<Token> arguments;

  FileSystem fs;
  final String usr = "/usr";
  final String usrLocal = usr + "/local";
  final String usrLocalBin = usrLocal + "/bin";
  final String usrLocalShare = usrLocal + "/share";
  final String docFileName = usrLocalShare + "/info.txt";
  final String docFileText = "This is some very important documentation blah";

  @Before
  public void setup() {
    shell = new JShell(System.in, System.out);
    env = new Environment.Builder()
    .withErrorCapture()
    .withOutputCapture()
    .build();
    fs = shell.getFileSystem();
    cpCommand = new CommandCp();
    fs.mkdir(usr);
    fs.mkdir(usrLocal);
    fs.mkdir(usrLocalBin);
    fs.mkdir(usrLocalShare);
    fs.createFile(docFileName);
    File docFile = (File) fs.resolvePath(docFileName).node;
    docFile.setContent(docFileText);
  }

  @Test
  public void testExecuteSimpleCp() {
    final String docFileDest = "/info.txt";
    final String input = docFileName + " " + docFileDest;
    Result result = cpCommand.execute(shell, env, Token.tokenize(input));
    assertFalse(result.isError());
    FileSystemResolveResult resolveResult = fs.resolvePath(docFileDest);
    assertEquals(FSResultType.Success, resolveResult.status);
    assertTrue(resolveResult.node.isFile());
    assertEquals(docFileText, ((File) resolveResult.node).getContent());
  }

  @Test
  public void testExecuteComplexCp() {
    final String cpDest = "/copy";
    fs.mkdir(cpDest);
    final String input = usr + " " + cpDest;
    Result result = cpCommand.execute(shell, env, Token.tokenize(input));
    assertFalse(result.isError());
    // the directory structure we get should look like this:
    Object[][] structure =
        { {usr, Directory.class}, {usrLocal, Directory.class}, 
        {usrLocalBin, Directory.class}, {usrLocalShare, Directory.class}, 
        {docFileName, File.class}};
    for (int i = 0; i < structure.length; i++) {
      String path = cpDest + "/" + structure[i][0];
      FileSystemResolveResult resolveResult = fs.resolvePath(path);
      assertEquals(FSResultType.Success, resolveResult.status);
      assertEquals(structure[i][1], resolveResult.node.getClass());
    }
  }

  @Test
  public void testExecuteCpLoopDetection() {
    final String input = usr + " " + usrLocalShare;
    Result result = cpCommand.execute(shell, env, Token.tokenize(input));
    assertTrue(result.isError());
    String errOut = env.getCapturedError();
    assertTrue(errOut.length() > 5);
  }

  @Test
  public void testGetName() {
    assertEquals("cp", cpCommand.getName());
  }

  @Test
  public void testGetDocString() {
    assertTrue(cpCommand.getDocString().length() > 5);
  }

}
