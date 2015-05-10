package interpreter.commands;

import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Error;

import java.util.ArrayList;

import interpreter.core.Environment;
import driver.JShell;
import filesystem.*;

public class CommandMv extends Command {


  /*
   * Moves a directory(recuresively) and/or file(s) from old path to a new path
   * both of which are specified by the user.
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute(JShell shell, Environment env,
      ArrayList<Token> arguments) {
    if (arguments.size() != 2) {
      env.err.println("mv takes two arguments");
      return new Error();
    } else {
      String src = arguments.get(0).getBody();
      String dest = arguments.get(1).getBody();
      FileSystemResult fsResult = shell.getFileSystem().move(src, dest);
      if (fsResult.status == FSResultType.Success) {
        return Command.okayResult;
      } else {
        env.err.println(fsResult.status.toString());
        return new Error();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "mv";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: mv -- move files" + "\n" + "\n"
        + "SYNOPSIS: mv oldpath newpath" + "\n" + "\n"
        + "DESCRIPTION: Move item oldpath to newpath. Both oldpath" + "\n"
        + "and newpath may be relative to the current directory " + "\n"
        + "or may be full paths. If newpath is a directory, move" + "\n"
        + "the item into the directory.";
  }

}
