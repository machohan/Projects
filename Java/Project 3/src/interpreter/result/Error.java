/**
 * 
 */
package interpreter.result;

/**
 * Error indicates the unsuccessful execution of a command in JShell
 * 
 * @author mwb
 *
 */
public class Error extends Result {
  /**
   * Indicate the unsuccessful execution of a Command in JShell -- E.g. file not
   * found, wrong number of parameters, and so on.
   * 
   * @param message details about what went wrong.
   */

  public Error() {
    super(true);

  }

}
