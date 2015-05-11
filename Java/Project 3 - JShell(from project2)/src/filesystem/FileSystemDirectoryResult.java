/**
 * 
 */
package filesystem;

import java.util.ArrayList;

/**
 * The {@code FileSystemDirectoryResult} is returned by any {@code filesystem}
 * method that needs to return the contents of a Directory.
 *
 */
public class FileSystemDirectoryResult extends FileSystemResult {
  public final ArrayList<String> entries;

  /**
   * @param status the status of the operation
   * @param entries directory entries
   */
  public FileSystemDirectoryResult(FSResultType status,
      ArrayList<String> entries) {
    super(status);
    this.entries = entries;
  }

}
