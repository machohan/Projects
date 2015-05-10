package filesystem;

/**
 * An extremely simple and lightweight representation of a file.
 * 
 * @author mwb
 *
 */

public class File extends Node {
  private StringBuilder content;

  /**
   * Constructor.
   */
  public File() {
    content = new StringBuilder();
  }

  /**
   * return the content of this file.
   * 
   * @return A string representing the file's content.
   */
  public String getContent() {
    return this.content.toString();
  }

  /**
   * set the content of the file.
   * 
   * @param contentToSet A string of any size
   */
  public void setContent(String contentToSet) {
    this.content = new StringBuilder(contentToSet);
  }

  /**
   * Append to the content of this file.
   * 
   * @param contentToAppend A string of any size
   */
  public void appendToContent(String contentToAppend) {
    this.content.append(contentToAppend);
  }
}
