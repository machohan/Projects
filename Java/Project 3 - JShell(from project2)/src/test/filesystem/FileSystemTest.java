package filesystem;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class FileSystemTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testResolveAbsolutePathRootSimple() {
    FileSystem fs = new FileSystem();
    FileSystemResolveResult result = fs.resolvePath("/");
    assertEquals("/ must resolve with Success", 
          FSResultType.Success, result.status);
    Node node = result.node;
    assertNotEquals("/ should not resolve to null", null, node);
  }

  @Test
  public void testResolveAbsolutePathRootParent() {
    FileSystem fs = new FileSystem();
    FileSystemResolveResult result;
    result = fs.resolvePath("/");
    assertEquals("/ must resolve with Success", 
          FSResultType.Success, result.status);
    Node rootNode = result.node;
    result = fs.resolvePath("/..");
    assertEquals("/.. must resolve with Success", 
        FSResultType.Success, result.status);
    Node rootParent = result.node;
    assertEquals
      ("'/' and '/..' should not resolve to different things", 
              rootNode, rootParent);
    assertTrue("'/' must be a Directory", rootNode.isDirectory());
  }

  @Test
  public void testResolveRelativePathCwdIsRoot() {
    FileSystem fs = new FileSystem();
    FileSystemResolveResult result = fs.resolvePath("..");
    assertEquals(FSResultType.Success, result.status);
    Node dotdot = result.node;
    assertNotEquals("'..' should not resolve to null", null, dotdot);
  }

  @Test
  public void testTestRelativePathCwdIsRoot() {
    FileSystem fs = new FileSystem();
    FileSystemResolveResult result = fs.resolvePath("..");
    assertEquals(FSResultType.Success, result.status);
    Node dotdot = result.node;
    result = fs.resolvePath("/");
    assertEquals(FSResultType.Success, result.status);
    Node slash = result.node;
    assertEquals("'..' should resolve to the same as '/'", dotdot, slash);
  }

  @Test
  public void testResolveMediumAbsolutePath() {
    FileSystem fs = new FileSystem();
    FileSystemResolveResult result = fs.resolvePath("/");
    assertEquals
      ("/ must resolve with success", FSResultType.Success, result.status);
    Node rootNode = result.node;
    assertNotEquals("'/' should not resolve to null", null, rootNode);
    Directory rootDirectory = (Directory) rootNode;
    Directory fooDir = new Directory(rootDirectory);
    assertSame(FSResultType.Success, rootDirectory.addNode(fooDir, "foo"));
    Directory barDir = new Directory(fooDir);
    fooDir.addNode(barDir, "bar");
    result = fs.resolvePath("/foo/bar");
    assertEquals("/foo/bar must resolve after we've added it", 
        FSResultType.Success, result.status);
    Node barNodeFound = result.node;
    assertEquals(barDir, barNodeFound);
  }

  @Test
  public void testMkdirSimple() {
    FileSystem fs = new FileSystem();
    FileSystemResult result = fs.mkdir("/usr");
    assertEquals("mkdir(/usr) must be successful",
        FSResultType.Success, result.status);
    result = fs.mkdir("/usr/local");
    assertEquals("mkdir(/usr/local) must be successful",
        FSResultType.Success, result.status);
  }

  @Test
  public void testMkdirAllSortsOfBadCharacters() {
    FileSystem fs = new FileSystem();
    // note that we can't test for '/' as mkdir will
    // (correctly) interpret this as a path component
    // and strip it.
    String bad = "!@$&*()?:[]\"<>'`|={}\\,; \n\t\b\f\r";
    char[] badChars = bad.toCharArray();
    for (char c : badChars) {
      StringBuilder badSb = new StringBuilder();
      badSb.append("dir_").append(c);
      StringBuilder assertMessageSb = new StringBuilder();
      assertMessageSb.append("mkdir(");
      assertMessageSb.append(badSb).append(") should get an error");
      FileSystemResult result = fs.mkdir(badSb.toString());
      assertEquals(assertMessageSb.toString(),
          FSResultType.BadCharacterInPath, result.status);
    }
  }

  @Test
  public void testMkdirSimpleCollision() {
    FileSystem fs = new FileSystem();
    FileSystemResult result = fs.mkdir("/usr");
    assertEquals("mkdir(/usr) must be successful",
        FSResultType.Success, result.status);
    result = fs.mkdir("/usr");
    assertEquals("second mkdir(/usr) must fail as collision",
        FSResultType.AlreadyExists,
        result.status);
  }

  @Test
  public void testMkdirSimpleRelative() {
    FileSystem fs = new FileSystem();
    FileSystemResult result = fs.mkdir("usr");
    assertEquals("mkdir(usr) must be successful", 
        FSResultType.Success, result.status);
    result = fs.mkdir("usr/local");
    assertEquals("mkdir(usr/local) must be successful",
        FSResultType.Success, result.status);
  }

  @Test
  public void testMkdirDoubleDotRelative() {
    FileSystem fs = new FileSystem();
    FileSystemResult result = fs.mkdir("usr");
    assertEquals("mkdir(usr) must be successful",
        FSResultType.Success, result.status);
    result = fs.mkdir("usr/../opt");
    assertEquals("mkdir(usr/../opt) must be successful",
        FSResultType.Success, result.status);
    FileSystemResolveResult resolveResult = fs.resolvePath("/opt");
    assertEquals("Must be able to find /opt after mkdir(usr/../opt)",
        FSResultType.Success, resolveResult.status);
  }

  @Test
  public void testGetCwdSimple() {
    FileSystem fs = new FileSystem();
    assertEquals("cwd of root must be '/'", "/", fs.getCwd());
  }

  @Test
  public void testSetCwdGetCwdSimple() {
    FileSystem fs = new FileSystem();
    FileSystemResult result;
    fs.mkdir("usr");
    result = fs.setCwd("/usr");
    assertEquals("Must be able to cd to /usr after making /usr", result.status,
        FSResultType.Success);
    String cwd = fs.getCwd();
    assertEquals("cwd must be /usr after cd to /usr", "/usr", cwd);
  }

  @Test
  public void testCdRelativeParentInRoot() {
    FileSystem fs = new FileSystem();
    FileSystemResult result;
    result = fs.setCwd("..");
    assertEquals("Must be able to cd .. in /", 
        FSResultType.Success, result.status);
    assertEquals("cwd after cd .. in / must be '/'", "/", fs.getCwd());
  }

  @Test
  public void testMakeUsrLocalAbsoluteAndCdAbsolute() {
    FileSystem fs = new FileSystem();
    FileSystemResult result;
    result = fs.mkdir("/usr");
    result = fs.mkdir("/usr/local");
    assertEquals("mkdir /usr/local must be successful", 
        FSResultType.Success, result.status);
    result = fs.setCwd("/usr/local");
    assertEquals("cd to /usr/local must be successful", 
        FSResultType.Success, result.status);
    String cwd = fs.getCwd();
    assertEquals
      ("cwd after cd to /usr/local must be /usr/local","/usr/local", cwd);
  }

  @Test
  public void testCreateAndReadFileSimple() {
    FileSystem fs = new FileSystem();
    final String fileName = "happy_txt";
    FileSystemResult result;
    result = fs.createFile(fileName);
    assertEquals("createFile " + fileName + " must be successful", 
        FSResultType.Success, result.status);
    FileSystemStringResult readFileResult;
    readFileResult = fs.readFile(fileName);
    assertEquals("read of newly created file must be successful", 
        FSResultType.Success, readFileResult.status);
    assertNotEquals("content field for newly created file must not be null",
        readFileResult.content, null);
    assertEquals("content of newly created file must be empty string", "",
        readFileResult.content);

  }

  @Test
  public void testCreateWriteAndReadFileSimple() {
    FileSystem fs = new FileSystem();
    final String fileName = "medium_txt";
    final String fileData = "The quick red fox jumped over the lazy brown dog";
    FileSystemResult result;
    result = fs.createFile(fileName);
    assertEquals("createFile " + fileName + " must be successful", 
        FSResultType.Success, result.status);
    result = fs.writeFile(fileName, fileData);
    FileSystemStringResult readFileResult;
    readFileResult = fs.readFile(fileName);
    assertEquals("read of newly created file must be successful", 
        FSResultType.Success, readFileResult.status);
    assertNotEquals("content field for newly created file must not be null",
        readFileResult.content, null);
    assertEquals("content of newly created file must be " + fileData, fileData,
        readFileResult.content);

  }

  // tests a specific error encountered in the application layer
  // to demonstrate it's not a problem at the FileSystem layer
  @Test
  public void testCreateDirFileCdFile() {
    FileSystem fs = new FileSystem();
    final String fileName = "file1_text";
    final String path1 = "folder1";
    final String path2 = "folder2";
    FileSystemResult result;
    result = fs.mkdir(path1);
    result = fs.setCwd(path1);
    result = fs.mkdir(path2);
    result = fs.setCwd(path2);
    result = fs.createFile(fileName);
    result = fs.writeFile(fileName, "Here is some random data");
    assertEquals("File creation and write must succeed", 
        FSResultType.Success, result.status);
    result = fs.setCwd("../../..");
    String path2File = path1 + "/" + path2 + "/" + fileName;
    result = fs.setCwd(path2File);
    assertEquals("CD to a file must fail with NotADirectory", 
        FSResultType.NotADirectory, result.status);
  }

  @Test
  public void testIsSubPathOrEqual() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/usr");
    fs.mkdir("/usr/local");
    fs.mkdir("/usr/local/share");
    fs.mkdir("/usr/local/share/bin");
    FileSystemResolveResult resolveResult = fs.resolvePath("/usr/local");
    Node usrLocalNode = resolveResult.node;
    resolveResult = fs.resolvePath("/usr/local/share/bin");
    Directory usrLocalShareBinDir = (Directory) resolveResult.node;
    assertTrue(fs.isSubPathOrEqual(usrLocalShareBinDir, usrLocalShareBinDir));
    assertTrue(fs.isSubPathOrEqual(usrLocalNode, usrLocalShareBinDir));
  }

  @Test
  public void testMoveAsRename() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/usr");
    FileSystemResult result = fs.move("/usr", "/user");
    assertEquals("move() of /usr to /user should succeed", 
        FSResultType.Success, result.status);
    FileSystemResolveResult resolveResult = fs.resolvePath("/user");
    assertEquals("/usr should now be /user", 
        FSResultType.Success, resolveResult.status);
    resolveResult = fs.resolvePath("/usr");
    assertEquals("/usr should no longer exist", 
        FSResultType.NotFound, resolveResult.status);
  }

  @Test
  public void testMoveAsMoveSimple() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/usr");
    fs.mkdir("/usr/local");
    fs.mkdir("/usr/local/share");
    fs.mkdir("/bin");
    FileSystemResolveResult resolveResult = fs.resolvePath("/bin");
    Node binNode = resolveResult.node;
    FileSystemResult result = fs.move("/bin", "/usr/local");
    assertEquals("mv /bin /usr/local must succeed", 
        FSResultType.Success, result.status);
    resolveResult = fs.resolvePath("/usr/local/bin");
    assertEquals("after mv, /usr/local/bin must exist", 
        FSResultType.Success, resolveResult.status);
    assertEquals("after mv, /usr/local/bin must be same node as original /bin", 
        binNode,resolveResult.node);
    resolveResult = fs.resolvePath("/bin");
    assertEquals("after mv, original /bin should be gone", 
        FSResultType.NotFound, resolveResult.status);
  }

  @Test
  public void testMoveAsMoveAndRename() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/usr");
    fs.mkdir("/usr/local");
    fs.mkdir("/binaries");
    FileSystemResolveResult resolveResult = fs.resolvePath("/binaries");
    Node binariesNode = resolveResult.node;
    FileSystemResult result = fs.move("/binaries", "/usr/local/bin");
    assertEquals("mv of /binaries to /usr/local/bin should succeed", 
        FSResultType.Success, result.status);
    resolveResult = fs.resolvePath("/usr/local/bin");
    assertEquals("after mv, /usr/local/bin should now exist", 
        FSResultType.Success, resolveResult.status);
    assertEquals("after mv, /usr/local/bin must be same node as old /binaries", 
        binariesNode, resolveResult.node);
  }

  @Test
  public void testMoveDirectoryToFile() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/usr");
    fs.createFile("/usr/fish_text");
    fs.mkdir("/usr/local");
    FileSystemResult result = fs.move("/usr/local", "/usr/fish_text");
    assertEquals("moving a directory to a file should fail as not a directory",
        FSResultType.NotADirectory, result.status);
    // this should FAIL

  }

  @Test
  public void testMoveAsRenameFile() {
    FileSystem fs = new FileSystem();
    fs.createFile("George");
    FileSystemResult result = fs.move("George", "Sally");
    assertEquals("Simple move of George to Sally should succeed", 
        FSResultType.Success, result.status);
    FileSystemResolveResult resolveResult = fs.resolvePath("George");
    assertEquals("After rename of George to Sally, George should not be found",
        FSResultType.NotFound, resolveResult.status);
    resolveResult = fs.resolvePath("Sally");
    assertEquals("After rename of George to Sally, Sally should be found", 
        FSResultType.Success, resolveResult.status);
  }

  @Test
  public void testMoveAsMoveAndRenameFile() {
    FileSystem fs = new FileSystem();
    fs.createFile("MarthaTxt");
    fs.mkdir("TheLane");
    fs.mkdir("TheLane/TheHouse");
    FileSystemResult result = fs.setCwd("TheLane/TheHouse");
    assertEquals(FSResultType.Success, result.status);
    result = fs.move("/MarthaTxt", "./LadyMartha");
    assertEquals(FSResultType.Success, result.status);
    FileSystemResolveResult resolveResult 
        = fs.resolvePath("/TheLane/TheHouse/LadyMartha");
    assertEquals("Martha moved and name changed should exist", 
        FSResultType.Success, resolveResult.status);

  }

  @Test
  public void testMoveSubtreeIntoItself() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/usr");
    fs.mkdir("/usr/local");
    fs.mkdir("/usr/local/share");
    FileSystemResult result = fs.move("/usr", "/usr/local/share");
    assertEquals("moving a directory to its own sub-directory should fail",
        FSResultType.IllegalOperation, result.status);
  }

  @Test
  public void testGetDirectoryEntries() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/usr");
    String names[] = {"George", "Paul", "Ringo", "John"};
    for (String name : names) {
      fs.createFile("/usr/" + name);
    }
    FileSystemDirectoryResult directoryResult = fs.getNamesInDirectory("/usr");
    assertEquals("getting names from directory /usr should succeed", 
        FSResultType.Success, directoryResult.status);


    for (String name : names) {
      assertEquals("Directory /usr must contain name " + name, true,
          directoryResult.entries.contains(name));
    }
  }

  @Test
  public void testDeleteFileSimple() {
    FileSystem fs = new FileSystem();
    final String jonas = "JoeJonas.txt";
    fs.createFile(jonas);
    FileSystemResult result = fs.delete(jonas);
    assertEquals("simple delete() of file must succeed", 
        FSResultType.Success, result.status);
    FileSystemResolveResult resolveResult = fs.resolvePath(jonas);
    assertEquals("after delete(), file must be gone", 
        FSResultType.NotFound, resolveResult.status);
  }

  @Test
  public void testDeleteDoesntExist() {
    FileSystem fs = new FileSystem();
    final String utopia = "TheLandWhereNoOneAges";
    FileSystemResult result = fs.delete(utopia);
    assertEquals("delete() of nonexistent object must fail", 
        FSResultType.NotFound, result.status);
  }

  @Test
  public void testDeleteDirectorySimple() {
    FileSystem fs = new FileSystem();
    final String usr = "usr";
    fs.mkdir(usr);
    FileSystemResult result = fs.delete(usr);
    assertEquals("simple delete() of directory must succeed", 
        FSResultType.Success, result.status);
    FileSystemResolveResult resolveResult = fs.resolvePath(usr);
    assertEquals("after delete(), directory must be gone", 
        FSResultType.NotFound, resolveResult.status);
  }

  @Test
  public void testDeleteDirectoryNonEmpty() {
    FileSystem fs = new FileSystem();
    final String usr = "usr";
    final String usrLocal = "usr/local";
    fs.mkdir(usr);
    fs.mkdir(usrLocal);
    FileSystemResult result = fs.delete(usr);
    assertEquals("delete() of non-empty directory must fail", 
        FSResultType.DirectoryNotEmpty, result.status);
    FileSystemResolveResult resolveResult = fs.resolvePath(usr);
    assertEquals("after delete() failure, directory must remain", 
        FSResultType.Success, resolveResult.status);
  }

  @Test
  public void testBasenameSimple() {
    assertEquals("baseName(/usr/local) of must be usr", "local", 
        FileSystem.baseName("/usr/local"));

  }

  @Test
  public void testBaseNameEmpty() {
    assertEquals("baseName() of empty string must be empty string", "", 
        FileSystem.baseName(""));
  }

  @Test
  public void testBaseNameNoSlash() {
    assertEquals("baseName(usr) must be usr", "usr", 
        FileSystem.baseName("usr"));
  }

  @Test
  public void testBaseNameEmptyAfterSlash() {
    assertEquals("baseName(usr/) must be usr", "usr", 
        FileSystem.baseName("usr/"));
  }

  @Test
  public void testBaseNameAllSlashes() {
    assertEquals("baseName(////) must be /", "/", 
        FileSystem.baseName("////"));
  }

  @Test
  public void testBaseNameManyTerminalSlashes() {
    assertEquals("basename(usr/////) must be usr", "usr", 
        FileSystem.baseName("usr/////"));
  }

  // here's an experiment. tests are easy to write, but failure of a given
  // test short-circuits the test process, and successive cases are left
  // undone. If I had time, I might try using the @Parameter attribute
  // instead: https://github.com/junit-team/junit/wiki/Parameterized-tests
  @Test
  public void testDirNameManyCases() {
    String[][] testPairs =
        {
        /* input, expected output */
        {"", "."}, {".", "."}, {"..", "."}, {"usr", "."}, {"/usr", "/"}, 
        {"/usr/local", "/usr"}, {"usr/local", "usr"}, {"../..", ".."}, 
        {"/..", "/"}};
    for (String pair[] : testPairs) {
      String message = 
          String.format("dirName(\"%s\") must return \"%s\"", pair[0], pair[1]);
      assertEquals(message, pair[1], FileSystem.dirName(pair[0]));
    }
  }

  @Test
  public void testDirNameEmptyString() {
    assertEquals("dirname('') must be '.'", ".", FileSystem.dirName(""));
  }

  @Test
  public void testDirNameDot() {
    assertEquals("dirname('.') must be '.'", ".", FileSystem.dirName("."));
  }

  @Test
  public void testDirNameDotDot() {
    assertEquals("dirName('..') must be '.'", ".", FileSystem.dirName(".."));
  }

  @Test
  public void testDirNameOneBareName() {
    assertEquals("dirName('usr') must be '.'", ".", FileSystem.dirName("usr"));
  }


}
