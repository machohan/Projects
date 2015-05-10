package interpreter.commands;

import driver.*;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Success;
import interpreter.result.Error;

import java.util.ArrayList;

public class CommandEcho extends Command {

  /*
   * Writes text to the console or to the stated file by user 
   * (note that redirection is implemented by the Interpreter, not
   * this command itself)
   * 
   *  @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */

  public Result execute
      (JShell shell, Environment env, ArrayList<Token> arguments) {

    if (arguments.size() == 1 ) {
      env.out.println(arguments.get(0).getBody().toString());
      return new Success();
    } else {
      env.err.println("Error: echo: wrong number of arguments");
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
    return new String("echo");
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: echo -- prints out, write string to, or append to file"
        + "\n" + "\n" 
        + "SYNOPSIS: a.) echo STRING" + "\n"
        + "b.) echo STRING > FILE or " + "\n"
        + "c.)echo STRING >> FILE" + "\n" 
        + "\n"
        + "DESCRIPTION: " + "\n"
        + "a.) Will simply print out the STRING" + "\n"
        + "b.) Will write the string to a file, replacing the file's"
        + " contents (if it exists)." + "\n"
        + "c.) Will append the string to the file, or create the file"
        + " if it doesn't already exist.";

  }

}
