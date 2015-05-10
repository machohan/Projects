/**
 * 
 */
package filesystem;

/**
 * A Path is a wrapper around the filesystem's knowledge about the path, a
 * textual representation of an absolute or relative path, that is, from either
 * / (root) or the current working directory (cwd).
 *
 */
public class Path {
  private String path;
  FileSystem fs;

  /**
   * Create a new Path object.
   * 
   * @param fs the filesystem for which the path is relevant.
   * @param path the textual representation of the path.
   */
  public Path(FileSystem fs, String path) {
    this.fs = fs;
    this.path = path;
  }

  /**
   * Check if this Path points to a Node (a File or Directory) in the
   * filesystem.
   * 
   * @return true if the Path resolves to a Node, false otherwise.
   */
  public boolean exists() {
    FileSystemResolveResult resolveResult = fs.resolvePath(path);
    return resolveResult.status == FSResultType.Success;
  }

  /**
   * Attempt to resolve this Path to a Node. Upon success, returns that Node.
   * Upon failure, throws an exception.
   * 
   * @return the Node found.
   * @throws PathException upon failure to resolve the path if e.g. there's
   *         nothing there or the path is malformed.
   */
  public Node getNode() throws PathException {
    FileSystemResolveResult resolveResult = fs.resolvePath(path);
    if (resolveResult.status != FSResultType.Success) {
      throw new PathException(this.path, resolveResult.status);
    } else {
      return resolveResult.node;
    }
  }

  /**
   * Attempt to create a File at this Path location
   * 
   * @throws PathException if file already exists or other errorj
   */
  public void createFile() throws PathException {
    FileSystemResult result = fs.createFile(path);
    if (result.status != FSResultType.Success) {
      throw new PathException(this.path, result.status);
    }
  }

  /**
   * Attempt to locate a File at this Path location
   * 
   * @return the File
   * @throws PathException if the object at the Path location is not a File.
   */
  public File getFile() throws PathException {
    Node thisNode = this.getNode();
    if (thisNode.isFile()) {
      return (File) thisNode;
    } else {
      throw new PathException(this.path, FSResultType.NotAFile);
    }
  }

  /**
   * Attempt to locate a Directory at this Path location
   * 
   * @return the Directory
   * @throws PathException if the object at the Path location is not a
   *         Directory.
   */
  public Directory getDirectory() throws PathException {
    Node thisNode = this.getNode();
    if (thisNode.isDirectory()) {
      return (Directory) thisNode;
    } else {
      throw new PathException(this.path, FSResultType.NotADirectory);
    }
  }

  /**
   * Return a new Path object with the specified suffix appended to the path; if
   * needed, the path separator character will be inserted between the old path
   * and the suffix.
   * 
   * @param suffix the suffix to append (a file or directory name)
   * @return a new Path with the suffix appended.
   */
  public Path withSuffix(String suffix) {
    if (path.isEmpty()) {
      return new Path(this.fs, suffix);
    }
    if (path.endsWith("/")) {
      return new Path(this.fs, path + suffix);
    } else {
      return new Path(this.fs, path + "/" + suffix);
    }
  }

  /**
   * Attempt to locate or create a File at this Path location
   * 
   * @return the File (existing or newly created)
   * @throws PathException if the object at the Path location is not a File, or
   *         if we can't create the file for some reason.
   */
  public File getOrCreateFile() throws PathException {
    if (this.exists()) {
      Node thisNode = this.getNode();
      if (thisNode.isFile()) {
        return (File) thisNode;
      } else {
        throw new PathException(this.path, FSResultType.NotAFile);
      }
    } else {
      this.createFile();
      return (File) this.getNode();
    }
  }

  /**
   * Delete the filesystem object (File or Directory) at the specified Path.
   * Throws an exception if the filesystem encounters an error attempting to
   * delete (e.g. the object does not exist, or the object is a directory, and
   * that directory is not empty).
   * 
   * To avoid throwing an exception, consider using exists() and getNode(). If
   * the object is a Directory, you will also want to use Directory.getEntries()
   * to make sure the directory is empty.
   * 
   * @throws PathException if e.g. path points at a directory and directory is
   *         not empty.
   */
  public void delete() throws PathException {
    FileSystemResult result = fs.delete(path);
    if (result.status != FSResultType.Success) {
      throw (new PathException(path, result.status));
    }
  }

  /**
   * Check to see if the object represented by the Path is a file.
   * 
   * @return true if it's a file
   * @throws PathException if there's some error, e.g. there is no object
   *         represented by this path.
   */
  public boolean isFile() throws PathException {
    FileSystemResolveResult result = fs.resolvePath(path);
    if (result.status != FSResultType.Success) {
      throw (new PathException(path, result.status));
    } else {
      return result.node.isFile();
    }
  }

  /**
   * Check to see if the object represented by the Path is a Directory.
   * 
   * @return true if it's a Directory
   * @throws PathException if there's some error, e.g. there is no object
   *         represented by this path.
   */
  public boolean isDirectory() throws PathException {
    FileSystemResolveResult result = fs.resolvePath(path);
    if (result.status != FSResultType.Success) {
      throw (new PathException(path, result.status));
    } else {
      return result.node.isDirectory();
    }
  }


  /**
   * get the terminal or "name" part of a path. This is purely a textual
   * operation. Example: baseName(/usr/local/bin/make.sh) is make.sh
   * 
   * @return the basename of the given path.
   */
  public String baseName() {
    return FileSystem.baseName(path);
  }

  /**
   * get the terminal or "name" part of a path. This is purely a textual
   * operation. Example: baseName(/usr/local/bin/make.sh) is make.sh
   * 
   * This static version of baseName() does not require a Path instance.
   *
   * @param path the string upon which to operate
   * @return the basename of the path provided.
   */
  public static String baseName(String path) {
    return FileSystem.baseName(path);
  }

  /**
   * Get the directory portion of the path. This is purely a textual operation.
   * Example: dirName(/usr/local/bin) is /usr/local
   * 
   * @return the dirname of the given path.
   */
  public String dirName() {
    return FileSystem.dirName(path);
  }

  /**
   * get the directory portion of the path. This is purely a texual operation.
   * Example: dirName(/usr/local/bin) is /usr/local
   * 
   * This static version of dirname() does not require a Path instance.
   * 
   * @param path
   * @return
   */
  public static String dirName(String path) {
    return FileSystem.dirName(path);
  }

  /**
   * Provide a textual representation of this path.
   */
  public String toString() {
    return path;
  }


  /**
   * Get the resolve status for this Path; more detailed than {@code exists}.
   * Does not throw an exception.
   * 
   * @return the resolve status; FSResultType.Success if there's a Node (a File
   *         or Directory) specified by the path, or some other FSResultType
   *         (E.g. FSResultType.NotFound) otherwise.
   */
  public FSResultType getResolveStatus() {
    FileSystemResolveResult resolveResult = fs.resolvePath(path);
    return resolveResult.status;
  }



}
