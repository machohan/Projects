package interpreter.commands;

import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Success;
import interpreter.result.Error;
import interpreter.core.Environment;



import java.util.ArrayList;
import java.util.Iterator;

import driver.JShell;
import filesystem.FSResultType;

public class CommandMan extends Command {

  /*
   * Provides a command's documentation to the user.
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute
    (JShell shell, Environment env, ArrayList<Token> arguments) {
    Iterator<Token> argItr = arguments.iterator();
    StringBuilder sb = new StringBuilder();
    // man takes a single argument
    if (arguments.size() != 1) {
    	//Is this correct.
    	env.err.println(FSResultType.IllegalOperation.toString());
      return new Error();
    }

    Token tokenToFind = argItr.next();
    Command commandFound = 
         shell.getInterpreter().getCommandByToken(tokenToFind);
    if (null == commandFound) {
      sb.append("Not found: ");
      sb.append(tokenToFind);
    } else {
      sb.append(commandFound.getDocString());
      sb.append("\n");
    }
    
    env.out.print(sb.toString());
    return new Success();
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "man";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: man -- provides information for command specified" + "\n" 
        + "\n"
        + "SYNOPSIS: man commannd_name" + "\n" 
        + "\n"
        + "DESCRIPTION: Prints documentation for command provided in" 
        + "argument";
  }

}
