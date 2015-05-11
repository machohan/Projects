package interpreter.core;

import driver.*;
import filesystem.File;
import filesystem.Path;
import filesystem.PathException;
import interpreter.commands.Command;
import interpreter.result.Result;
import interpreter.result.Success;
import interpreter.result.Error;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The Interpreter class keeps the list of interpreter.commands, associates
 * their command names, and dispatches the interpreter.commands based on user
 * input..
 * 
 * @author mwb
 *
 */
public class Interpreter {

  private HashMap<String, Command> commandMap;
  private JShell shell;

  public Interpreter(JShell shell) {
    this.shell = shell;
    this.commandMap = new HashMap<String, Command>();
  }

  /**
   * Execute a line of input. Tokenizes the input, looks up the first token in
   * our map of interpreter.commands, and then dispatches the remainder of the
   * line as tokenized arguments to the specific Command object.
   * 
   * @param line A line of text from the user.
   * @return a Result--success or failure, with any message--from executing the
   *         command.
   */
  public Result doLine(String line) {

    ArrayList<Token> tokens = Token.tokenize(line);
    if (tokens.isEmpty()) {
      return Command.okayResult;
    } else {
      Token commandToken = tokens.get(0);
      Environment env =
          new Environment.Builder().withOut(shell.getPrintStream())
              .withIn(shell.getInputStream()).build();
      Command command = getCommandByToken(commandToken);
      if (command == null) {
        env.err.println("Unrecognized Command");
        return new Error();
      } else {
        tokens.remove(0);
        return executeHandlingRedirect(env, command, tokens);
      }
    }
  }

  /**
   * Handle redirection, then execute the command
   * 
   * @param command the command to execute
   * @param args the arguments from the command line
   * @return the Result: success or failure
   */
  private Result executeHandlingRedirect(Environment env, Command command,
      ArrayList<Token> args) {
    boolean redirect = false;
    boolean append = false;
    String output = null;
    for (int i = 0; i < args.size(); i++) {
      if (args.get(i).getBody().equals(">")
          || args.get(i).getBody().equals(">>")) {
        if (redirect) {
          env.err.println("too many redirections");
          return new Error();
        } else if (i + 1 >= args.size()) {
          env.err.println("redirection without a target");
          return new Error();
        } else {
          redirect = true;
          append = (args.get(i).getBody().equals(">>"));
          output = args.get(i + 1).getBody();
          args.remove(i + 1);
          args.remove(i);
          i--; // back up to process rest of line correctly
        }
      }
    }

    Environment execEnv;
    if (redirect) {
      // duplicate current Environment with capture
      execEnv = new Environment.Builder(env).withOutputCapture().build();
    } else {
      execEnv = env;
    }

    Result result = command.execute(this.shell, execEnv, args);

    if (redirect) {
      try {
        File file = new Path(shell.getFileSystem(), output).getOrCreateFile();
        if (append) {
          file.appendToContent(execEnv.getCapturedOutput());
        } else {
          file.setContent(execEnv.getCapturedOutput());
        }
      } catch (PathException e) {
        env.err.println("Error: " + e);
        return new Error();
      }
      return Command.okayResult;
    } else {
      return result;
    }
  }



  /**
   * Add a specific Command to the table of interpreter.commands available to
   * the user. Each Command provides its own name, providing a means for the
   * interpreter to dispatch user input to the specific execute method for that
   * command.
   * 
   * @param c The Command to add.
   */
  public void addCommand(Command c) {
    commandMap.put(c.getName(), c);
  }

  /**
   * Look up a specific command by the name contained in a Token. Command
   * objects are required to provide a name() method and are thus
   * self-identifying.
   * 
   * @param t the token containing a word
   * @return the specific Command associated with that word.
   */
  public Command getCommandByToken(Token t) {
    return commandMap.get(t.getBody());
  }
}
