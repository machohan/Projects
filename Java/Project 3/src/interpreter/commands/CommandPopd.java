package interpreter.commands;

import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Error;
import interpreter.result.Result;

import java.util.ArrayList;

import driver.JShell;

public class CommandPopd extends Command {
  
  /*
   * Provides functionality of popd commands.
   * 
   * This class removes the top most directory on the stack and makes it the
   * the current working directory.
   * 
   * When there is no directory onto the stack, the execute method will give
   * error message.
   * 
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute
    (JShell shell, Environment env, ArrayList<Token> arguments) {
    int stackSize = shell.getDirectoryStack().size();
    if (arguments.size() != 0) {
      env.err.println("popd does not take arguments");
      return new Error();
    }
    if (stackSize == 0) {
    	env.err.println("stack size not sufficient");
      return new Error();
    }
    String cdPath = shell.getDirectoryStack().get(stackSize - 1);
    shell.getDirectoryStack().pop().charAt(stackSize - 1);
    CommandCd cd = new CommandCd();
    ArrayList<Token> cdArgs = Token.tokenize(cdPath);
    Result result = cd.execute(shell, env,cdArgs);
    return result;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "popd";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: popd -- deletes last saved cwd and changes directory to"
        + " new new top path in directory statck." + "\n" 
        + "\n" + "SYNOPSIS: popd" + "\n" + "\n"
        + "DESCRIPTION: Removes the top entry from the directory stack,"
        + " and changes directory to the new top directory."
        + "The popd command removes the top most directory onto the"
        + " stack and makes it the current workign directory.";
  }

}
