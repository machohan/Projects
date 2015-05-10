package filesystem;


/**
 * Result object for filesystem methods that return string data but may also
 * fail. Always check the status field before using the data from the content
 * field.
 */
public class FileSystemStringResult extends FileSystemResult {
  public final String content;

  public FileSystemStringResult(FSResultType status, String content) {
    super(status);
    this.content = content;
  }
};
