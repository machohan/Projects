/**
 * 
 */
package interpreter.result;

/**
 * Success is the successful result of a Command in JShell.
 *
 */

public class Success extends Result {
  /**
   * Indicate successful execution of the command.
   * 
   * @param any information returned by the successful execution of the command.
   */
  public Success() {
    super(false);
  }

}
