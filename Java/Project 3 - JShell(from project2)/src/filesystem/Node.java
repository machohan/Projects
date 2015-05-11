package filesystem;

/*
 * Allows for the creation of files and directories type in our tree
 */


public abstract class Node {

  protected Node() {}

  public boolean isFile() {
    return this instanceof File;
  }

  public boolean isDirectory() {
    return this instanceof Directory;
  }

}
