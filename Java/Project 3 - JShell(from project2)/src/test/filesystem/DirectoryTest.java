/**
 * 
 */

package filesystem;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author mwb
 *
 */
public class DirectoryTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for {@link FileSystem.Directory#getEntries()}.
   */
  @Test
  public void testGetEntriesSimple() {
    Directory dir = new Directory(null);
    String[] fileNames = {"Sam", "Pat", "Jamie", "Jean"};
    for (String name : fileNames) {
      dir.addNode(new File(), name);
    }
    HashMap<String, Node> entries = dir.getEntries();
    for (String name : fileNames) {
      assertTrue(entries.containsKey(name));
    }
  }

  /**
   * Test method for {@link FileSystem.Directory#exists(java.lang.String)}.
   */
  @Test
  public void testExistsSimple() {
    Directory dir = new Directory(null);
    File f1 = new File();
    String f1Name = "foo.txt";
    dir.addNode(f1, f1Name);
    Node f1Found = dir.getNodeByName(f1Name);
    assertSame(f1, f1Found);
  }

  /**
   * Test method for {@link FileSystem.Directory#exists(java.lang.String)}.
   */
  @Test
  public void testExistsAmongMany() {
    Directory dir = new Directory(null);
    File f1 = new File();
    String f1Name = "foo.txt";
    dir.addNode(f1, f1Name);
    String fOtherNames[] =
        {"Rose", "Shirlie", "mancuso", "jixacz.txt", "bogus.jar",
            "febrileimaginingings et cetera", "nostril", "XYZZY"};
    for (int i = 0; i < fOtherNames.length; i++) {
      File fOther = new File();
      dir.addNode(fOther, fOtherNames[i]);
    }
    Node f1Found = dir.getNodeByName(f1Name);
    assertSame(f1, f1Found);
  }



  /**
   * Test method for {@link FileSystem.Directory#deleteNode(FileSystem.Node)}.
   */
  @Test
  public void testDeleteNode() {
    Directory dir = new Directory(null);
    File f = new File();
    String fileName = "Akash Chandresekhar";
    dir.addNode(f, fileName);
    dir.deleteNodeByName(fileName);
    Node shouldBeNullNode = dir.getNodeByName(fileName);
    assertEquals(null, shouldBeNullNode);
  }

  /**
   * Test method for
   * {@link FileSystem.Directory#rename(FileSystem.Node, java.lang.String)}.
   */
  @Test
  public void testRename() {
    Directory dir = new Directory(null);
    File f = new File();
    String fileNameOld = "Seamus";
    String fileNameNew = "Annabelle Lee";
    dir.addNode(f, fileNameOld);
    FSResultType result = dir.rename(fileNameOld, fileNameNew);
    assertEquals(FSResultType.Success, result);
    Node nodeFound = dir.getNodeByName(fileNameNew);
    assertEquals(f, nodeFound);
    assertEquals(null, dir.getNodeByName(fileNameOld));
  }

  @Test
  public void testRenameDoesNotExist() {
    Directory dir = new Directory(null);
    String fileNameOld = "Marcus";
    String fileNameNew = "Torvald";
    FSResultType result = dir.rename(fileNameOld, fileNameNew);
    assertEquals(FSResultType.NotFound, result);
  }

  @Test
  public void testGetNameByNodeWithFile() {
    Directory dir = new Directory(null);
    String fileName = "AppleCinnamon";
    File fileNode = new File();
    dir.addNode(fileNode, fileName);
    assertEquals("getNameByNode must return correct filename", fileName,
        dir.getNameByNode(fileNode));
  }

  @Test
  public void testGetNameByNodeWithDirectory() {
    Directory root = new Directory(null);
    Directory child = new Directory(root);
    String childDirectoryName = "MarcusSamuelsson";
    root.addNode(child, childDirectoryName);
    assertEquals("getNameByNode must return correct directory name",
        childDirectoryName, root.getNameByNode(child));
  }

}
