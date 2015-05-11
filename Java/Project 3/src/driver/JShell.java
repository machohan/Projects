// **********************************************************
// Please note this project was done in collaboration with three more students.
// *********************************************************
package driver;

import interpreter.commands.*;
import interpreter.core.Interpreter;
import interpreter.core.Token;
import interpreter.result.Result;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import filesystem.FileSystem;


/**
 * The JShell. A command-line shell with simulated file system. Finely
 * cultivated cuisine for the delectations of the CSC 207 grading team, Fall
 * 2014.
 * 
 * In this class, JShell, we find a collection of top-level handles to data that
 * persist during the lifetime of a session:
 * 
 * A filesystem. An Interpreter. A pair of streams, one in and one out. Our
 * primal state: is the JShell running, or not (about to exit). And a directory
 * stack.
 * 
 * Other pieces of state, including the current working directory (CWD), and the
 * specific choice of interpreter.commands implemented by this shell, are
 * contained in the object hierarchy above.
 * 
 */
public class JShell {

  private FileSystem fs;
  private Interpreter inter;
  private InputStream in;
  private PrintStream out;
  private boolean running;
  private Scanner inScanner;
  private Stack<String> dirStack;

  /**
   * Constructor.
   * 
   * @param in Where we should take input, either interactively or from a stream
   *        or file.
   * @param out Where we print results and errors.
   */
  public JShell(InputStream in, PrintStream out) {
    this.in = in;
    this.out = out;
    this.inScanner = new Scanner(this.in);
    running = true;
    this.fs = new FileSystem();
    this.inter = makeMyInterpreter(this);
    this.dirStack = new Stack<String>();
  }

  /**
   * Get a reference to the current interpreter.
   * 
   * @return The Interpreter instance.
   */
  public Interpreter getInterpreter() {
    return this.inter;
  }

  /**
   * Get a reference to the current input stream.
   * 
   * @return the InputStream instance.
   */
  public InputStream getInputStream() {
    return in;
  }

  /**
   * Get a reference to the current print stream.
   * 
   * @return the PrintStream instance.
   */
  public PrintStream getPrintStream() {
    return out;
  }

  /**
   * Get a reference to the current filesystem.
   * 
   * @return the filesystem instance.
   */
  public FileSystem getFileSystem() {
    return fs;
  }

  /**
   * Get a reference to the directory stack.
   * 
   * @return the Stack<String> instance.
   */
  public Stack<String> getDirectoryStack() {
    return dirStack;
  }

  /**
   * Our run state. If true, the JShell will continue to run. If false, the
   * JShell will exit at the completion of the current command (and before the
   * next prompt()).
   * 
   * @return
   */
  public boolean isRunning() {
    return this.running;
  }

  /**
   * Prompt the user. A utility function.
   */
  public void prompt() {
    out.print(fs.getCwd() + "> ");
  }

  /**
   * Instruct the JShell to exit. To be invoked by any command that detects a
   * reason for the JShell to exit before the next prompt().
   */
  public void exit() {
    running = false;
  }

  /**
   * Convenience function to get a line of user input.
   * 
   * @return what the user typed (or did not type) before pressing return. (If
   *         taking our interpreter.commands from a file, the next line of input
   *         up to NEWLINE.) Returns null if end of file.
   * 
   */
  public String getLine() {
    try {
      return new String(inScanner.nextLine());
    } catch (java.util.NoSuchElementException e) {
      return null;
    }
  }

  /**
   * Build the interpreter.
   * 
   * @param shell Which shell object.
   * @return the new interpreter.
   */
  public static Interpreter makeMyInterpreter(JShell shell) {
    Interpreter interp = new Interpreter(shell);
    interp.addCommand(new CommandEcho());
    interp.addCommand(new CommandGet());
    interp.addCommand(new CommandExit());
    interp.addCommand(new CommandMkdir());
    interp.addCommand(new CommandMan());
    interp.addCommand(new CommandLs());
    interp.addCommand(new CommandCd());
    interp.addCommand(new CommandPwd());
    interp.addCommand(new CommandMv());
    interp.addCommand(new CommandCat());
    interp.addCommand(new CommandCp());
    interp.addCommand(new CommandPushd());
    interp.addCommand(new CommandGrep());
    interp.addCommand(new CommandPopd());
    interp.addCommand(new CommandRm());

    return interp;
  }

  /**
   * The main JShell loop and driver.
   * 
   * @param args command-line arguments to main() are ignored.
   */
  public static void main(String[] args) {
    JShell shell = new JShell(System.in, System.out);
    String line;

    while (shell.isRunning()) {
      shell.prompt();
      line = shell.getLine();
      if (line == null) {
        shell.inScanner = new Scanner(shell.in);
        continue;
      } else {
        // we collect the result for now.
        Result result = shell.inter.doLine(line);
      }
    }
  }

}
