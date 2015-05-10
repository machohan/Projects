package interpreter.core;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Token class encapsulates command-line tokens after parsing and provides
 * a simple quoting Tokenizer for breaking up space-delimited command lines.
 * @author mwb
 *
 */
public class Token {

  private String body;

  /**
   * Get the content of this token
   * @return the actual text of this token
   */
  public String getBody() {
    return body;
  }

  /**
   * The constructor makes a Token; presently, it only takes a String
   * with the actual text of this token
   * @param body
   */
  public Token(String body) {
    this.body = body;
  }

  /**
   * Nice printable format of a token
   */
  @Override
  public String toString() {
    if (null == body) {
      return "";
    } else {
      return body;
    }
  }

  /**
   * The tokenizer breaks up an input line on whitespace. Characters between
   * double quotes are taken as literal, including whitespace. E.g.:
   * "hello there" you are a fine "fellow" will be parsed into
   * [hello there] [you] [are] [a] [fine] [fellow]
   * @param input
   * @return an ArrayList of Tokens.
   */
  public static ArrayList<Token> tokenize(String input) {
    ArrayList<Token> result = new ArrayList<Token>();
    boolean inQuote = false;
    boolean inWord = false;
    StringBuilder word = new StringBuilder();

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c == '"') {
        if (!inQuote & !inWord) {
          inWord = true;
          word = new StringBuilder();
        }
        inQuote = !inQuote;
      } else if (inQuote) {
        word.append(c);
      } else {
        if (Character.isWhitespace(c)) {
          if (inWord) {
            // hit whitespace while in a word so we've ended that word
            if (word.length() > 0) {
              result.add(new Token(word.toString()));
              word = null;
            }
            inWord = false;
          }
        } else {
          if (!inWord) {
            // hit NON-whitespace while NOT in a word, so start a new word
            inWord = true;
            word = new StringBuilder();
          }
          word.append(c);
        }
      } // end of non-quoted characters
    } // end of loop over string
    if (word != null && word.length() > 0) {
      result.add(new Token(word.toString()));
    }
    return result;
  }

}