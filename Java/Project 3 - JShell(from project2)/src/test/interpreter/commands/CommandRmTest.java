/**
 * 
 */
package interpreter.commands;

import static org.junit.Assert.*;
import filesystem.FileSystem;
import filesystem.Path;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;

import java.util.ArrayList;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import driver.JShell;


/**
 * @author Ammar
 * @author mwb
 *
 */
public class CommandRmTest {

  JShell shell;
  Environment env;
  String input ;
  FileSystem fs;
  Command rmCommand;
  ArrayList<Token> arguments;
  Result result;
  String usr="/usr";
  String usrLocal=usr+"/local";
  String usrLocalBin=usrLocal+"/bin";
  String usrLocalShare=usrLocal+"/share";


  @Before
  public void setup(){
    shell = new JShell(System.in,System.out);
    env = new Environment.Builder()
    .withErrorCapture()
    .withOutputCapture()
    .build();
    fs = shell.getFileSystem();
    rmCommand = new CommandRm();
    fs.mkdir(usr);
    fs.mkdir(usrLocal);     
    fs.mkdir(usrLocalBin);
    fs.mkdir(usrLocalShare);
  }


  /**
   * Test method for {@link interpreter.commands.CommandRm#execute(driver.JShell, java.util.ArrayList)}.
   */
  @Test
  public void testSimpleRm() {
    arguments=Token.tokenize("-f "+usrLocalBin);
    Path path=new Path(fs, usrLocalBin);
    assertTrue(path+" should exist before rm",path.exists());
    result=rmCommand.execute(shell, env, arguments);
    assertFalse("a simple rm -f should succeed!",
        result.isError());
    String stdOut = env.getCapturedOutput();
    assertFalse(stdOut,path.exists());
    }
  

  /**
   * Test method for {@link interpreter.commands.CommandRm#getName()}.
   */
  @Test
  public void testGetName() {
    assertEquals("rm",rmCommand.getName());
  }

  /**
   * Test method for {@link interpreter.commands.CommandRm#getDocString()}.
   */
  @Test
  public void testGetDocString() {
    assertThat(rmCommand.getDocString(),CoreMatchers.containsString("delete"));
  }

}
