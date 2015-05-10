package interpreter.commands;

import driver.*;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Error;

import java.util.ArrayList;

public class CommandExit extends Command {

  /*
   * Terminates the program.
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute
    (JShell shell, Environment env, ArrayList<Token> arguments) {
    if (arguments.size() > 0) {
      env.out.println("exit does not take arguments");
      return new Error();
    }
    shell.exit();
    return Command.okayResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "exit";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: exit -- terminates the program" + "\n" 
           + "\n" 
           + "SYNOPSIS: exit" + "\n" 
           + "\n"
           + "DESCRIPTION: This command will quit the program";
  }

}
