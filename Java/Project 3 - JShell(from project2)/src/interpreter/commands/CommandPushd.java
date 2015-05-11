package interpreter.commands;

import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Error;

import java.util.ArrayList;

import driver.JShell;

public class CommandPushd extends Command {

  /*
   * Provides functionality to save current working directory onto
   * defined stack in JShell and then changes the new current working directory
   * stated by the user with the command. 
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute
    (JShell shell, Environment env, ArrayList<Token> arguments) {
    if (arguments.size() != 1) {
    	env.err.println("pushd takes only one argument");
      return new Error();
    }
    String oldCwd = shell.getFileSystem().getCwd();
    CommandCd cd = new CommandCd();
    Result result = cd.execute(shell, env, arguments);
    if (!result.isError()) {
      shell.getDirectoryStack().push(oldCwd);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "pushd";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: pushd -- saves currently working directory and move to"
        + "change directoru to DIR" + "\n" + "\n" + "SYNOPSIS: pushd DIR" 
        + "\n" + "\n"
        + "DESCRIPTION: Saves the current working directory " + "\n"
        + "onto stack and then changes the new current working " + "\n" 
        + "directory DIR";
  }

}
