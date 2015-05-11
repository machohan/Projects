package filesystem;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * The FileSystem builds and maintains the intrinsic tree structure of
 * a collection of Files and Directories. It keeps references pointing 
 * to the root and the current working directory. Operations include:
 * making a directory, string operations on pathnames, creating a file,
 * moving a file, reading a file (subsumed by File.getContent), writing
 * a file (subsumed by File.setContent), setting and getting the current
 * working directory, and resolving a path.
 *
 * NOTE: Many of these functions are more conveniently accessed via the
 * Path class, which provides a friendly layer around a FileSystem object.
 *
 */
public class FileSystem {

  private Directory root;
  private Directory cwd;

  /**
   * convenience type to distinguish files and directories
   *
   */
  private enum NodeType {
    FileNode, DirectoryNode
  };


  /**
   * Get a new filesystem.
   * 
   * This object presents a complete interface for the creation, naming,
   * modifying, deletion of interpreter.commands.
   */
  public FileSystem() {
    root = new Directory(null);
    cwd = root;
  }

  /**
   * mkdir Makes a directory at the end of the specified path.
   * 
   * @param path a relative or absolute path. The last element (or, possibly,
   *        the only element) is the name of the new directory.
   * 
   *        Relative paths (those that do not start with a '/') will be resolved
   *        with respect to the CWD (current working directory).
   * 
   * @return If FileSystemResult.status == FSResult.Success, the operation
   *         succeeded. Otherwise, check the specific result code to discover
   *         what went wrong.
   */

  public FileSystemResult mkdir(String path) {
    return mknod(path, NodeType.DirectoryNode);
  }

  /**
   * Create a file at the specified location.
   * 
   * Relative paths (those that do not start with a '/') will be resolved with
   * respect to the CWD (current working directory).
   * 
   * @param path a relative path or absolute path. The last element (or,
   *        possibly, the only element) is the name of the new file.
   * @return If FileSystemResult.status == FSResult.Success, the operation
   *         succeeded. Otherwise, check the specific result code to discover
   *         what went wrong.
   */
  public FileSystemResult createFile(String path) {
    return mknod(path, NodeType.FileNode);
  }



  /**
   * Write (replace) the contents of a file. Will fail if the file does not
   * already exist, or if the thing pointed to by path exists but is not a file.
   * Relative paths (those that do not start with a '/') will be resolved with
   * respect to the CWD (current working directory).
   * 
   * @param path The location to write to
   * @param data The data to write
   * @return status is FSReturnType.Success on success; failure otherwise.
   */
  public FileSystemResult writeFile(String path, String data) {
    FileSystemResolveResult resolveResult = resolvePath(path);
    if (resolveResult.status != FSResultType.Success) {
      return new FileSystemResult(resolveResult.status);
    }
    if (resolveResult.node.isFile()) {
      ((File) resolveResult.node).setContent(data);
      return new FileSystemResult(FSResultType.Success);
    } else {
      return new FileSystemResult(FSResultType.NotAFile);
    }
  }

  /**
   * Read the contents of the file specified by {@code path}
   * 
   * @param path The absolute or relative path of the file to retrieve
   * @return status is FSReturnType.Success on success, and content will contain
   *         the contents of the file retrieved. If status is some other file,
   *         an error has occurred (see status for details) and content is not
   *         guaranteed to be valid.
   */

  public FileSystemStringResult readFile(String path) {
    FileSystemResolveResult resolveResult = resolvePath(path);
    if (resolveResult.status != FSResultType.Success) {
      return new FileSystemStringResult(resolveResult.status, null);
    }
    if (resolveResult.node.isFile()) {
      String fileContent = ((File) resolveResult.node).getContent();
      return new FileSystemStringResult(FSResultType.Success, fileContent);
    } else {
      return new FileSystemStringResult(FSResultType.NotAFile, null);
    }
  }


  /**
   * Return the current working directory (cwd) as a String containing the
   * absolute path.
   * 
   * @return a string representation of the current working directory
   */
  public String getCwd() {
    return getCanonicalPath(cwd);
  }

  /**
   * Given a non-empty string, returns the component of the string after the
   * last '/' in the string. If given "/", will return "/". If given a path with
   * any number of terminal slashes, will return the last named component in the
   * path, eg "/usr/" and "usr////" and "usr" all have a basename of "usr"
   * 
   * @param path a string representation of the path
   * @return the basename, or null if path is null.
   */

  public static String baseName(String path) {
    if (path == null) {
      return null;
    } else if (path.isEmpty()) {
      return "";
    }

    int end = baseNameEnd(path);

    // contents of a single-character path are always correct,
    // even if just a single '/' left over from discard
    if (end == 1) {
      return path.substring(0, end);
    }

    return (path.substring(baseNameStart(path, end), end));
  }

  /**
   * Helper function for basename.
   * 
   * @param path
   * @return
   */
  private static int baseNameEnd(String path) {
    assert (path != null);
    int end = path.length();

    // discard all ending '/' characters, ensuring remainder is not empty
    while (end > 1 && path.charAt(end - 1) == '/') {
      end--;
    }
    return end;
  }

  /**
   * Helper function for basename
   * 
   * @param path
   * @param end
   * @return
   */
  private static int baseNameStart(String path, int end) {
    // find beginning: search until top of string, or a '/';
    int start = end - 1;
    while (start > 0 && path.charAt(start - 1) != '/') {
      start--;
    }
    return start;
  }

  /**
   * Get the "dirname" of the path -- implementing the semantics of dirname as
   * availble in bash (which see)
   * 
   * e.g. dirname(/usr/local) returns "/usr"; dirname(/usr) returns "/";
   * dirname("") returns "."; and so on.
   * 
   * @param path the path of interest
   * @return the dirname
   */
  public static String dirName(String path) {
    // handle three special special cases
    if (path.isEmpty()) {
      return ".";
    }
    if (path.equals(".")) {
      return path;
    } else if (path.equals("..")) {
      return ".";
    }

    // start looking for dirName at index preceding baseName
    int end = baseNameStart(path, baseNameEnd(path));

    // also special: if end is 0, dirName will be empty, meaning cwd, or "."
    if (end == 0) {
      return ".";
    }

    // skip any '/' characters, but leave at least one at beginning.
    while (end > 1 && path.charAt(end - 1) == '/') {
      end--;
    }

    return (path.substring(0, end));

  }

  /**
   * Produce the canonical path to a given directory node. Walks up through
   * parent references until it finds a loop (the parent of the root directory
   * is root, after all).
   * 
   * @param d the terminal directory in the path
   * @return a string expressing the path.
   */
  protected String getCanonicalPath(Directory d) {
    assert (d != null);
    assert (d.getParent() != null);

    StringBuilder sb = new StringBuilder();
    while (d != d.getParent()) {
      sb.insert(0, d.getParent().getNameByNode(d));
      sb.insert(0, "/");
      d = d.getParent();
    }

    if (sb.length() == 0) {
      sb.append("/");
    }
    return sb.toString();
  }

  /**
   * Change the current working directory (CWD). If the path specified does not
   * begin with a slash, then it is treated as a relative path and resolved with
   * respect to the current working directory (i.e. the CWD before the method
   * invocation.)
   * 
   * @param path A string containing the desired new working directory.
   * @return If FileSystemResult.status == FSResult.Success, the operation
   *         succeeded. Otherwise, check the specific result code to discover
   *         what went wrong.
   */

  public FileSystemResult setCwd(String path) {
    // reject empty path strings.
    if (path.isEmpty()) {
      return new FileSystemResult(FSResultType.EmptyString);
    }

    // make sure that path resolves successfully; if not, return error
    FileSystemResolveResult resolveResult = resolvePath(path);
    if (resolveResult.status != FSResultType.Success) {
      return resolveResult;
    }

    // make sure path resolved to a directory
    if (resolveResult.node.isDirectory()) {
      // yes
      cwd = (Directory) resolveResult.node;
      return new FileSystemResult(FSResultType.Success);
    } else {
      // not a directory
      return new FileSystemResult(FSResultType.NotADirectory);
    }
  }

  /**
   * Get the names in the specified directory.
   * 
   * @param path the path to the directory
   * @return Upon success (status is {@code FSResult.Success}), the entries
   *         field will contain an ArrayList of String of all the names in the
   *         directory.
   */

  public FileSystemDirectoryResult getNamesInDirectory(String path) {
    // reject empty path strings.
    if (path.isEmpty()) {
      return new FileSystemDirectoryResult(FSResultType.EmptyString, null);
    }

    // make sure that path resolves successfully; if not, return error
    FileSystemResolveResult resolveResult = resolvePath(path);
    if (resolveResult.status != FSResultType.Success) {
      return new FileSystemDirectoryResult(resolveResult.status, null);
    }

    // make sure path resolved to a directory
    if (resolveResult.node.isDirectory()) {
      // yes, pull out the names of the entries
      Set<String> entrySet =
          ((Directory) resolveResult.node).getEntries().keySet();
      ArrayList<String> entries = new ArrayList<String>(entrySet);

      return new FileSystemDirectoryResult(FSResultType.Success, entries);
    } else {
      // not a directory
      return new FileSystemDirectoryResult(FSResultType.NotADirectory, null);
    }
  }

  /**
   * Remove a file system object (a file or a directory) from the filesystem. If
   * the object is a directory, it must be empty or the operation will fail and
   * an error will be returned.
   * 
   * @param path A path, relative or absolute, to the object to remove
   * @return FSResultType.Success upon success; explanatory error otherwise.
   */
  public FileSystemResult delete(String path) {
    if (path.isEmpty()) {
      return new FileSystemResult(FSResultType.EmptyString);
    }
    FileSystemResolveResult resolveResult = resolvePath(dirName(path));
    if (resolveResult.status != FSResultType.Success) {
      return new FileSystemResult(FSResultType.NotFound);
    }
    Directory dir = (Directory) resolveResult.node;
    return new FileSystemResult(dir.deleteNodeByName(baseName(path)));
  }

  /**
   * Move a file system object (a file or directory) from one place to another.
   * oldPath must be a file or directory. newPath can be an existing directory,
   * or the path to a new file system object.
   * 
   * This attempts to follow the semantics of rename(2) under Unix and
   * derivative systems.
   * 
   * @param src the path to the source Node
   * @param dest the path to the destination Node
   * @return the result (success or reason for failure) of the move
   */
  public FileSystemResult move(String src, String dest) {
    // This method is long. Like the Ouroboros, it threatens to
    // swallow its own tail. But it works.

    // source
    ArrayList<String> srcTokens = tokenizePath(src);
    if (srcTokens.size() == 0) {
      return new FileSystemResult(FSResultType.EmptyString);
    }

    final String srcBaseName = srcTokens.get(srcTokens.size() - 1);

    FileSystemResolveResult resolveResultOld = resolvePath(srcTokens);
    if (resolveResultOld.status != FSResultType.Success) {
      return new FileSystemResult(resolveResultOld.status);
    }

    final Node srcNode = resolveResultOld.node;
    if (srcNode == root) {
      return new FileSystemResult(FSResultType.IllegalOperation);
    }

    // note that ArrayList<?>.sublist(a,b) includes a but excludes b!

    List<String> srcParentTokens = srcTokens.subList(0, srcTokens.size() - 1);
    FileSystemResolveResult resolveResult = resolvePath(srcParentTokens);
    assert (resolveResult.status == FSResultType.Success);
    Node srcParentNode = resolveResult.node;
    assert (srcParentNode.isDirectory());
    final Directory srcParentDirectory = (Directory) srcParentNode;

    final Node destNode;
    // destination
    FileSystemResolveResult destDirectoryResolveResult = resolvePath(dest);
    if (destDirectoryResolveResult.status == FSResultType.Success) {
      // == dest exists
      destNode = destDirectoryResolveResult.node;
      // is dest a directory?
      if (destNode.isDirectory()) {
        // src exists, dest exists, dest is a directory.
        // We just need to unlink src, and link it into dest.
        // unless it violates the tree property
        Directory destDirectory = (Directory) destNode;
        if (isSubPathOrEqual(srcNode, destDirectory)) {
          return new FileSystemResult(FSResultType.IllegalOperation);
        }
        FSResultType unlinkResult =
            srcParentDirectory.unlinkNodeByName(srcBaseName);
        assert (unlinkResult == FSResultType.Success);
        if (srcNode.isDirectory()) {
          ((Directory) srcNode).setParent(destDirectory);
        }
        destDirectory.addNode(srcNode, srcBaseName);
        return new FileSystemResult(FSResultType.Success);
      } else {
        // can only move things into directories.
        return new FileSystemResult(FSResultType.NotADirectory);
      }
    } else {
      // == dest does not exist, so we're essentially renaming
      ArrayList<String> newPathTokens = tokenizePath(dest);
      if (newPathTokens.size() == 0) {
        return new FileSystemResult(FSResultType.EmptyString);
      }
      String destBaseName = newPathTokens.get(newPathTokens.size() - 1);
      destDirectoryResolveResult =
          resolvePath(newPathTokens.subList(0, newPathTokens.size() - 1));
      if (destDirectoryResolveResult.status != FSResultType.Success) {
        return new FileSystemResult(destDirectoryResolveResult.status);
      }
      final Directory destDirectory =
          (Directory) destDirectoryResolveResult.node;
      // src exists, dest does not, but directory containing
      // dest does, so we need to (1) unlink src and (2) link it
      // into dest, but with its new given name.
      if (isSubPathOrEqual(srcNode, destDirectory)) {
        return new FileSystemResult(FSResultType.IllegalOperation);
      }
      FSResultType unlinkResult =
          srcParentDirectory.unlinkNodeByName(srcBaseName);
      assert (unlinkResult == FSResultType.Success);
      if (srcNode.isDirectory()) {
        ((Directory) srcNode).setParent(destDirectory);
      }
      FSResultType addResult = destDirectory.addNode(srcNode, destBaseName);
      return new FileSystemResult(addResult);
    }
  }


  /**
   * Validate strings for the following requirement: Only the characters '.',
   * 0-9, A-Z, a-z, _, and - are allowed in any component of a path (aside from
   * .,.., and /, all of which have special meaning).
   * 
   * @param component The part of the path to check. Path components are the
   *        bits between the slashes.
   * @return true if this component is valid, false otherwise.
   */
  public boolean isPathComponentValid(String component) {
    if (component.matches("[\\.0-9A-Za-z_-]+")) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * Resolve a path that looks like "/usr/local/bin" to a reference to a
   * {@code Node} object, which may be a {@code File} or a {@code Directory}.
   * 
   * {@code resolvePath} starts from the beginning of the string, resolving the
   * elements (names) that appear between "/" characters. The resolver attempts
   * to find the current element in the directory named by the previous element.
   * 
   * A path that starts with a "/" is treated as an absolute path and is path
   * resolution starts from the root of the filesystem.
   * 
   * A path that starts with "." is treated as a relative path, and resolution
   * starts from the current working directory (CWD). Subsequent appearances of
   * "." are ignored.
   * 
   * A path that starts with ".." is treated as a relative path, and resolution
   * starts from the parent of the current working directory. Subsequent
   * appearances of ".." will cause the resolver to "back up" to the parent
   * directory of the previous element.
   * 
   * 
   * @param path the path to resolve
   * @return FSResultType.Success, with the node in question, or some other
   *         value of FSResultType, explaining the failure
   */

  public FileSystemResolveResult resolvePath(String path) {
    assert (null != path);
    if (path.isEmpty()) {
      return new FileSystemResolveResult(FSResultType.EmptyString, null);
    }
    ArrayList<String> pathComponents = tokenizePath(path);
    return resolvePath(pathComponents);
  }



  /**
   * Check if a given node, when expressed by its canonical path, is a subpath
   * (or equal to) a given directory node, when expressed by its canonical path.
   * E.g. "/usr/local" is a sub-path of "/usr/local/bin" Useful in catching
   * "mv /usr/local /usr/local/bin"
   * 
   * @param node The node in question
   * @param dir The directory in question
   * @return True if the node is a sub-path, or equal to, the directory
   */
  public boolean isSubPathOrEqual(Node node, Directory dir) {
    assert (node != null);
    assert (dir != null);

    if (node.isDirectory() == false) {
      return false;
    }
    if (node == dir) {
      return true;
    }

    String nodePath = getCanonicalPath((Directory) node) + "/";
    String dirPath = getCanonicalPath(dir) + "/";

    if (dirPath.startsWith(nodePath)) {
      return true;
    } else {
      return false;
    }

  }

  /**
   * Encapsulates common functionality for mkdir and createFile.
   * 
   * @param path The path to the object to create
   * @param type The type of object to create
   * @return FSResultType.Success if it works; other return values upon failure.
   */

  private FileSystemResult mknod(String path, NodeType type) {
    if (path.isEmpty()) {
      return new FileSystemResult(FSResultType.EmptyString);
    }
    // tokenize the path
    ArrayList<String> pathElements = tokenizePath(path);
    assert (!pathElements.isEmpty());
    // the last element of the path is the name of our new node
    String newNodeName = pathElements.get(pathElements.size() - 1);
    pathElements.remove(pathElements.size() - 1);
    // validate new node name
    if (!isPathComponentValid(newNodeName)) {
      return new FileSystemResult(FSResultType.BadCharacterInPath);
    }

    // now to find where to put our new node.
    Directory parentDir;
    if (pathElements.size() == 0) {
      // the new node name was the only element, which means we have a
      // relative path (relative to cwd).
      parentDir = cwd;
    } else {
      // resolve the rest of the path
      FileSystemResolveResult resolveResult = resolvePath(pathElements);
      if (!(FSResultType.Success == resolveResult.status)) {
        // resolving got an error, and we'll pass that up to whoever called us
        return new FileSystemResult(resolveResult.status);
      }
      // check that destination for new node is a directory
      if (!resolveResult.node.isDirectory()) {
        return new FileSystemResult(FSResultType.NotADirectory);
      }
      parentDir = (Directory) resolveResult.node;
    }
    // now make the appropriate sort of node, passing errors to invoker
    if (type == NodeType.DirectoryNode) {
      return new FileSystemResult(parentDir.addNode(new Directory(parentDir),
          newNodeName));
    } else {
      return new FileSystemResult(parentDir.addNode(new File(), newNodeName));
    }
  }



  /**
   * The private version of resolvePath does its work from a List<String>
   * instead of a string. Almost identical to resolvePath(String) and is in fact
   * called by resolvePath(String). One difference: if called with an empty list
   * for pathComponents, returns the current working directory. 
   * 
   * @param pathComponents
   * @return
   */
  private FileSystemResolveResult resolvePath(List<String> pathComponents) {
    Iterator<String> pathIter = pathComponents.iterator();
    assert (pathIter.hasNext());
    if (pathComponents.size() == 0) {
      return new FileSystemResolveResult(FSResultType.Success, cwd);
    }
    if (pathComponents.get(0).isEmpty()) {
      // empty element at the beginning of pathComponents means path starts
      // with a slash (/) and is therefore absolute path.
      pathIter.next();
      return resolvePathWorker(root, pathIter);
    } else {
      // note: this will include paths that start with "./"
      return resolvePathWorker(cwd, pathIter);
    }
  }

  /**
   * Worker function that does the actual path resolution. Works recursively.
   * 
   * @param dir The directory from which to start
   * @param pathIter an {@code Iterator} pointing to the next n path elements to
   *        resolve. Each path element is a {@code String} corresponding to a
   *        {@code File} or {@code Directory} in the sub-tree referenced by
   *        {@code dir}.
   * @return
   */
  private FileSystemResolveResult resolvePathWorker(Directory dir,
      Iterator<String> pathIter) {
    assert (null != dir);
    assert (null != pathIter);

    String thisComponent;

    // handle "." elements as well as a paths that start with "////"
    do {
      if (!pathIter.hasNext()) {
        return new FileSystemResolveResult(FSResultType.Success, dir);
      }
      thisComponent = pathIter.next();
    } while (thisComponent.equals("."));

    if (!dir.exists(thisComponent)) {
      // this is the NotFound case
      return new FileSystemResolveResult(FSResultType.NotFound, null);
    }

    // this component exists
    Node thisNode = dir.getNodeByName(thisComponent);
    if (!pathIter.hasNext()) {
      // we've reached the end of the path requested and we've resolved it
      return new FileSystemResolveResult(FSResultType.Success, thisNode);
    } else {
      // we still have further to go
      if (thisNode.isDirectory()) {
        return resolvePathWorker((Directory) thisNode, pathIter);
      } else {
        // component exists, but not a directory so we can't go further.
        return new FileSystemResolveResult(FSResultType.NotADirectory, null);
      }
    }
  }

  /**
   * break a path up into elements, each element delimited by one or more /
   * characters.
   * 
   * @param path the path
   * @return the elements between the "/" characters. Note that split will put
   *         an empty element at the beginning of the ArrayList if the path
   *         starts with a "/" (which we will depend upon) but that it does NOT
   *         do so at the end of the string.
   */
  private static ArrayList<String> tokenizePath(String path) {
    assert (null != path);
    assert (!path.isEmpty());
    String splitPath[] = path.split("/+");
    ArrayList<String> result = new ArrayList<String>();
    for (String elem : splitPath) {
      result.add(elem);
    }
    // handle the odd behavior of split where "/" and "//" give no elements,
    // while "" gives one.
    if (result.isEmpty()) {
      result.add("");
    }
    return result;
  }


}
