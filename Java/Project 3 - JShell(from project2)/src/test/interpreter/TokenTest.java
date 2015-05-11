package interpreter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import interpreter.core.Token;

import java.util.ArrayList;

public class TokenTest {

  String path;
  ArrayList<Token> expectedOutput = new ArrayList<Token>();

  @Before
  public void setup() {
    path = "mkdir directory1 \" my folder 1\"  Ashely Ben \" my folder 2 \" ";

    expectedOutput.add(new Token("mkdir"));
    expectedOutput.add(new Token("directory1"));
    expectedOutput.add(new Token(" my folder 1"));
    expectedOutput.add(new Token("Ashely"));
    expectedOutput.add(new Token("Ben"));
    expectedOutput.add(new Token(" my folder 2 "));
  }

  @Test
  public void testTokenize() {
    assertEquals(expectedOutput.size(), Token.tokenize(path).size());

  }

}
