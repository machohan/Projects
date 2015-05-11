package interpreter.commands;

import driver.*;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.*;

import java.util.ArrayList;

/**
 * The Command class is the abstract base class from which all
 * interpreter.commands are derived. Each class that extends Command must
 * implement at least three methods:
 * 
 * {@code execute()} {@code getName()} {@code getDocString()}
 * 
 * The class also provides a convenient protected static member
 * {@code OkayResult} for a successful result of {@code execute()}
 * 
 */
abstract public class Command {

  /**
   * Execute implements the functionality specific to the command.
   * 
   * @param context The shell executing this command
   * @param arguments A list of Tokens given as arguments (parameters) to the
   *        command
   * @return the result; use Command.okayResult as a convenient default for
   *         successful execution, or (alternately) new Success() and new
   *         Error()
   */
  public abstract Result execute(JShell shell, Environment env,
      ArrayList<Token> arguments);

  /**
   * return the name of this command. Useful for command-line parsing, where the
   * name may be used as the token that invokes the command
   * 
   * @return The name of the command.
   */
  public abstract String getName();

  /**
   * return the string documenting this command, for use in "man"
   * 
   * @return description or documentation of this command
   */

  public abstract String getDocString();

  /**
   * a convenience for return from a successful execution of a Command.
   */
  public static Result okayResult = new Success();

}
