package filesystem;

/**
 * Report errors when resolving a Path.
 *
 */
public class PathException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String badPath;
  FSResultType status;

  /**
   * Report the failure of a path operation on a given Path.
   * 
   * @param badPath the textual representation of the path.
   * @param status the problem.
   */
  public PathException(String badPath, FSResultType status) {
    this.badPath = badPath;
    this.status = status;
  }

  public String toString() {
    return this.status.toString() + ": \"" + badPath + "\"";
  }
}
