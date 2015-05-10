package interpreter.commands;

import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Error;
import interpreter.result.Success;

import java.util.ArrayList;

import driver.JShell;
import interpreter.core.Environment;
import filesystem.FSResultType;
import filesystem.FileSystem;
import filesystem.FileSystemResult;

public class CommandCd extends Command {


  /*
   * Change the current working directory to directory named in the
   * arguments.
   * 
   */
  @Override
  public Result execute
    (JShell shell, Environment env, ArrayList<Token> arguments) {
    if (arguments.size() != 1) {
    	env.err.println("cd takes one argument");
      return new Error();
    }
    FileSystem fs = shell.getFileSystem();

    Token token = arguments.get(0);

    String path = token.getBody();

    FileSystemResult result = fs.setCwd(path);

    if (result.status == FSResultType.Success) {
      return okayResult;
    } else {
    	env.err.println(result.status.toString());
    	
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
    return "cd";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: cd -- change directory" + "\n" + "\n" 
        + "SYNOPSIS: a.) cd DIR b.) cd .. c.) cd ."
        + "\n" + "\n" 
        + "DESCRIPTION: a.) Change directory to DIR which maybe relative to" 
        + "\n"
        + "the current directory or maybe a full path b.) cd .. means a " 
        + "\n"
        + "parent  directory   and d.) cd . means the current directory" 
        + "\n"
        + "The directory separator must be /, the forward slash. The root" 
        + "\n"
        + "of the file system is a single slash: /";
  }

}
