package interpreter.commands;

import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Success;
import interpreter.result.Error;

import java.util.ArrayList;

import driver.JShell;

/**
 * This command presents path of user's current working directory.
 *
 */
public class CommandPwd extends Command {

  /*
   * Prints the current working directory.
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute(JShell shell, Environment env,
      ArrayList<Token> arguments) {

    if (arguments.size() >= 1) {
      env.out.println("pwd does not take any arguments");
      return new Error();
    }
    env.out.println(shell.getFileSystem().getCwd() + "\n");
    return new Success();
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "pwd";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: pwd -- return working directory name" + "\n" + "\n"
        + "SYNOPSIS: pwd [-L | -P]" + "\n" + "\n"
        + "DESCRIPTION: The pwd utility writes the absolute pathname" + "\n"
        + "of the current working directory to the standard output.";
  }

}
