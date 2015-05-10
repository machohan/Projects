/**
 * 
 */
package interpreter.commands;

import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Success;
import interpreter.result.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import driver.JShell;
import filesystem.*;

public class CommandRm extends Command {
  
  /*
   * Recursively deletes the files and/or directories listed in the arguments.
   * Queries the user on each item, unless the -f option is supplied as the
   * first argument.
   * 
   * @see interpreter.commands.Command#execute(driver.JShell, 
   *        interpreter.core.Environment, java.util.ArrayList)
   */
  
  public static boolean forceDelete = false;

  
  @Override
  public Result execute(JShell shell, Environment env,
      ArrayList<Token> arguments) {
    FileSystem fs = shell.getFileSystem();
    if (checkArgumentList(arguments) == false) {
      env.err.println("invalid arguments");
      return new Error();
    }

    StringBuilder sb = new StringBuilder();
    Iterator<Token> argumentItr = arguments.iterator();
    while (argumentItr.hasNext()) {// the -f option is removed at this point
      try {
        Path pathArgument = new Path(fs, argumentItr.next().getBody());
        if (!pathArgument.exists()) {
          env.err.println(pathArgument.toString() + ": no such file "
              + "or directory!");
          continue;
        }
        if (forceDelete) {// -f was issued along with the rm command

          pathArgument.delete();
        } else {// -f was not issued with the rm command

          if (pathArgument.isFile()) {// if it's a file, delete it!

            pathArgument.delete();
          } else if (pathArgument.isDirectory()) {// if it's a directory

            // is the directory empty
            Directory dir = pathArgument.getDirectory();
            if (dir.getEntries().isEmpty()) {
              pathArgument.delete();

            } else {// directory isn't empty! Delete recursively!
              deleteRecursivelyInDirectory(fs, env, pathArgument);
            }
          }
        }
      } catch (PathException e) {
        env.err.println(e.getMessage());
        continue;
      }
    }
    return new Success();
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getName()
   */
  @Override
  public String getName() {
    return "rm";
  }

  /*
   * (non-Javadoc)
   * 
   * @see interpreter.commands.Command#getDocString()
   */
  @Override
  public String getDocString() {
    return "NAME: rm -- removes directory/s or file/s from the file system"
        + "\n" + "SYNOPSIS: rm [-f] PATH ... \n"
        + "DESCRIPTION: Confirm	with the user that they	want to	delete"
        + " PATH, and if so, remove it from the JShell file system. If"
        + " PATH is a directory, recursively remove all files and"
        + " directories in it, prompting for confirmation for each one." + "\n"
        + "If [-f] is supplied, do not confirm: just remove";
  }

  /**
   * Validate the argument list passed in to the rm command and modify
   * forceDelete accordingly
   * 
   * @param arguments the list of arguments passed in to rm
   * 
   * @return true if the argument list is valid and false otherwise
   */
  public boolean checkArgumentList(ArrayList<Token> arguments) {
    if (arguments.size() >= 2) {
      if (arguments.get(0).getBody().equals("-f")
          || arguments.get(0).getBody().equals("-F")) {
        forceDelete = true;
        arguments.remove(0);
      }
      return true;
    } else if (arguments.size() == 1) {
      if (arguments.get(0).getBody().equals("-f")
          || arguments.get(0).getBody().equals("-F")) {
        return false;
      }
      return true;
    } else
      return false;
  }

  /**
   * helper function for execute() deletes the content of a non-empty directory
   * recursively asking the user before each delete to verify the deletion
   * 
   * recusiveDelete algorithm: -get the non-empty directory to delete -get the
   * entries in that directory -iterate through the entries one by one -if an
   * entry is a file, delete it -if an entry is a non-empty directory
   * recursiveDeleteDirectory((
   * 
   * @param path the path to parent directory
   * @param fs reference to a filesystem
   * @param node a reference to the parent directory
   * @return a string containing information about the deleted content under
   *         path
   */
  private Environment deleteRecursivelyInDirectory(FileSystem fs,
      Environment env, Path pathArgument) {
    Iterator<String> entryItr = null;
    Directory dir;
    HashMap<String, Node> entries;

    try {
      dir = pathArgument.getDirectory();
      entries = dir.getEntries();
      entryItr = entries.keySet().iterator();
    } catch (PathException e1) {
      env.err.println(e1.getMessage());
    }
    while (entryItr.hasNext()) {
      try {
        Path childPath = pathArgument.withSuffix(entryItr.next());
        if (childPath.isFile()) {// if it's a file
          promtToDelete(childPath.toString());// prompt to delete
          if (getPrompotResponseFromUser()) {// delete the file?
            childPath.delete();
          }
        } else {// if it's a directory
          Directory childDirectory = childPath.getDirectory();
          if (childDirectory.getEntries().isEmpty()) {// if it's empty
            promtToDelete(childPath.toString());// prompt to delete
            if (getPrompotResponseFromUser()) {

              childPath.delete();
            }
          } else {// entry directory isn't empty
            deleteRecursivelyInDirectory(fs, env, childPath);
          }
        }
      } catch (PathException e) {
        env.err.println(e.getMessage());
        continue;
      }
    }
    try {// delete the parent directory
      promtToDelete(pathArgument.toString());// prompt to delete
      if (getPrompotResponseFromUser()) {

        pathArgument.delete();
      }
    } catch (PathException e2) {
      env.err.println(e2.getMessage());
    }
    return env;
  }

  /**
   * helper function for execute()
   * 
   * @param pathString the path of the directory to delete
   */
  private void promtToDelete(String pathString) {
    System.out.println("Do you want to delete " + pathString + " y/n?");
  }

  /**
   * helper function for execute()
   * 
   * @return true if the user agrees to delete a directory and false otherwise
   */
  private boolean getPrompotResponseFromUser() {
    Scanner inScanner = new Scanner(System.in);
    return inScanner.nextLine().equalsIgnoreCase("y");
  }
}
