package interpreter.commands;

import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Success;
import interpreter.result.Error;

import java.util.ArrayList;
import java.util.Iterator;

import interpreter.core.Environment;
import driver.JShell;
import filesystem.Directory;
import filesystem.FileSystem;
import filesystem.Node;
import filesystem.Path;
import filesystem.PathException;

/**
 * Implements the ls command.
 * 
 *
 */
public class CommandLs extends Command {

  /*
   * Implements the ls command, ala Unix and its derivatives.
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute(JShell shell, Environment env,
      ArrayList<Token> arguments) {
    FileSystem fs = shell.getFileSystem();
    boolean recursive = false;
    if (!arguments.isEmpty()
        && (arguments.get(0).getBody().equals("-R") 
            || arguments.get(0).getBody().equals("-r"))) {
      recursive = true;
      arguments.remove(0);
    }
    try {
      if (arguments.isEmpty()) {
        listFileOrDirectory(env,new Path(fs,"."), recursive);
      } else {
        Iterator<Token> argItr = arguments.iterator();
        while (argItr.hasNext()) {
          listFileOrDirectory(env,new Path(fs,argItr.next().getBody()), 
              recursive);
        }
      }
      return new Success();
    } catch (PathException e) {
      env.err.println(e.toString());
      return new Error();
    }

  }

  /**
   * The worker function to list directories and files, including recursive
   * listing of contents.
   * 
   * @param fs the filesystem we're working in
   * @param path the path to list
   * @param recursive if true, output will include a recursive listing of all
   *        subdirectories (and their files therein)
   * @return a string containing the entire listing generated.
   * @throws PathException upon failure to find a file, directory, etc;
   *         propagated up from the Path instance.
   */

  private void listFileOrDirectory(Environment env, Path path, boolean recursive) 
      throws PathException {
    Node node = path.getNode();
    if (node.isFile()) {
      env.out.println(path.toString());
    } else if (node.isDirectory()) {
      Directory dir = (Directory) node;
      env.out.println(path.toString()+":");
      for( String name : dir.getEntries().keySet()) {
        env.out.print(name+"\t");
      }
      if (!dir.getEntries().isEmpty()) {
        env.out.println();
      }
      if (recursive) {
        for (String name: dir.getEntries().keySet()) {
          Path possiblePath = path.withSuffix(name);
          if (possiblePath.isDirectory()) {
            listFileOrDirectory(env,possiblePath,recursive);
          }
        }
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
    return "ls";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: " + "ls -- list directory contents" + "\n" + "\n"
        + "SYNOPSIS: ls [-R] [file ...]" + "\n" + "\n"
        + "DESCRIPTION: For each operand that names a file of a type other"
        + "\n"
        + "than directory, ls displays its name as well as any requested,"
        + "\n"
        + "associated information.  For each operand that names a file of"
        + "\n"
        + "type directory, ls displays the names of files contained within"
        + "\n"
        + "that directory, as well as any requested associated information"
        + "\n"
        + "If no operands are given, the contents of the current directory"
        + "\n" + "are displayed. ";
  }

}
