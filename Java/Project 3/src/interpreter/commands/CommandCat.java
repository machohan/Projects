package interpreter.commands;

import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.*;
import interpreter.result.Error;

import java.util.ArrayList;

import driver.JShell;
import filesystem.FSResultType;
import filesystem.FileSystem;
import filesystem.FileSystemStringResult;

public class CommandCat extends Command {

  /*
   * Prints the content of file if it exists; generates an error otherwise.
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */


  @Override
  public Result execute(JShell shell, Environment env,
      ArrayList<Token> arguments) {

    if (arguments.size() != 1) {
      env.err.println("cat only takes 1 arguments");
      return new Error();
    }

    FileSystem fs = shell.getFileSystem();
    String fileName = arguments.get(0).toString();

    FileSystemStringResult fsSR = fs.readFile(fileName);

    if (fsSR.status == FSResultType.Success) {
      env.out.print(fsSR.content);
      return new Success();
    } else {
      env.err.println("cat: " + fileName + ": " + fsSR.status.toString());
      return new Error();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "cat";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: cat --Display the contents of file in the shell" + "\n"
        + "\n" + "SYNOPSIS: cat file" + "\n" + "\n"
        + "DESCRIPTION: File must be in current path";
  }

}
