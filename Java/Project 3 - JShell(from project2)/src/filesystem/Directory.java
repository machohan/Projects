package filesystem;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Used by the filesystem class to encode the basic facts about a directory. A
 * directory associates a name (a String) with a node (a File or Directory). A
 * directory also knows a reference to its parent. If the parent reference
 * points to the Directory itself, this particular Directory is the root of a
 * filesystem.
 * 
 * The association of names to Nodes is efficient (using a HashMap in the
 * current implementation) but the association of Nodes to names is slow
 * (requires a linear search).
 * 
 *
 * @author mwb
 *
 */


public class Directory extends Node {
  private HashMap<String, Node> entries;
  private Directory parent;


  /**
   * Get the entries in this directory
   * 
   * @return a copy of the internal HashMap of String,Node
   */
  public HashMap<String, Node> getEntries() {
    HashMap<String, Node> returnEntries = new HashMap<String, Node>(entries);
    // returnEntries.put("..",parent);
    return returnEntries;
  }

  /**
   * Confirm that a given named entry exists in the Directory
   * 
   * @param name the name to look up.
   * @return true if this name exists in the directory; false, otherwise.
   */
  public boolean exists(String name) {
    if (name.equals("..") || name.equals(".")) {
      return true;
    } else {
      return entries.containsKey(name);
    }
  }

  /**
   * get a node by name; a fast search. Returns null upon failure.
   * 
   * @param name the name to look up
   * @return a node, or null if the name is not found
   */
  public Node getNodeByName(String name) {
    if (name.equals("..")) {
      return this.parent;
    } else if (name.equals(".")) {
      return this;
    } else {
      return entries.get(name);
    }
  }

  /**
   * Get the parent of this directory.
   * 
   * @return the Directory. Equal to the instance if the root of the file
   *         system.
   */

  public Directory getParent() {
    return parent;
  }


  /**
   * get Name by Node -- not available for general use at this time -- looks up
   * the node in the directory. A linear search in effect, so (relatively) slow.
   * 
   * @param node The node to find.
   * @return The name, or null if the node is not found.
   */
  protected String getNameByNode(Node node) {
    if (node == null) {
      return null;
    }
    assert (entries.containsValue(node));
    Iterator<String> nameItr = entries.keySet().iterator();
    while (nameItr.hasNext()) {
      String name = nameItr.next();
      if (entries.get(name) == node) {
        return name;
      }
    }
    return null;
  }


  /**
   * Get a new Directory object.
   * 
   * @param parent The directory's parent. All directories must have a parent
   *        except the root of a filesystem.
   */
  protected Directory(Directory parent) {
    if (null == parent) {
      this.parent = this;
    } else {
      this.parent = parent;
    }
    this.entries = new HashMap<String, Node>();
  }

  /**
   * setParent -- set the parent reference for this Directory object a null
   * parent implies that this is the root of this particular file system
   * (semantics enforced by FileSystem)
   * 
   * @param parent the parent directory
   */
  protected void setParent(Directory parent) {
    this.parent = parent;
  }

  /**
   * Add an entry to the directory, associating a name with a Node
   * 
   * @param node the node in question (data)
   * @param name the name (key)
   * @return
   */
  protected FSResultType addNode(Node node, String name) {
    if (name.isEmpty()) {
      return FSResultType.EmptyString;
    } else if (name.equals("..") || name.equals(".")) {
      return FSResultType.AlreadyExists;
    } else if (entries.containsKey(name)) {
      return FSResultType.AlreadyExists;
    } else {
      entries.put(name, node);
      return FSResultType.Success;
    }
  }

  /**
   * Delete a node by name.
   * 
   * @param name the entry to delete.
   * @return any error that may occur (e.g. the directory is not empty) or
   *         FSResultType.Success upon success
   */
  protected FSResultType deleteNodeByName(String name) {
    return unlinkNodeByNameWorker(name, true);
  }

  /**
   * Unlink a node by name. NOTE: Does not check if directories are empty before
   * removing them.
   * 
   * @param name name of the entry to delete
   * @return any error that may occur, or FSResultType.Success upon success.
   */
  protected FSResultType unlinkNodeByName(String name) {
    return unlinkNodeByNameWorker(name, false);
  }

  /**
   * Private worker function: unlink Node By name
   * 
   * @param name
   * @param checkEmpty
   * @return any error that may occur, or FSResultType upon success.
   */
  private FSResultType unlinkNodeByNameWorker(String name, boolean checkEmpty) {
    if (name.isEmpty()) {
      return FSResultType.EmptyString;
    } else if (entries.containsKey(name)) {
      Node node = entries.get(name);
      if (node.isDirectory()) {
        Directory dir = (Directory) node;
        if (checkEmpty) {
          if (!dir.entries.isEmpty()) {
            return FSResultType.DirectoryNotEmpty;
          }
        }
      }
      entries.remove(name);
      return FSResultType.Success;
    } else {
      return FSResultType.NotFound;
    }
  }


  /**
   * Rename a File or Directory with a Directory. This will not move a directory
   * from one place to another; this merely changes the name by which a Node
   * (File or Directory) is known within a Directory.
   * 
   * @param oldName The old name.
   * @param newName The new name.
   * @return FSResultType.NotFound if the entry is not found;
   *         FSResultType.Success otherwise
   */
  public FSResultType rename(String oldName, String newName) {
    assert (null != oldName);
    assert (null != newName);
    if (entries.containsKey(oldName)) {
      Node node = entries.get(oldName);
      entries.remove(oldName);
      entries.put(newName, node);
      return FSResultType.Success;
    } else {
      return FSResultType.NotFound;
    }
  }

}
