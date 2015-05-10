package interpreter.result;

/*
 * All classes return Result type in order to talk to filesystem and tell if
 * there are errors
 */
public class Result {
  private boolean isError;
  private String message;

  protected Result(boolean isError) {
    this.isError = isError;
  }

  public boolean isError() {
    return this.isError;
  }

}
