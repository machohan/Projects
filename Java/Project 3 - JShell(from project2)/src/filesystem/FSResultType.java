package filesystem;

/*
 * Types of error that are possible
 */


public enum FSResultType {
  Success("Success."), 
  NotFound("File or directory not found"), 
  AlreadyExists("File or directory already exists"), 
  DirectoryNotEmpty("Directory is not empty."), 
  NotADirectory("Not a directory."), 
  NotAFile("Not a file."), 
  EmptyString("Empty string is invalid."), 
  BadCharacterInPath("Bad character in path."), 
  NotImplemented("filesystem method not implemented"), 
  IllegalOperation("Illegal operation");

  private final String text;

  private FSResultType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return this.text;
  }

}
