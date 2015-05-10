/**
 * 
 */
package interpreter.core;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * 
 *         The Environment class encapsulates access to the Input and Output
 *         streams used by an application.
 * 
 * @author mwb
 * 
 *
 */
public class Environment {
  public final PrintStream err;
  public final InputStream in;
  public final PrintStream out;
  private boolean captureOut;
  private ByteArrayOutputStream captureOutStream;
  private boolean captureErr;
  private ByteArrayOutputStream captureErrStream;

  /**
   * Private constructor; internal to class and used by Environment.Builder
   * 
   * @param builder
   */
  private Environment(Builder builder) {
    this.err = builder.err;
    this.in = builder.in;
    this.out = builder.out;
    this.captureOut = builder.captureOut;
    this.captureOutStream = builder.captureOutStream;
    this.captureErr = builder.captureErr;
    this.captureErrStream = builder.captureErrStream;
  }

  /**
   * 
   * @return
   */
  /**
   * Report if the output has been captured.
   * 
   * @return true if output has been captured.
   */
  public boolean isOutputCaptured() {
    return this.captureOut;
  }

  /**
   * Get the output captured in a stream.
   * 
   * @return the String representation of the output if it has been captured;
   *         null if capturing is not enabled.
   */
  public String getCapturedOutput() {
    if (this.captureOut) {
      return this.captureOutStream.toString();
    } else {
      return null;
    }
  }

  /**
   * Report if the error stream has been captured
   * 
   * @return true if the error stream has been captured.
   */
  public boolean isErrorCaptured() {
    return this.captureErr;
  }

  /**
   * Get the contents of the error stream
   * 
   * @return the String representation of the output if it has been captured;
   *         null if it capturing has not been enabled.
   */
  public String getCapturedError() {
    if (this.captureErr) {
      return this.captureErrStream.toString();
    } else {
      return null;
    }
  }

  /**
   * The Environment.Builder roughly follows the Builder pattern, providing a
   * way to build an Environment either (1) completely from defaults (the
   * streams present in System) or (2) from an existing Environment. Then, the
   * user may customize the environment by specifying alternate streams, or by
   * specifying that output be captured for later retrieval.
   * 
   * @author mwb
   *
   */
  public static class Builder {

    private PrintStream err;
    private boolean errSet = false;
    private InputStream in;
    private boolean inSet = false;
    private PrintStream out;
    private boolean outSet = false;
    private boolean captureOut = false;
    private boolean captureErr = false;
    private ByteArrayOutputStream captureErrStream = null;
    private ByteArrayOutputStream captureOutStream = null;

    /**
     * Build up a collection of streams. The streams default to System.out,
     * System.err, and System.in.
     * 
     */
    public Builder() {};

    /**
     * Build up a collection of streams from an existing Environment.
     * 
     * @param env the Environment to clone.
     */
    public Builder(Environment env) {
      this.err = env.err;
      this.errSet = true;
      this.in = env.in;
      this.inSet = true;
      this.out = env.out;
      this.outSet = true;
      this.captureErrStream = env.captureErrStream;
      this.captureOutStream = env.captureOutStream;
    }

    /**
     * Specify an error stream.
     * 
     * @param err the PrintStream to use for error output.
     * @return the Builder under construction.
     */
    public Builder withErr(PrintStream err) {
      this.err = err;
      errSet = true;
      return this;
    }

    /**
     * Specify an input stream
     * 
     * @param in the InputStream to use for standard input.
     * @return the Builder under construction.
     */
    public Builder withIn(InputStream in) {
      this.in = in;
      inSet = true;
      return this;
    }

    /**
     * Specify an output stream
     * 
     * @param out the PrintSTream to use for standard output
     * @return the Builder under construction.
     */
    public Builder withOut(PrintStream out) {
      this.out = out;
      outSet = true;
      return this;
    }

    /**
     * Specify that the Environment under construction capture its standard
     * output for later retrieval as a String.
     * 
     * @return the Builder under construction.
     */
    public Builder withOutputCapture() {
      this.captureOut = true;
      this.captureOutStream = new ByteArrayOutputStream();
      this.out = new PrintStream(captureOutStream);
      outSet = true;
      return this;
    }

    /**
     * Specify that the Environment under construction capture its error output
     * for later retrieval as a String.
     * 
     * @return the Builder under construction
     */
    public Builder withErrorCapture() {
      this.captureErr = true;
      this.captureErrStream = new ByteArrayOutputStream();
      this.err = new PrintStream(this.captureErrStream);
      errSet = true;
      return this;
    }

    /**
     * Specify that the Environment under construction take input from a String
     * instead of the default (standard input) or any previously- specified
     * input stream.
     * 
     * @param input The String containing the canned input.
     * @return The Builder under construction.
     */
    public Builder withCannedInput(String input) {
      this.in = new ByteArrayInputStream(input.getBytes());
      this.inSet = true;
      return this;
    }

    /**
     * Build the Environment as specified.
     * 
     * @return the new Environment.
     */
    public Environment build() {
      supplyDefaultsAndValidate();
      return new Environment(this);
    }

    private void supplyDefaultsAndValidate() {
      if (!errSet) {
        err = System.err;
      }
      if (!inSet) {
        in = System.in;
      }
      if (!outSet) {
        out = System.out;
      }
      if (err == null)
        throw new NullPointerException();
      if (in == null)
        throw new NullPointerException();
      if (out == null)
        throw new NullPointerException();
    }
  }
}
