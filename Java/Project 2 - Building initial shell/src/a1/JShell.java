// **********************************************************
// This project was done in collaboration with another student.
// *********************************************************
package a1;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * 
 * A top level class for interaction with the user. It provides a home for
 * main() It provides these services: - take user input - send user input to 
 * the parser - send user input for validation - execute valid user input
 * (unimplemented) - keep track of filesystem & web access (unimplemented)
 *
 */
public class JShell {

  /**
   * Top level entry for JShell
   * 
   * @param args command-line arguments are presently ignored.
   */
  public static void main(String[] args) {
    JShell js = new JShell(System.in, System.out);
    JShell.Interpreter interp = new JShell.Interpreter();

    // while we're still running
    while (js.getRunning()) {
      // give the user a prompt; take what they type; do it.

      js.prompt();
      String line = js.getLine();
      if (null == line) {
        // we have hit an unexpected end of input -- just stop
        js.exit(); 
      } else {
        interp.doLine(js, line);
      }
    }
  }

  // input stream (e.g. terminal)
  private InputStream inStream;
  // a buffered wrapper around input stream
  private Scanner inScanner;
  // output to user (e.g. terminal)
  private PrintStream outStream;
  // if we have exit()ed, this should be false
  private boolean running;

  /**
   * Initialize an instance of the shell.
   * 
   * @param inStreamParam input stream from user, like a terminal
   * @param outStreamParam output stream to user, like a terminal
   */

  public JShell(InputStream inStreamParam, PrintStream outStreamParam) {
    inStream = inStreamParam;
    outStream = outStreamParam;
    inScanner = new Scanner(inStream);
    running = true;
  }

  /**
   * Give the stream where we communicate with the user-- command output, error
   * message, etc.
   * 
   * @return The PrintStream to the user
   */
  public PrintStream getOutStream() {
    return outStream;
  }

  /**
   * Display a message to the user, prompting them to enter a new command. Also
   * provides context (current directory).
   */
  public void prompt() {
    // TODO: make a real prompt reflecting current working directory
    outStream.print("/#>");
  }

  /**
   * Convenience function to get a line of user input.
   * 
   * @return what the user typed (or did not type) before pressing return. (If
   *         taking our commands from a file, the next line of input up to
   *         NEWLINE.)  Returns null if end of file.
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
   * Check if the JShell is still in the "running" state. (If not running, we
   * are about to exit.)
   * 
   * @return true if still running.
   */
  public boolean getRunning() {
    return running;
  }

  /**
   * Request that this JShell stop running.
   */
  public void exit() {
    running = false;
  }

  static public class Interpreter {

    /*
     * Interpreter is a helper class. It's currently the front end of JShell's
     * attempts to communicate with the user.
     * 
     * In its current form, it parses an input line into tokens (with the help
     * of Tokenizer) and then prints out those tokens if the line begins with a
     * recognizable command.
     */
    
    private Hashtable<String,Integer> nameToArgs;
    private String commandNamesPattern;
 
    /**
     * Constructor for the Interpreter. Interpreter is stateless,
     * so it just builds some helper data and we're ready to go!
     */
    public Interpreter() {
      nameToArgs = new Hashtable<String,Integer>();
      nameToArgs.put("exit",0);
      nameToArgs.put("mkdir",1);
      nameToArgs.put("cd", 1);
      nameToArgs.put("ls",0);
      nameToArgs.put("pwd", 0);
      nameToArgs.put("mv",2);
      nameToArgs.put("cp",2);
      nameToArgs.put("cat",1);
      nameToArgs.put("get",1);
      nameToArgs.put("echo",3);
      Enumeration<String> names = nameToArgs.keys();
      StringBuilder tempPattern = new StringBuilder();
      tempPattern.append(names.nextElement());
      while(names.hasMoreElements()) {
        tempPattern.append("|");
        tempPattern.append(names.nextElement());
      }
      this.commandNamesPattern=tempPattern.toString();
      
    }

    /**
     * Process a single command-line from the user, executing the user's
     * commands and printing out the result.
     * 
     * @param shell invoking JShell, which links to file system, etc.
     * @param inputLine a command to parse and execute.
     */
    public void doLine(JShell shell, String inputLine) {
      PrintStream out = shell.getOutStream();

      // debugging code
      String[] words = inputLine.trim().split("\\s+");

      // if line is blank, we're done.
      if(words.length==0 || words[0].isEmpty()) {
        return;
      }
      
      int nArgs=words.length-1;
      String commandName=words[0];

      // error on invalid command or number of args
      // does NOT check type of args (if present)
      if(!commandName.matches(commandNamesPattern) 
          || nameToArgs.get(commandName) != nArgs) {
        out.println("Invalid command, please try again");
      } else {
        // command-specific behavior
        if(commandName.matches("exit") && nArgs==0) {
          shell.exit();
        } else {
          // if not the exit command,
          // special case for echo:
          if(commandName.matches("echo") &&
              (words[1].matches(">")
                  ||words[1].matches(">>")
                  || (!words[2].matches(">") && !words[2].matches(">>"))
                  ||words[3].matches(">")
                  ||words[3].matches(">>"))) {
            out.println("Invalid command, please try again");
          } else {
            // print out command and arguments
            out.println(commandName);
            if (nArgs>0) {
              StringBuilder argString = new StringBuilder();
              for(int i=1;i<words.length;i++) {
                argString.append(words[i]);
                if (i!=words.length-1) {
                  argString.append(" ");
                }
              }
              out.print(argString);
            }
            out.println();
          }
        }
      }
    }
  }
}
