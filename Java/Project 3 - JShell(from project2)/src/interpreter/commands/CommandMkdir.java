package interpreter.commands;

import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Success;
import interpreter.result.Error;

import java.util.ArrayList;
import java.util.Iterator;

import interpreter.core.Environment;
import driver.JShell;
import filesystem.FSResultType;
import filesystem.FileSystemResult;

public class CommandMkdir extends Command {

  /*
   * Make one or more directories as directed by the user.
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute
    (JShell shell, Environment env, ArrayList<Token> arguments) {
    Iterator<Token> argItr = arguments.iterator();
    if (arguments.isEmpty()) {
    	env.err.println("Enter path or new directory name");
      return new Error();
    }
    while (argItr.hasNext()) {
      Token t = argItr.next();
      String path = t.getBody();
      FileSystemResult result = shell.getFileSystem().mkdir(path);
      if (result.status != FSResultType.Success) {
    	  env.err.println(path+": "+result.status.toString());
        return new Error();
      }
    }
    return Command.okayResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "mkdir";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: mkdir -- make directories" + "\n" + "\n"
        + "SYNOPSIS: mkdir [-pv] [-m mode] directory_name ..." + "\n" + "\n"
        + "DESCRIPTION: The mkdir utility creates the directories named as" 
        + "\n"
        + "operands, in the order specified.";
  }

}
