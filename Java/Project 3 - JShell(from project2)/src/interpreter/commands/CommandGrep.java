package interpreter.commands;

import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Error;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import interpreter.core.Environment;
import driver.JShell;
import filesystem.Directory;
import filesystem.Path;
import filesystem.PathException;

/**
 * Implement the Grep command.
 * 
 * @author mwb
 *
 */

public class CommandGrep extends Command {

  /*
   * Matches the regular expression given by user in the command with text in
   * one or more files. Where regex is matched, it will extract the line and
   * print to file or console. 
   * 
   * (non-Javadoc)
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  @Override
  public Result execute
    (JShell shell,Environment env, ArrayList<Token> arguments) {
    boolean recursive = false;
    if (arguments.size() > 0 && arguments.get(0).getBody().matches("-[rR]")) {
      recursive = true;
      arguments.remove(0);
    }
    Result result = Command.okayResult;

    Pattern pattern;
    if (arguments.size() == 0) {
      env.err.println("grep requires a pattern");
      return new Error();
    } else {
      pattern = Pattern.compile(arguments.get(0).getBody());
      arguments.remove(0);
    }
    Iterator<Token> argIter = arguments.iterator();
    if (!argIter.hasNext()) {
      env.err.println("grep requires at least one file or directory name");
      return new Error();
    }

    try {
      while (argIter.hasNext()) {
        Path path = new Path(shell.getFileSystem(), argIter.next().getBody());

        if (!path.exists()) {
          env.err.println(path + ": doesn't exist");
          result=new Error();
          continue;
        } else if (path.isFile()) {
          simpleGrep(env, pattern, path, recursive);
        } else {
          if (!recursive) {
            env.err.println(path + ": is a directory and -R not specified");
            result=new Error();
            continue;
          } else {
            directoryGrep(env, pattern, path);
          }
        }
      }
    } catch (PathException e) {
      env.err.println(e.toString());
      return new Error();
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "grep";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "grep [-R] REGEX PATH ...\n" + "\n"
    + "If -R is not supplied, print any lines containing REGEX in PATH,\n"
    + "which must be a file. If -R is supplied, and PATH is a directory,\n"
    + "recursively traverse the directory, and, for all lines in all files\n"
    + "that contain REGEX, print the path to the file\n"
    + "(including the filename)\n";
  }

  /**
   * Worker method. Go through a single file and discover all occurrences of the pattern (if any)
   * and return a string containing each line that contains the match. If withTag is specified, the
   * name of the file is included before the line.
   * 
   * @param pattern The pattern to search for.
   * @param path The file to search
   * @param withTag If true, prints the filename before each matched line
   * @return a string containing matches.
   * @throws PathException
   */
  private void simpleGrep(Environment env, Pattern pattern, 
      Path path, boolean withTag) throws PathException {
    String[] lines = path.getFile().getContent().split("\n");
    for (String line : lines) {
      if (pattern.matcher(line).find()) {
        if (withTag) {
          env.out.print(path.toString()+":");
        }
        env.out.println(line);
      }
    }
  }

  /**
   * Worker method. Go through a directory. For each file, perform simple grep.
   * For each directory, recursively invoke directoryGrep.
   * 
   * @param pattern The pattern to search for
   * @param path The path to the directory
   * @return All the matches, formatted a la simpleGrep() withTag.
   * @throws PathException If e.g. the path is not actually a directory.
   */
  private void directoryGrep(Environment env, Pattern pattern, Path path) 
      throws PathException {
    StringBuilder sb = new StringBuilder();
    Directory dir = path.getDirectory();
    for (String name : dir.getEntries().keySet()) {
      Path entryPath = path.withSuffix(name);
      if (entryPath.isFile()) {
       simpleGrep(env, pattern, entryPath, true);
      } else {
        directoryGrep(env, pattern, entryPath);
      }
    }
  }
}
