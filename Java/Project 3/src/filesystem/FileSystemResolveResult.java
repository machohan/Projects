package filesystem;



/**
 * The result of a path resolution operation within the filesystem. Although
 * other classes outside the filesystem may never call the resolver directly,
 * resolve results may be passed up from the filesystem after a call to, for
 * example, filesystem.setCwd().
 * 
 * @author mwb
 *
 */
public class FileSystemResolveResult extends FileSystemResult {
  public final Node node;

  public FileSystemResolveResult(FSResultType status, Node node) {
    super(status);
    this.node = node;
  }
}
