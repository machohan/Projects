package filesystem;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import filesystem.FileSystem;
import filesystem.FileSystemResolveResult;
import filesystem.Node;
import filesystem.Path;
import filesystem.PathException;

public class PathTest {
  FileSystem fs;
  private final static String USR = "/usr";
  private final static String USRLOCAL = "/usr/local";
  private final static String USRLOCALCONFIG = "/usr/local/config";
  private final static String USRLOCALBIN = "/usr/local/bin";
  private final static String INITFILE = "/initfile";
  private final static String CONFIGFILE = USRLOCALCONFIG + "/configfile";
  private final static String CONFIGFILEDATA = "blah configuration blah";

  @Before
  public void setUp() throws Exception {
    fs = new FileSystem();
    fs.mkdir(USR);
    fs.mkdir(USRLOCAL);
    fs.mkdir(USRLOCALCONFIG);
    fs.mkdir(USRLOCALBIN);
    fs.createFile(INITFILE);
    fs.createFile(CONFIGFILE);
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testPathBasic() {
    String pathString = "/made/up/path";
    Path path = new Path(fs, pathString);
    assertEquals(pathString, path.toString());
  }

  @Test
  public void testBaseNameStatic() {
    assertEquals("fishbein", Path.baseName("/usr/local/fishbein"));
  }

  @Test
  public void testDirNameStatic() {
    assertEquals("/usr/local", Path.dirName("/usr/local/fishbein"));
  }

  @Test
  public void testExistsRoot() {
    Path path = new Path(fs, "/");
    assertTrue(path.exists());
  }

  @Test
  public void testExistsDot() {
    Path path = new Path(fs, ".");
    assertTrue(path.exists());
  }

  @Test
  public void testExistsUsrLocalRelpath() {
    Path path = new Path(fs, "usr/local");
    assertTrue(path.exists());

  }
  
  @Test
  public void testIsDirectoryTrue() throws PathException {
    Path path = new Path(fs, USRLOCAL);
    assertTrue(path.isDirectory());
  }
  
  @Test
  public void testIsDirectoryFalse() throws PathException {
    Path path = new Path(fs, CONFIGFILE);
    assertFalse(path.isDirectory());
  }
  
  @Test
  public void testIsFileTrue() throws PathException {
    Path path = new Path(fs,CONFIGFILE);
    assertTrue(path.isFile());
  }
  
  @Test
  public void testIsFileFalse() throws PathException {
    Path path = new Path(fs,USRLOCAL);
    assertFalse(path.isFile());
  }


  @Test
  public void testGetNodeRoot() throws PathException {
    Path path = new Path(fs, "/");
    Node node = path.getNode();
    FileSystemResolveResult result = fs.resolvePath("/");
    assertEquals(result.node, node);
  }

  @Test(expected = PathException.class)
  public void testGetNodeBogus() throws PathException {
    Path path = new Path(fs, "bogusPathDoesNotExist"); // throws Exception
    Node node = path.getNode();
    node.isFile(); // we shouldn't get here.
    fail("we should have thrown a PathException");

  }
  
  @Test
  public void testGetFile() throws PathException {
    Path path = new Path(fs, CONFIGFILE);
    File configFile = path.getFile();
    configFile.setContent(CONFIGFILEDATA);
    Path path2 = new Path(fs, CONFIGFILE);
    File configFile2 = path2.getFile();
    assertEquals(CONFIGFILEDATA,configFile2.getContent());
  }

  @Test
  public void testDeleteSimple() throws PathException {
    Path path = new Path(fs, CONFIGFILE);
    path.delete();
    assertFalse(path.exists());
  }

  @Test(expected = PathException.class)
  public void testDeleteDirectoryNotEmpty() throws PathException {
    Path path = new Path(fs, USR);
    path.delete(); // should throw an exception
  }

  @Test
  public void testWithSuffixSimple() {
    Path foo = new Path(fs, "foo");
    Path fooBar = foo.withSuffix("bar");
    assertEquals("foo/bar", fooBar.toString());
  }
  
  

}
