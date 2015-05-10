package filesystem;


/**
 * The result of a request to the filesystem. At minimum, a FileSystemResult
 * carries a status field, which should be consulted after any use of a
 * filesystem method which returns a FileSystemResult or any of the classes that
 * extend FileSystemResult.
 * 
 * 
 *
 */

public class FileSystemResult {
  public final FSResultType status;

  public FileSystemResult(FSResultType status) {
    this.status = status;
  }

}
