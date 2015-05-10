package interpreter.commands;

import driver.*;
import filesystem.File;
import filesystem.Path;
import filesystem.PathException;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Error;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import interpreter.core.Environment;

public class CommandGet extends Command {

  /*
   * Attempts to get contents of the specified URL and saves them into a file. 
   * 
   * (non-Javadoc)
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */

  @Override
  public Result execute
    (JShell shell, Environment env, ArrayList<Token> arguments) {
    Iterator<Token> argItr = arguments.iterator();
    if (argItr.hasNext()) {
      while (argItr.hasNext()) {
        Token next = argItr.next();

        URL url;
        String fileName;
        BufferedReader input;
        String urlString;
        File file;

        try {
          url = new URL(next.getBody());
          urlString = url.toString();
          fileName = Path.baseName(urlString);
          input = new BufferedReader(new InputStreamReader(url.openStream()));
          String text;
          try {
            file = new Path(shell.getFileSystem(), fileName).getOrCreateFile();
          } catch (PathException e) {
        	  env.err.println(e.toString());
              return new Error();
         
          }
          while ((text = input.readLine()) != null) {
            file.appendToContent(text + "\n");
          }
        } catch (IOException e) {
        	env.err.println(e.getLocalizedMessage());
            return new Error();
        }
      }
      return Command.okayResult;
    } else {
    	env.err.println("get takes one argument");
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
    return "get";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: get -- attempts to retrieve data at the URL given" + "\n" 
        + "\n"
        + "SYNOPSIS: get URL" + "\n" 
        + "\n"
        + "DESCRIPTION: get URL command gets the contents at the URL given" 
        + "\n"
        + "in the argument and saves data at URL in a file and adds" 
        + "\n"
        + "this file to current working directory";
  }

}
